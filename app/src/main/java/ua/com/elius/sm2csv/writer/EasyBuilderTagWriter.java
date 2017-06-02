package ua.com.elius.sm2csv.writer;

import org.apache.commons.csv.CSVFormat;
import ua.com.elius.sm2csv.record.EasyBuilderRecord;

public class EasyBuilderTagWriter extends CSVWriter {
    private static final String OUTPUT_FILE_NAME = "easybuilder-tags.csv";
    private static final String OUTPUT_FILE_ENCODING = "windows-1251";

    public EasyBuilderTagWriter() {
        super(CSVFormat.EXCEL, OUTPUT_FILE_NAME, OUTPUT_FILE_ENCODING, "EasyBuilder", "tag");
    }

    public void write(EasyBuilderRecord record) {
        write(record.toList());
    }
}
