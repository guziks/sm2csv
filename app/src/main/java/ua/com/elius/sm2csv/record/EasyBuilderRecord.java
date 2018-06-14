package ua.com.elius.sm2csv.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EasyBuilderRecord extends Record {

    public static final String DATA_TYPE_UNDESIGNATED = "Undesignated";
    public static final String DATA_TYPE_BCD_16 = "16-bit BCD";
    public static final String DATA_TYPE_BCD_32 = "32-bit BCD";
    public static final String DATA_TYPE_UNSIGNED_16 = "16-bit Unsigned";
    public static final String DATA_TYPE_UNSIGNED_32 = "32-bit Unsigned";
    public static final String DATA_TYPE_SIGNED_16 = "16-bit Signed";
    public static final String DATA_TYPE_SIGNED_32 = "32-bit Signed";
    public static final String DATA_TYPE_FLOAT_32 = "32-bit Float";

    public static final String ADDRESS_TYPE_MW = "%MW";
    public static final String ADDRESS_TYPE_M = "%M";
    public static final String ADDRESS_TYPE_MW_BIT = "%MW_Bit";

    private String mPlcName;
    private String mAddressType;
    private String mAddress;
    private String mComment;
    private String mDataType = DATA_TYPE_UNDESIGNATED;

    private static HashMap<String, String> sFromSoMachineType;

    static {
        sFromSoMachineType = new HashMap<>();
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_BIT, DATA_TYPE_UNDESIGNATED);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_BOOL, DATA_TYPE_UNDESIGNATED);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_BYTE, DATA_TYPE_UNDESIGNATED);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_WORD, DATA_TYPE_UNSIGNED_16);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DWORD, DATA_TYPE_UNSIGNED_32);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LWORD, DATA_TYPE_UNDESIGNATED);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_SINT, DATA_TYPE_UNDESIGNATED);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_USINT, DATA_TYPE_UNDESIGNATED);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_INT, DATA_TYPE_SIGNED_16);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_UINT, DATA_TYPE_UNSIGNED_16);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DINT, DATA_TYPE_SIGNED_32);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_UDINT, DATA_TYPE_UNSIGNED_32);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LINT, DATA_TYPE_UNDESIGNATED);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_ULINT, DATA_TYPE_UNDESIGNATED);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_REAL, DATA_TYPE_FLOAT_32);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LREAL, DATA_TYPE_UNDESIGNATED);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_STRING, DATA_TYPE_UNDESIGNATED);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_WSTRING, DATA_TYPE_UNDESIGNATED);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_TIME, DATA_TYPE_SIGNED_32);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LTIME, DATA_TYPE_UNDESIGNATED);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DATE, DATA_TYPE_SIGNED_32);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DATE_AND_TIME, DATA_TYPE_SIGNED_32);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_TIME_OF_DAY, DATA_TYPE_SIGNED_32);
    }

    public String getPlcName() {
        return mPlcName;
    }
    public String getAddressType() {
        return mAddressType;
    }
    public String getAddress() {
        return mAddress;
    }
    public String getComment() {
        return mComment;
    }
    public String getDataType() {
        return mDataType;
    }

    public void setPlcName(String plcName) {
        this.mPlcName = plcName;
    }
    public void setAddressType(String addressType) {
        this.mAddressType = addressType;
    }
    public void setAddress(String address) {
        this.mAddress = address;
    }
    public void setComment(String comment) {
        this.mComment = comment;
    }
    public void setDataType(String dataType) {
        this.mDataType = dataType;
    }

    private EasyBuilderRecord() {}

    public static EasyBuilderRecord of(SoMachineRecord smRec) throws UnsupportedAddressException {
        EasyBuilderRecord.Builder builder =
                new EasyBuilderRecord.Builder()
                    .name(smRec.getName());

        SoMachineRecord.Address smAddress = smRec.getAddress();
        if (smAddress != null) {
            if (smAddress.isDigital()) {
                builder.addressType(ADDRESS_TYPE_MW_BIT);
                builder.address(formatAddress(smAddress.getNumber(), smAddress.getDigit()));
            } else if (smAddress.isAnalog()) {
                builder.addressType(ADDRESS_TYPE_MW);
                builder.address(formatAddress(smAddress.getNumber()));
            } else {
                throw new UnsupportedAddressException(smAddress.toString());
            }
        }

        builder.comment(smRec.getComment());

        builder.dataType(sFromSoMachineType.getOrDefault(smRec.getType(), DATA_TYPE_UNDESIGNATED));

        return builder.build();
    }

    public static String formatAddress(int number, int digit) {
        return number + String.format("%02d", digit);
    }

    private static String formatAddress(int number) {
        return Integer.toString(number);
    }

    public List<String> toList() {
        List<String> list = new ArrayList<>();
        list.add(getName());
        list.add(mPlcName);
        list.add(mAddressType);
        list.add(mAddress);
        list.add(mComment);
        list.add(mDataType);
        return list;
    }

    public boolean isDigital() {
        return ADDRESS_TYPE_M.equals(mAddressType) ||
               ADDRESS_TYPE_MW_BIT.equals(mAddressType);
    }

    public static class Builder {
        private EasyBuilderRecord mRec = new EasyBuilderRecord();

        public Builder name(String name) {
            mRec.setName(name);
            return this;
        }
        public Builder plcName(String plcName) {
            mRec.setPlcName(plcName);
            return this;
        }
        public Builder addressType(String addressType) {
            mRec.setAddressType(addressType);
            return this;
        }
        public Builder address(String address) {
            mRec.setAddress(address);
            return this;
        }
        public Builder comment(String comment) {
            mRec.setComment(comment);
            return this;
        }
        public Builder dataType(String dataType) {
            mRec.setDataType(dataType);
            return this;
        }
        public EasyBuilderRecord build() {
            return mRec;
        }
    }

}
