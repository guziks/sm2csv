package ua.com.elius.sm2csv.writer;

import ua.com.elius.sm2csv.record.SimpleScadaRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class SimpleScadaAssignmentWriter {

    public static final String FILE_NAME = "update_tags_script.txt";

    private FileWriter mWriter;

    public SimpleScadaAssignmentWriter(Path outputPath) throws IOException {
        mWriter = new FileWriter(outputPath.resolve(FILE_NAME).toFile());
    }

    public void write(List<SimpleScadaRecord> records, List<String> varsToUncomment) throws IOException {
        mWriter.write("begin\r\n");
        for (SimpleScadaRecord r : records) {
            String commentPrefix = "//";
            if (varsToUncomment != null && varsToUncomment.contains(r.getName())) {
                commentPrefix = "";
            }
            mWriter.write(String.format(commentPrefix +
                    "%s.Variable := GetVariableByName('%s');\r\n", r.getName(), r.getName()));
        }
        mWriter.write("end.\r\n");
    }

    public void close() {
        try {
            mWriter.flush();
            mWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
