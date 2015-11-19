package ua.com.elius.sm2csv.writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import ua.com.elius.sm2csv.record.EasyBuilderRecord;

import java.io.*;
import java.util.List;

public class EasyBuilderTagWriter {
    private static final String OUTPUT_FILE_NAME = "easybuilder-tags.csv";
    private static final String OUTPUT_FILE_ENCODING = "windows-1251";

    private CSVPrinter printer;

    public void open() {
        CSVFormat format = CSVFormat.EXCEL;
        OutputStreamWriter writer = null;

        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream(OUTPUT_FILE_NAME), OUTPUT_FILE_ENCODING
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            printer = new CSVPrinter(writer, format);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(EasyBuilderRecord record) {
        write(record.toList());
    }

    private void write(List<String> record) {
        try {
            printer.printRecord(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
