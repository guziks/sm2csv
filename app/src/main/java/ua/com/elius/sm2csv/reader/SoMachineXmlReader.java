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

    public static final String TYPECLASS_BOOL = "Bool";

    private File mSymbolConfig;
    private List<SoMachineRecord> mRecords;
    private HashMap<String, Type> mTypeMap;

    public SoMachineXmlReader(File symbolConfig) {
        mSymbolConfig = symbolConfig;
        mRecords = new ArrayList<>();
    }

    public List<SoMachineRecord> read() throws IOException {
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
            SoMachineRecord.Address address = new SoMachineRecord.Address(var.directaddress);
            addVar(var.name, var.comment, address, type, "", "", 0);
        }

        return mRecords;
    }

    private void addVar(String name, String comment, SoMachineRecord.Address address, Type type,
                        String namePrefix, String commentPrefix, int addressShift) {
        final String NAME_DIV = "_"; // divider for nested names
        final String COMMENT_DIV = " | "; // divider for nested comments

        if (type instanceof TypeSimple) {
            SoMachineRecord.Address addressShifted = address.shifted(addressShift);
            addPrimitive(name, comment, addressShifted, (TypeSimple) type, namePrefix, commentPrefix);
        } else if (type instanceof TypeArray) {
            int arrayLength = ((TypeArray) type).arrayDim.maxrange;
            int arraySizeBytes = ((TypeArray) type).size;
            int elementSize = arraySizeBytes / arrayLength;
            Type elementType = mTypeMap.get(((TypeArray) type).basetype);
            for (int i = 0; i < arrayLength; i++) {
                // here 'i + 1' is just to start element names from 1
                addVar(Integer.toString(i + 1), comment, address, elementType,
                        namePrefix + name + NAME_DIV,
                        commentPrefix + name + COMMENT_DIV,
                        addressShift + i * elementSize);
            }
        } else if (type instanceof TypeUserDef) {
            for (UserDefElement element : ((TypeUserDef) type).userDefElement) {
                Type elementType = mTypeMap.get(element.type);
                // unions have byteoffset = -1
                int byteoffset = element.byteoffset < 0 ? 0 : element.byteoffset;
                addVar(element.iecname, comment, address, elementType,
                        namePrefix + name + NAME_DIV,
                        commentPrefix + name + COMMENT_DIV,
                        addressShift + byteoffset);
            }
        } else {
            // TODO Maybe throw exception
        }
    }

    private void addPrimitive(String name, String comment, SoMachineRecord.Address address,
                              TypeSimple type, String namePrefix, String commentPrefix) {
        SoMachineRecord.Builder builder = new SoMachineRecord.Builder();

        builder.name(namePrefix + name);
        if (comment != null) {
            builder.comment(commentPrefix + comment);
        }
        builder.type(type.iecname);

        SoMachineRecord record = builder.build();

        if (TYPECLASS_BOOL.equals(type.typeclass)) {
            address.setDigital(true);
        }
        record.setAddress(address);

        mRecords.add(record);
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
}
