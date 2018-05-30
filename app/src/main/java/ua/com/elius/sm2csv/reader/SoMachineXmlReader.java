package ua.com.elius.sm2csv.reader;

import me.tatarka.parsnip.Xml;
import me.tatarka.parsnip.XmlAdapter;
import ua.com.elius.sm2csv.model.symbolconfig.*;
import ua.com.elius.sm2csv.model.symbolconfig.node.VarNode;
import ua.com.elius.sm2csv.model.symbolconfig.type.*;
import ua.com.elius.sm2csv.record.SoMachineRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SoMachineXmlReader {

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
        Symbolconfiguration config = xmlAdapter.fromXml(new FileReader(mSymbolConfig));

        mTypeMap = new HashMap<>();
        for (TypeSimple type : config.typeList.typeSimple) mTypeMap.put(type.name, type);
        for (TypeArray type : config.typeList.typeArray) mTypeMap.put(type.name, type);
        for (TypeUserDef type : config.typeList.typeUserDef) mTypeMap.put(type.name, type);

        for (VarNode var : config.nodeList.appNode.gvlNode.varNode) {
            Type type = mTypeMap.get(var.type);
            addVar(var.name, var.comment, var.directaddress, type, "", "", 0);
        }

        return mRecords;
    }

    private void addVar(String name, String comment, String directaddress, Type type, String namePrefix, String commentPrefix, int addressshift) {
        final String NAME_DIV = "_"; // divider for nested names
        final String COMMENT_DIV = " | "; // divider for nested comments

        if (type instanceof TypeSimple) {
            addPrimitive(name, comment, directaddress, (TypeSimple) type, namePrefix, commentPrefix, addressshift);
        } else if (type instanceof TypeArray) {
            int arrayLength = ((TypeArray) type).arrayDim.maxrange;
            int arraySizeBytes = ((TypeArray) type).size;
            int elementSize = arraySizeBytes / arrayLength;
            Type elementType = mTypeMap.get(((TypeArray) type).basetype);
            for (int i = 0; i < arrayLength; i++) {
                // here 'i + 1' is just to start element names from 1
                addVar(Integer.toString(i + 1), comment, directaddress, elementType,
                        namePrefix + name + NAME_DIV,
                        commentPrefix + name + COMMENT_DIV,
                        addressshift + i * elementSize);
            }
        } else if (type instanceof TypeUserDef) {
            for (UserDefElement element : ((TypeUserDef) type).userDefElement) {
                Type elementType = mTypeMap.get(element.type);
                addVar(element.iecname, comment, directaddress, elementType,
                        namePrefix + name + NAME_DIV,
                        commentPrefix + name + COMMENT_DIV,
                        addressshift + element.byteoffset);
            }
        } else {
            // TODO Maybe throw exception
        }
    }

    private void addPrimitive(String name, String comment, String directaddress, TypeSimple type, String namePrefix, String commentPrefix, int addressShift) {
        SoMachineRecord.Builder builder = new SoMachineRecord.Builder();

        builder.name(namePrefix + name);
        if (comment != null) {
            builder.comment(commentPrefix + comment);
        }
        builder.type(type.iecname);
        builder.address(directaddress);

        SoMachineRecord record = builder.build();

        SoMachineRecord.Address address;
        address = record.getAddress();
        address.setNumber(address.getNumber() + addressShift);

        mRecords.add(record);
    }
}
