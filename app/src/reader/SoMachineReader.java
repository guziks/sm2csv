package reader;

import org.apache.commons.io.FilenameUtils;
import record.SoMachineRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Pattern commentPattern = Pattern.compile("^\\s+//(.+)$");
        Pattern recordPattern = Pattern.compile("^\\s+((?!//).)+$");
        try {
            Files.walk(Paths.get(path)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    String curExtention = FilenameUtils.getExtension(filePath.toString());
                    if (curExtention.equals(extention)) {
                        try {
                            ArrayList<String> curComments = new ArrayList<>();
                            Files.lines(filePath).forEach(line -> {
                                Matcher commentlMatcher = commentPattern.matcher(line);
                                Matcher recordMatcher = recordPattern.matcher(line);
                                if (commentlMatcher.matches()) {
                                    curComments.add(commentlMatcher.group(1).trim());
                                } else if (recordMatcher.matches()) {
                                    SoMachineRecord rec = SoMachineRecord.fromString(line);
                                    if (!curComments.isEmpty()) {
                                        rec.setComment(curComments.get(0));
                                        curComments.clear();
                                    }
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
