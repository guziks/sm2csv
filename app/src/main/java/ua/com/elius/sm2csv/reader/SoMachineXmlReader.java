package ua.com.elius.sm2csv.reader;

import me.tatarka.parsnip.Xml;
import me.tatarka.parsnip.XmlAdapter;
import ua.com.elius.sm2csv.model.symbolconfig.Symbolconfiguration;
import ua.com.elius.sm2csv.model.symbolconfig.node.VarNode;
import ua.com.elius.sm2csv.model.symbolconfig.type.*;
import ua.com.elius.sm2csv.record.SoMachineRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SoMachineXmlReader {

    public static final String NAME_DIV = "_"; // divider for nested names
    public static final String COMMENT_DIV = " | "; // divider for nested comments

    public static final String TYPECLASS_BOOL = "Bool";
    public static final String TYPECLASS_BYTE = "Byte";
    public static final String TYPECLASS_WORD = "Word";
    public static final String TYPECLASS_DWORD = "DWord";
    public static final String TYPECLASS_LWORD = "LWord";
    public static final String TYPECLASS_SINT = "SInt";
    public static final String TYPECLASS_USINT = "USInt";
    public static final String TYPECLASS_INT = "Int";
    public static final String TYPECLASS_UINT = "UInt";
    public static final String TYPECLASS_DINT = "DInt";
    public static final String TYPECLASS_UDINT = "UDInt";
    public static final String TYPECLASS_LINT = "LInt";
    public static final String TYPECLASS_ULINT = "ULInt";
    public static final String TYPECLASS_REAL = "Real";
    public static final String TYPECLASS_LREAL = "LReal";
    public static final String TYPECLASS_STRING = "String";
    public static final String TYPECLASS_WSTRING = "WString";
    public static final String TYPECLASS_TIME = "Time";
    public static final String TYPECLASS_LTIME = "LTime";
    public static final String TYPECLASS_DATE = "Date";
    public static final String TYPECLASS_DATE_AND_TIME = "DateAndTime";
    public static final String TYPECLASS_TIME_OF_DAY = "TimeOfDay";
    public static final String TYPECLASS_ENUM = "Enum";
    public static final String TYPECLASS_ARRAY = "Array";
    public static final String TYPECLASS_USERDEF = "Userdef";

    private File mSymbolConfig;
    private List<SoMachineRecord> mRecords;
    private HashMap<String, Type> mTypeMap;

    private static HashMap<String, String> sAddressPatch;

    static {
        sAddressPatch = new HashMap<>();
        sAddressPatch.put(TYPECLASS_BOOL, SoMachineRecord.ADDRESS_TYPE_MX);
        sAddressPatch.put(TYPECLASS_BYTE, SoMachineRecord.ADDRESS_TYPE_MB);
        sAddressPatch.put(TYPECLASS_WORD, SoMachineRecord.ADDRESS_TYPE_MW);
        sAddressPatch.put(TYPECLASS_DWORD, SoMachineRecord.ADDRESS_TYPE_MD);
        sAddressPatch.put(TYPECLASS_LWORD, SoMachineRecord.ADDRESS_TYPE_ML);
        sAddressPatch.put(TYPECLASS_SINT, SoMachineRecord.ADDRESS_TYPE_MB);
        sAddressPatch.put(TYPECLASS_USINT, SoMachineRecord.ADDRESS_TYPE_MB);
        sAddressPatch.put(TYPECLASS_INT, SoMachineRecord.ADDRESS_TYPE_MW);
        sAddressPatch.put(TYPECLASS_UINT, SoMachineRecord.ADDRESS_TYPE_MW);
        sAddressPatch.put(TYPECLASS_DINT, SoMachineRecord.ADDRESS_TYPE_MD);
        sAddressPatch.put(TYPECLASS_UDINT, SoMachineRecord.ADDRESS_TYPE_MD);
        sAddressPatch.put(TYPECLASS_LINT, SoMachineRecord.ADDRESS_TYPE_ML);
        sAddressPatch.put(TYPECLASS_ULINT, SoMachineRecord.ADDRESS_TYPE_ML);
        sAddressPatch.put(TYPECLASS_REAL, SoMachineRecord.ADDRESS_TYPE_MD);
        sAddressPatch.put(TYPECLASS_LREAL, SoMachineRecord.ADDRESS_TYPE_ML);
        sAddressPatch.put(TYPECLASS_STRING, SoMachineRecord.ADDRESS_TYPE_MB);
        sAddressPatch.put(TYPECLASS_WSTRING, SoMachineRecord.ADDRESS_TYPE_MW);
        sAddressPatch.put(TYPECLASS_TIME, SoMachineRecord.ADDRESS_TYPE_ML);
        sAddressPatch.put(TYPECLASS_LTIME, SoMachineRecord.ADDRESS_TYPE_ML);
        sAddressPatch.put(TYPECLASS_DATE, SoMachineRecord.ADDRESS_TYPE_ML);
        sAddressPatch.put(TYPECLASS_DATE_AND_TIME, SoMachineRecord.ADDRESS_TYPE_ML);
        sAddressPatch.put(TYPECLASS_TIME_OF_DAY, SoMachineRecord.ADDRESS_TYPE_ML);
        sAddressPatch.put(TYPECLASS_ENUM, SoMachineRecord.ADDRESS_TYPE_ML);
        sAddressPatch.put(TYPECLASS_USERDEF, SoMachineRecord.ADDRESS_TYPE_ML);
    }

    public SoMachineXmlReader(File symbolConfig) {
        mSymbolConfig = symbolConfig;
        mRecords = new ArrayList<>();
    }

    public List<SoMachineRecord> read() throws IOException, UnsupportedTypeNode {
        XmlAdapter<Symbolconfiguration> xmlAdapter =
                new Xml.Builder().build().adapter(Symbolconfiguration.class);

        FileInputStream inputStream = new FileInputStream(mSymbolConfig);
        skipBom(inputStream);
        Symbolconfiguration config = xmlAdapter.fromXml(inputStream, StandardCharsets.UTF_8.toString());

        mTypeMap = new HashMap<>();
        for (TypeSimple type : config.typeList.typeSimple) mTypeMap.put(type.name, type);
        for (TypeArray type : config.typeList.typeArray) mTypeMap.put(type.name, type);
        for (TypeUserDef type : config.typeList.typeUserDef) mTypeMap.put(type.name, type);

        for (VarNode var : config.nodeList.appNode.gvlNode.varNode) {
            Type type = mTypeMap.get(var.type);
            SoMachineRecord.Address address = new SoMachineRecord.Address(
                    patchedAddress(var.directaddress, type));
            String comment = prepareComment(var.comment);
            addVar(var.name, comment, address, type, "", 0);
        }

        return mRecords;
    }

    /**
     * Fixes address markers
     * <p>
     * SoMachine incorrectly puts "%MB" marker where other
     * markers are expected, so we replace them according to sAddressPatch map.
     *
     * @param directaddress Address to patch
     * @param type Patch according to this type from sAddressPatch map
     * @return Patched address
     */
    private String patchedAddress(String directaddress, Type type) {
        String patched = directaddress;
        if (TYPECLASS_ARRAY.equals(type.typeclass)) {
            String basetype = ((TypeArray) type).basetype;
            patched = patchedAddress(directaddress, mTypeMap.get(basetype));
        } else {
            if (sAddressPatch.containsKey(type.typeclass)) {
                patched = directaddress.replaceFirst(
                        SoMachineRecord.ADDRESS_TYPE_MB, sAddressPatch.get(type.typeclass));
            }
        }
        return patched;
    }

    /**
     * Takes first line of the comment
     *
     * @param comment Comment to prepare
     * @return Prepared comment
     */
    private static String prepareComment(String comment) {
        if (comment == null) {
            comment = "";
        }
        int index = comment.indexOf('\n');
        if (index > -1) {
            comment = comment.substring(0, index);
        }
        return comment.trim();
    }

    private void addVar(String name, String comment, SoMachineRecord.Address address, Type type,
                        String namePrefix, int addressShift) throws UnsupportedTypeNode {
        if (type instanceof TypeSimple) {
            SoMachineRecord.Address addressShifted = address.shifted(addressShift);
            addPrimitive(name, comment, addressShifted, (TypeSimple) type, namePrefix);
        } else if (type instanceof TypeArray) {
            int arrayLength = ((TypeArray) type).arrayDim.maxrange;
            int arraySizeBytes = ((TypeArray) type).size;
            int elementSize = arraySizeBytes / arrayLength;
            Type elementType = mTypeMap.get(((TypeArray) type).basetype);
            for (int i = 0; i < arrayLength; i++) {
                // here 'i + 1' is just to start element names from 1
                addVar(Integer.toString(i + 1), comment, address, elementType,
                        namePrefix + name + NAME_DIV,
                        addressShift + i * elementSize);
            }
        } else if (type instanceof TypeUserDef) {
            TypeUserDef thisType = (TypeUserDef) type;
            if (TYPECLASS_ENUM.equals(thisType.typeclass)) {
                addVar(name, comment, address, mTypeMap.get(thisType.basetype), namePrefix, addressShift);
            } else {
                for (UserDefElement element : thisType.userDefElement) {
                    String elementComment = "";
                    if (element.comment != null) {
                        elementComment = comment + COMMENT_DIV + element.comment.trim();
                    }
                    Type elementType = mTypeMap.get(element.type);
                    // unions have byteoffset = -1
                    int byteoffset = element.byteoffset < 0 ? 0 : element.byteoffset;
                    addVar(element.iecname, elementComment, address, elementType,
                            namePrefix + name + NAME_DIV,
                            addressShift + byteoffset);
                }
            }
        } else {
            throw new UnsupportedTypeNode(type.getClass().getName());
        }
    }

    private void addPrimitive(String name, String comment, SoMachineRecord.Address address,
                              TypeSimple type, String namePrefix) {
        SoMachineRecord.Builder builder = new SoMachineRecord.Builder();

        builder.name(namePrefix + name);
        builder.comment(comment);

        String recordType = type.iecname;
        // 'STRING...', 'WSTRING...' iec names are dynamic (depend on size)
        // so just using corresponding constants
        switch (type.typeclass) {
            case TYPECLASS_STRING:
                recordType = SoMachineRecord.DATA_TYPE_STRING; break;
            case TYPECLASS_WSTRING:
                recordType = SoMachineRecord.DATA_TYPE_WSTRING; break;
            case TYPECLASS_ENUM:
                recordType = guessEnumType(type.size); break;
        }
        builder.type(recordType);

        SoMachineRecord record = builder.build();

        if (TYPECLASS_BOOL.equals(type.typeclass)) {
            address.setDigital(true);
        }
        record.setAddress(address);

        mRecords.add(record);
    }

    private String guessEnumType(int size) {
        switch (size) {
            case 1: return SoMachineRecord.DATA_TYPE_SINT;
            case 2: return SoMachineRecord.DATA_TYPE_INT;
            case 4: return SoMachineRecord.DATA_TYPE_DINT;
            case 8: return SoMachineRecord.DATA_TYPE_LINT;
            default: return SoMachineRecord.DATA_TYPE_INT;
        }
    }

    private void skipBom(FileInputStream inputStream) throws IOException {
        byte[] bomRead = new byte[3];
        byte[] utf8Bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        inputStream.read(bomRead);
        for (int i = 0; i < 3; i++) {
            if (bomRead[i] != utf8Bom[i]) {
                inputStream.skip(-3);
                break;
            }
        }
    }

    public class UnsupportedTypeNode extends Throwable {
        public UnsupportedTypeNode(String message) {
            super(message);
        }
    }
}
