package ua.com.elius.sm2csv.record;

public class WinccRecord extends Record {
    public static final String ADDRESS_TYPE_ANALOG = "3x";
    public static final String ADDRESS_TYPE_DIGITAL = "0x";
    public static final String ADDRESS_TYPE_DIGITAL_IN_ANALOG = "3x";

    public static final String TYPE_DIGITAL = "Binary Tag";
    public static final String TYPE_REAL = "Floating-point number 32-bit IEEE 754";

    public static final String TYPE_LENGTH_DIGITAL = "1";
    public static final String TYPE_LENGTH_REAL = "4";

    public static final String FORMAT_FLOAT_TO_FLOAT = "FloatToFloat";

    private String mType;
    private String mLength;
    private String mFormat;
    private String mConnection;
    private String mAddress;
    private String mComment;

    public String getType() {
        return mType;
    }
    public String getLength() {
        return mLength;
    }
    public String getFormat() {
        return mFormat;
    }
    public String getConnection() {
        return mConnection;
    }
    public String getAddress() {
        return mAddress;
    }
    public String getComment() {
        return mComment;
    }

    public void setType(String mType) {
        this.mType = mType;
    }
    public void setLength(String mLength) {
        this.mLength = mLength;
    }
    public void setFormat(String mFormat) {
        this.mFormat = mFormat;
    }
    public void setConnection(String mConnection) {
        this.mConnection = mConnection;
    }
    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }
    public void setComment(String mComment) {
        this.mComment = mComment;
    }

    private WinccRecord() {

    }

    public static WinccRecord fromSoMachineRecord(SoMachineRecord smRec) throws UnsupportedAddressException {
        WinccRecord.Builder builder =
                new WinccRecord.Builder()
                        .name(smRec.getName())
                        .comment(smRec.getComment());

        SoMachineRecord.Address smAddress = smRec.getAddress();
        if (smAddress != null) {
            if (smAddress.isDigital()) {
                builder.address(ADDRESS_TYPE_DIGITAL_IN_ANALOG + fromSoMachineAddress(smAddress.getNumber(), smAddress.getDigit()));
                builder.type(TYPE_DIGITAL);
                builder.length(TYPE_LENGTH_DIGITAL);
            } else if (smAddress.isAnalog()) {
                builder.address(ADDRESS_TYPE_ANALOG + fromSoMachineAddress(smAddress.getType(), smAddress.getNumber()));
                builder.type(TYPE_REAL); // TODO support other types like double word
                builder.length(TYPE_LENGTH_REAL);
                builder.format(FORMAT_FLOAT_TO_FLOAT);
            } else {
                throw new UnsupportedAddressException(smAddress.toString());
            }
        }

        return builder.build();
    }

    private static String fromSoMachineAddress(int number, int digit) {
        int newNumber = number / 2;
        int newDigit;
        if (number % 2 == 0) {
            newDigit = digit;
        } else {
            newDigit = digit + 8;
        }
        return newNumber + "." + newDigit;
    }

    private static String fromSoMachineAddress(String type, int number) {
        int newNumber;
        switch (type) {
            case SoMachineRecord.SM_ADDRESS_TYPE_DWORD:
                newNumber = number * 2;
                break;
            default:
                newNumber = number;
        }
        return Integer.toString(newNumber);
    }

    public static class Builder {
        private WinccRecord mRec = new WinccRecord();

        public Builder name(String name) {
            mRec.setName(name);
            return this;
        }
        public Builder type(String type) {
            mRec.setType(type);
            return this;
        }
        public Builder length(String length) {
            mRec.setLength(length);
            return this;
        }
        public Builder format(String format) {
            mRec.setFormat(format);
            return this;
        }
        public Builder connection(String connection) {
            mRec.setConnection(connection);
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
        public WinccRecord build() {
            return mRec;
        }
    }
}
