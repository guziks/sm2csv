package reader;

import org.apache.commons.io.FilenameUtils;
import record.SoMachineRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SoMachineReader {
    private List<SoMachineRecord> records;
    private String extention;
    private String path;

    public String getExtention() {
        return extention;
    }
    public String getPath() {
        return path;
    }

    public List<SoMachineRecord> getRecords() {
        return records;
    }

    public void setExtention(String extention) {
        this.extention = extention;
    }
    public void setPath(String path) {
        this.path = path;
    }

    private SoMachineReader() {}

    public SoMachineReader read() {
        records = new ArrayList<>();
        try {
            Files.walk(Paths.get(path)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    String curExtention = FilenameUtils.getExtension(filePath.toString());
                    if (curExtention.equals(extention)) {
                        try {
                            Files.lines(filePath).forEach(line -> {
                                if (Character.isWhitespace(line.charAt(0))) {
                                    SoMachineRecord rec = SoMachineRecord.fromString(line);
                                    records.add(rec);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static class Builder {
        SoMachineReader reader = new SoMachineReader();

        public Builder path(String path) {
            reader.setPath(path);
            return this;
        }
        public Builder extention(String extention) {
            reader.setExtention(extention);
            return this;
        }

        public SoMachineReader build() {
            return reader;
        }
    }
}
