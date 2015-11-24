package ua.com.elius.sm2csv.reader;

import org.apache.commons.io.FilenameUtils;
import ua.com.elius.sm2csv.record.SoMachineRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoMachineReader {
    private List<SoMachineRecord> mRecords;
    private String mExtention;
    private String mPath;

    public String getExtention() {
        return mExtention;
    }
    public String getPath() {
        return mPath;
    }

    public List<SoMachineRecord> getRecords() {
        return mRecords;
    }

    public void setExtention(String extention) {
        this.mExtention = extention;
    }
    public void setPath(String path) {
        this.mPath = path;
    }

    private SoMachineReader() {}

    public SoMachineReader read() {
        mRecords = new ArrayList<>();
        Pattern blockCommentBeginPattern = Pattern.compile("\\(\\*");
        Pattern blockCommentEndPattern = Pattern.compile("\\*\\)");
        Pattern commentPattern = Pattern.compile("^\\s+//(.+)$");
        Pattern recordPattern = Pattern.compile("^\\s+((?!//).)+$");
        try {
            Files.walk(Paths.get(mPath)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    String curExtention = FilenameUtils.getExtension(filePath.toString());
                    if (curExtention.equals(mExtention)) {
                        try {
                            ArrayList<String> curComments = new ArrayList<>();
                            boolean inBlockComment = false;
                            for (String line : Files.readAllLines(filePath)) {
                                Matcher blockCommentBeginMatcher = blockCommentBeginPattern.matcher(line);
                                Matcher blockCommentEndMatcher = blockCommentEndPattern.matcher(line);
                                Matcher commentMatcher = commentPattern.matcher(line);
                                Matcher recordMatcher = recordPattern.matcher(line);
                                if (blockCommentBeginMatcher.find()) inBlockComment = true;
                                if (blockCommentEndMatcher.find()) {
                                    inBlockComment = false;
                                    continue;
                                }
                                if (inBlockComment) continue;
                                if (commentMatcher.matches()) {
                                    curComments.add(commentMatcher.group(1).trim());
                                } else if (recordMatcher.matches()) {
                                    SoMachineRecord rec = SoMachineRecord.fromString(line);
                                    if (!curComments.isEmpty()) {
                                        rec.setComment(curComments.get(0));
                                        curComments.clear();
                                    }
                                    mRecords.add(rec);
                                }
                            }
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
