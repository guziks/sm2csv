package ua.com.elius.sm2csv.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LectusRecord extends Record {

    public static final int DATA_TYPE_SMALL_INTEGER = 2;
    public static final int DATA_TYPE_INTEGER = 3;
    public static final int DATA_TYPE_SINGLE_FLOAT = 4;
    public static final int DATA_TYPE_BOOLEAN = 11;

    private int mDataType;
    private String mComment;
    private int mAddressNumber;
    private int mAddressDigit;

    private static HashMap<String, Integer> sFromSoMachineType;

    static {
        sFromSoMachineType = new HashMap<>();
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_BOOL, DATA_TYPE_BOOLEAN);
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_BYTE, );
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_WORD, DATA_TYPE_SMALL_INTEGER);
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DWORD, );
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LWORD, );
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_SINT, );
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_USINT, );
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_INT, DATA_TYPE_SMALL_INTEGER);
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_UINT, );
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DINT, DATA_TYPE_INTEGER);
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_UDINT, );
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LINT, );
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_ULINT, );
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_REAL, DATA_TYPE_SINGLE_FLOAT);
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LREAL, );
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_STRING, );
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_WSTRING, );
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_TIME, );
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LTIME, );
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DATE, );
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DATE_AND_TIME, );
    }

    public int getDataType() {
        return mDataType;
    }

    public void setDataType(int dataType) {
        mDataType = dataType;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public int getAddressNumber() {
        return mAddressNumber;
    }

    public void setAddressNumber(int addressNumber) {
        mAddressNumber = addressNumber;
    }

    public int getAddressDigit() {
        return mAddressDigit;
    }

    public void setAddressDigit(int addressDigit) {
        mAddressDigit = addressDigit;
    }

    public LectusRecord() {
    }

    public static LectusRecord of(SoMachineRecord smRec) {
        LectusRecord rec = new LectusRecord();

        rec.setName(smRec.getName());

        if (sFromSoMachineType.containsKey(smRec.getType())) {
            rec.setDataType(sFromSoMachineType.get(smRec.getType()));
        }

        rec.setAddressNumber(smRec.getAddress().getNumber());

        if (rec.isDigital()) {
            rec.setAddressDigit(smRec.getAddress().getDigit() + 1);
        }

        rec.setComment(smRec.getComment());

        return rec;
    }

    public List<String> toList() {
        List<String> list = new ArrayList<>();

        list.add(""); // OwnerItemID placeholder
        list.add(getName()); // Name
        list.add("Modbus"); // Plugin
        list.add("2"); // Flag
        list.add("3"); // AccessRights
        list.add("25"); // AccessChange
        list.add(String.valueOf(mDataType)); // DataType
        list.add("0"); // EUType
        list.add("0"); // EUInfo
        list.add("5"); // PropertyID
        list.add("3"); // PropertyValue
        list.add("7"); // PropertyID
        list.add("0"); // PropertyValue
        list.add("101");
        list.add(mComment);
        list.add("5003");
        list.add(String.valueOf(mAddressNumber));
        if (this.isDigital()) {
            list.add("5041");
            list.add(String.valueOf(mAddressDigit));
        }

        return list;
    }

    private boolean isDigital() {
        return mDataType == DATA_TYPE_BOOLEAN;
    }
}
