package ua.com.elius.sm2csv.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LectusRecord extends Record {

    public static final int DATA_TYPE_WORD = 18; // 2 bytes
    public static final int DATA_TYPE_BYTE = 17; // 1 bytes
    public static final int DATA_TYPE_DOUBLE_WORD = 19; // 4 bytes
    public static final int DATA_TYPE_QUADE_WORD = 21; // 8 bytes
    public static final int DATA_TYPE_SHORT_INTEGER = 16; // 1 bytes
    public static final int DATA_TYPE_SMALL_INTEGER = 2; // 2 bytes
    public static final int DATA_TYPE_INTEGER = 3; // 4 bytes
    public static final int DATA_TYPE_BIG_INTEGER = 20; // 8 bytes
    public static final int DATA_TYPE_SINGLE_FLOAT = 4; // 4 bytes
    public static final int DATA_TYPE_DOUBLE_FLOAT = 5; // 8 bytes
    public static final int DATA_TYPE_CURRENCY = 6; // 8 bytes
    public static final int DATA_TYPE_DATE = 7; // 8 bytes
    public static final int DATA_TYPE_BOOLEAN = 11; // 2 bytes
    public static final int DATA_TYPE_BCD = 18; // n bytes
    public static final int DATA_TYPE_STRING = 8; // n bytes

    private int mDataType;
    private String mComment;
    private int mAddressNumber;
    private int mAddressDigit;

    private static HashMap<String, Integer> sFromSoMachineType;

    static {
        sFromSoMachineType = new HashMap<>();
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_BOOL, DATA_TYPE_BOOLEAN);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_BYTE, DATA_TYPE_BYTE);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_WORD, DATA_TYPE_SMALL_INTEGER); // TODO maybe change to DATA_TYPE_WORD
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DWORD, DATA_TYPE_DOUBLE_WORD);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LWORD, DATA_TYPE_QUADE_WORD);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_SINT, DATA_TYPE_SHORT_INTEGER);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_USINT, DATA_TYPE_SHORT_INTEGER);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_INT, DATA_TYPE_SMALL_INTEGER);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_UINT, DATA_TYPE_SMALL_INTEGER);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DINT, DATA_TYPE_INTEGER);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_UDINT, DATA_TYPE_INTEGER);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LINT, DATA_TYPE_BIG_INTEGER);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_ULINT, DATA_TYPE_BIG_INTEGER);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_REAL, DATA_TYPE_SINGLE_FLOAT);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LREAL, DATA_TYPE_DOUBLE_FLOAT);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_STRING, DATA_TYPE_STRING);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_WSTRING, DATA_TYPE_STRING);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_TIME, DATA_TYPE_DATE);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LTIME, DATA_TYPE_DATE);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DATE, DATA_TYPE_DATE);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DATE_AND_TIME, DATA_TYPE_DATE);
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
