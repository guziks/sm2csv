package record;

import java.util.ArrayList;
import java.util.List;

public class SoMachineRecord {
    private String name;
    private String address;
    private String type;

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setType(String type) {
        this.type = type;
    }

    private SoMachineRecord() {}

    public boolean isExported() {
        return (address != null);
    }

    public List<String> toList() {
        List<String> list = new ArrayList<>();
        list.add(name);
        list.add(address);
        list.add(type);
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
                .split("(\t)|(: )|( AT )", 3);

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
        private SoMachineRecord rec = new SoMachineRecord();

        public Builder name(String name) {
            rec.setName(name);
            return this;
        }
        public Builder address(String address) {
            rec.setAddress(address);
            return this;
        }
        public Builder type(String type) {
            rec.setType(type);
            return this;
        }
        public SoMachineRecord build() {
            return rec;
        }
    }
}
