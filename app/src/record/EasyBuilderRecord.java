package record;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EasyBuilderRecord {

    public static final String EB_ADDRESS_TYPE_ANALOG = "%MW";
    public static final String EB_ADDRESS_TYPE_DIGITAL = "%M";
    public static final String EB_ADDRESS_TYPE_DIGITAL_IN_ANALOG = "%MW_Bit";

    private String name;
    private String plcName;
    private String addressType;
    private String address;
    private String comment;
    private String valueType;

    private boolean dummy;

    public String getName() {
        return name;
    }
    public String getPlcName() {
        return plcName;
    }
    public String getAddressType() {
        return addressType;
    }
    public String getAddress() {
        return address;
    }
    public String getComment() {
        return comment;
    }
    public String getValueType() {
        return valueType;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setPlcName(String plcName) {
        this.plcName = plcName;
    }
    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    private EasyBuilderRecord() {}

    public static EasyBuilderRecord fromSoMachineRecord(SoMachineRecord smRec) throws UnsupportedAddressException {

        EasyBuilderRecord.Builder builder =
                new EasyBuilderRecord.Builder()
                    .name(smRec.getName());

        String smAddress = smRec.getAddress();
        if (smAddress != null) {
            Pattern analogAddressPattern = Pattern.compile("^(%\\D+)(\\d+)$");
            Pattern digitalAddressPattern = Pattern.compile("^(%\\D+)(\\d+)\\.(\\d+)$");
            Matcher analogMatcher = analogAddressPattern.matcher(smAddress);
            Matcher digitalMatcher = digitalAddressPattern.matcher(smAddress);
            if (digitalMatcher.matches()) {
                String wordString = digitalMatcher.group(2);
                String bitString = digitalMatcher.group(3);
                String bitStringFormatted = String.format("%02d", Integer.valueOf(bitString).intValue());
                builder.addressType(EB_ADDRESS_TYPE_DIGITAL_IN_ANALOG);
                builder.address(wordString + bitStringFormatted);
            } else if (analogMatcher.matches()) {
                builder.addressType(EB_ADDRESS_TYPE_ANALOG);
                builder.address(analogMatcher.group(2));
            } else {
                throw new UnsupportedAddressException(smAddress);
            }
        }

        return builder.build();
    }

    public List<String> toList() {
        List<String> list = new ArrayList<>();
        list.add(name);
        list.add(plcName);
        list.add(addressType);
        list.add(address);
        list.add(comment);
        list.add(valueType);
        return list;
    }

    public static class Builder {
        private EasyBuilderRecord rec = new EasyBuilderRecord();

        public Builder name(String name) {
            rec.setName(name);
            return this;
        }
        public Builder plcName(String plcName) {
            rec.setPlcName(plcName);
            return this;
        }
        public Builder addressType(String addressType) {
            rec.setAddressType(addressType);
            return this;
        }
        public Builder address(String address) {
            rec.setAddress(address);
            return this;
        }
        public Builder comment(String comment) {
            rec.setComment(comment);
            return this;
        }
        public Builder valueType(String valueType) {
            rec.setValueType(valueType);
            return this;
        }
        public EasyBuilderRecord build() {
            return rec;
        }
    }

    public static class UnsupportedAddressException extends Throwable {
        public UnsupportedAddressException(String message) {
            super(message);
        }
    }
}
