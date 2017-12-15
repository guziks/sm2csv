package ua.com.elius.sm2csv.record;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoMachineRecord extends Record {

    public static final String DATA_TYPE_BOOL = "BOOL";
    public static final String DATA_TYPE_WORD = "WORD";
    public static final String DATA_TYPE_INT = "INT";
    public static final String DATA_TYPE_DINT = "DINT";
    public static final String DATA_TYPE_REAL = "REAL";

    public static final String SM_ADDRESS_TYPE_BIT = "%MX";
    public static final String SM_ADDRESS_TYPE_BYTE = "%MB";
    public static final String SM_ADDRESS_TYPE_WORD = "%MW";
    public static final String SM_ADDRESS_TYPE_DWORD = "%MD";
    public static final String SM_ADDRESS_TYPE_LONG = "%ML";

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

        private String mAddress;
        private boolean mIsDigital;
        private boolean mIsAnalog;
        private String mType;
        private int mNumber;
        private int mDigit;

        private Matcher mDigitalMatcher;
        private Matcher mAnalogMatcher;

        public Address(String address) {
            mAddress = address;
            mDigitalMatcher = DIGITAL_ADDRESS_PATTERN.matcher(mAddress);
            mAnalogMatcher = ANALOG_ADDRESS_PATTERN.matcher(mAddress);
            mIsDigital = mDigitalMatcher.matches();
            mIsAnalog = mAnalogMatcher.matches();
            if (mIsDigital) {
                mType = mDigitalMatcher.group(1);
                mNumber = Integer.parseInt(mDigitalMatcher.group(2));
                mDigit = Integer.parseInt(mDigitalMatcher.group(3));
            } else if (mIsAnalog) {
                mType = mAnalogMatcher.group(1);
                mNumber = Integer.parseInt(mAnalogMatcher.group(2));
            }
        }

        public String toString() {
            return mType + ":" + mAddress;
        }

        public boolean isDigital() {
            return mIsDigital;
        }

        public boolean isAnalog() {
            return mIsAnalog;
        }

        public String getType() {
            return mType;
        }

        public int getNumber() {
            return mNumber;
        }

        public int getDigit() {
            return mDigit;
        }
    }
}
