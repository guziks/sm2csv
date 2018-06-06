package ua.com.elius.sm2csv.record;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoMachineRecord extends Record {

    public static final String DATA_TYPE_BIT = "BIT";
    public static final String DATA_TYPE_BOOL = "BOOL"; // 1 bytes
    public static final String DATA_TYPE_BYTE = "BYTE"; // 1 bytes
    public static final String DATA_TYPE_WORD = "WORD"; // 2 bytes
    public static final String DATA_TYPE_DWORD = "DWORD"; // 4 bytes
    public static final String DATA_TYPE_LWORD = "LWORD"; // 8 bytes
    public static final String DATA_TYPE_SINT = "SINT"; // 1 bytes
    public static final String DATA_TYPE_USINT = "USINT"; // 1 bytes
    public static final String DATA_TYPE_INT = "INT"; // 2 bytes
    public static final String DATA_TYPE_UINT = "UINT"; // 2 bytes
    public static final String DATA_TYPE_DINT = "DINT"; // 4 bytes
    public static final String DATA_TYPE_UDINT = "UDINT"; // 4 bytes
    public static final String DATA_TYPE_LINT = "LINT"; // 8 bytes
    public static final String DATA_TYPE_ULINT = "ULINT"; // 8 bytes
    public static final String DATA_TYPE_REAL = "REAL"; // 4 bytes
    public static final String DATA_TYPE_LREAL = "LREAL"; // 8 bytes
    public static final String DATA_TYPE_STRING = "STRING"; // (size + 1) bytes
    public static final String DATA_TYPE_WSTRING = "WSTRING"; // 2(size + 1) bytes
    public static final String DATA_TYPE_TIME = "TIME"; // 4 bytes
    public static final String DATA_TYPE_LTIME = "LTIME"; // 8 bytes
    public static final String DATA_TYPE_DATE = "DATE"; // 4 bytes
    public static final String DATA_TYPE_DATE_AND_TIME = "DATE_AND_TIME"; // 4 bytes
    public static final String DATA_TYPE_TIME_OF_DAY = "TIME_OF_DAY"; // 4 bytes

    public static final String ADDRESS_TYPE_BIT = "%MX";
    public static final String ADDRESS_TYPE_BYTE = "%MB";
    public static final String ADDRESS_TYPE_WORD = "%MW";
    public static final String ADDRESS_TYPE_DWORD = "%MD";
    public static final String ADDRESS_TYPE_LONG = "%ML";

    private String mComment;
    private Address mAddress;
    private String mType;

    public String getComment() {
        return mComment;
    }
    public Address getAddress() {
        return mAddress;
    }
    public String getType() {
        return mType;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }
    public void setAddress(Address address) {
        this.mAddress = address;
    }
    public void setType(String type) {
        this.mType = type;
    }

    private SoMachineRecord() {}

    public boolean isExported() {
        return (mAddress != null);
    }

    public List<String> toList() {
        List<String> list = new ArrayList<>();
        list.add(getName());
        list.add(mAddress.toString());
        list.add(mType);
        return list;
    }

    public static SoMachineRecord of(String raw) {
        Pattern pattern = Pattern.compile("^\\s*(?<name>\\S+)(?:\\s+AT\\s+(?<addr>\\S+))?\\s*:\\s*(?<type>\\S+)\\s*\\S+\\s*$");

        Builder builder = new Builder();
        Matcher matcher;

        matcher = pattern.matcher(raw);
        if (matcher.matches()) {
            builder.name(matcher.group("name"));
            String addr = matcher.group("addr");
            if (addr != null) {
                builder.address(addr);
            }
            builder.type(matcher.group("type"));
        }

        return builder.build();
    }

    public static class Builder {
        private SoMachineRecord mRec = new SoMachineRecord();

        public Builder comment(String comment) {
            mRec.setComment(comment);
            return this;
        }
        public Builder name(String name) {
            mRec.setName(name);
            return this;
        }
        public Builder address(String address) {
            Address addr = new Address(address);
            mRec.setAddress(addr);
            return this;
        }
        public Builder type(String type) {
            mRec.setType(type);
            return this;
        }
        public SoMachineRecord build() {
            return mRec;
        }
    }

    public static class Address {
        private static final Pattern DIGITAL_ADDRESS_PATTERN = Pattern.compile("^(%\\D+)(\\d+)\\.(\\d+)$");
        private static final Pattern ANALOG_ADDRESS_PATTERN = Pattern.compile("^(%\\D+)(\\d+)$");

        private boolean mIsDigital;
        private boolean mIsAnalog;
        private String mType;
        private int mNumber;
        private int mDigit;

        public Address() {}

        public Address(String address) {
            Matcher digitalMatcher = DIGITAL_ADDRESS_PATTERN.matcher(address);
            Matcher analogMatcher = ANALOG_ADDRESS_PATTERN.matcher(address);
            mIsDigital = digitalMatcher.matches();
            mIsAnalog = analogMatcher.matches();
            if (mIsDigital) {
                mType = digitalMatcher.group(1);
                mNumber = Integer.parseInt(digitalMatcher.group(2));
                mDigit = Integer.parseInt(digitalMatcher.group(3));
            } else if (mIsAnalog) {
                mType = analogMatcher.group(1);
                mNumber = Integer.parseInt(analogMatcher.group(2));
            }

            // convert numbers to common units (2 byte words)
            switch (mType) {
                case ADDRESS_TYPE_BIT:
                case ADDRESS_TYPE_BYTE:
                    if (mNumber % 2 != 0) {
                        mDigit = (mDigit + 8);
                    }
                    mNumber = mNumber / 2;
                    break;
                case ADDRESS_TYPE_DWORD:
                    mNumber = mNumber * 2;
                    break;
                case ADDRESS_TYPE_LONG:
                    mNumber = mNumber * 4;
                    break;
            }
        }

        public Address shifted(int shiftBytes) {
            Address address = new Address();
            address.setDigital(mIsDigital);
            address.setAnalog(mIsAnalog);
            address.setType(mType);

            // mNumber is measured in words (2 bytes)
            address.setNumber(mNumber + shiftBytes / 2);

            if (shiftBytes % 2 == 0) {
                address.setDigit(mDigit);
            } else {
                address.setDigit(mDigit + 8);
            }

            return address;
        }

        public boolean isDigital() {
            return mIsDigital;
        }

        public void setDigital(boolean digital) {
            mIsDigital = digital;
            mIsAnalog = !digital;
        }

        public boolean isAnalog() {
            return mIsAnalog;
        }

        public void setAnalog(boolean analog) {
            mIsAnalog = analog;
            mIsDigital = !analog;
        }

        public String getType() {
            return mType;
        }

        public void setType(String type) {
            mType = type;
        }

        public int getNumber() {
            return mNumber;
        }

        public void setNumber(int number) {
            mNumber = number;
        }

        public int getDigit() {
            return mDigit;
        }

        public void setDigit(int digit) {
            mDigit = digit;
        }
    }
}
