package ua.com.elius.sm2csv.record;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoMachineRecord extends Record {
    public static final String SM_ADDRESS_TYPE_BYTE = "%MX";
    public static final String SM_ADDRESS_TYPE_WORD = "%Mw";
    public static final String SM_ADDRESS_TYPE_DWORD = "%MD";

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

    /**
     * Converts from string like this:
     *     "auto_start AT %MX11.2: BOOL;"
     *     "warn_sound: BOOL;"
     */
    public static SoMachineRecord of(String raw) {
        String[] parts = raw
                .replace(";","")
                .trim()
                .split("(^\\s*)|(\\s*:\\s*)|(\\s*AT\\s*)", 3);

        Builder builder = new Builder();
        builder.name(parts[0]);
        switch (parts.length) {
            case 2:
                builder.type(parts[1]);
                break;
            case 3:
                builder.address(parts[1]);
                builder.type(parts[2]);
                break;
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
