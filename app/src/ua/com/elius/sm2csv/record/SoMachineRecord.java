package ua.com.elius.sm2csv.record;

import java.util.ArrayList;
import java.util.List;

public class SoMachineRecord {
    public static final String SM_ADDRESS_TYPE_BYTE = "%MX";
    public static final String SM_ADDRESS_TYPE_WORD = "%Mw";
    public static final String SM_ADDRESS_TYPE_DWORD = "%MD";

    private String mComment;
    private String mName;
    private String mAddress;
    private String mType;

    public String getComment() {
        return mComment;
    }
    public String getName() {
        return mName;
    }
    public String getAddress() {
        return mAddress;
    }
    public String getType() {
        return mType;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }
    public void setName(String name) {
        this.mName = name;
    }
    public void setAddress(String address) {
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
        list.add(mName);
        list.add(mAddress);
        list.add(mType);
        return list;
    }

    /**
     * Converts from string like this:
     *     "auto_start AT %MX11.2: BOOL;"
     *     "warn_sound: BOOL;"
     */
    public static SoMachineRecord fromString(String raw) {
        String[] parts = raw
                .replace(";","")
                .trim()
                .split("(\t)|( : )|(: )|(:)|( :)|( AT )", 3); //TODO check if \t required

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
            mRec.setAddress(address);
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
}
