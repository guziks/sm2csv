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
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_WORD, DATA_TYPE_SMALL_INTEGER);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_INT, DATA_TYPE_SMALL_INTEGER);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DINT, DATA_TYPE_INTEGER);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_REAL, DATA_TYPE_SINGLE_FLOAT);
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

        rec.setAddressNumber(numberFromSoMachineAddress(smRec.getAddress()));

        if (rec.isDigital()) {
            rec.setAddressDigit(digitFromSoMachineAddress(smRec.getAddress()));
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

    private static int numberFromSoMachineAddress(SoMachineRecord.Address smAddress) {
        int newNumber;
        switch (smAddress.getType()) {
            case SoMachineRecord.SM_ADDRESS_TYPE_DWORD:
                newNumber = smAddress.getNumber() * 2;
                break;
            case SoMachineRecord.SM_ADDRESS_TYPE_BIT:
                newNumber = smAddress.getNumber() / 2;
                break;
            default:
                newNumber = smAddress.getNumber();
        }
        return newNumber;
    }

    private static int digitFromSoMachineAddress(SoMachineRecord.Address smAddress) {
        if (smAddress.getNumber() % 2 == 0) {
            return smAddress.getDigit() + 1;
        } else {
            return smAddress.getDigit() + 9;
        }
    }
}
