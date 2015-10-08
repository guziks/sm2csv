package ua.com.elius.sm2csv.writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import ua.com.elius.sm2csv.record.EasyBuilderRecord;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class EeasyBuilderAlarmWriter {

    private static final String OUTPUT_FILE_NAME = "alarms.csv";
    private static final String OUTPUT_FILE_ENCODING = "windows-1251";
    private static final String[] OUTPUT_FILE_HEADER = {"Category","Priority","Address Type","PLC Name (Read)","Device Type (Read)","System Tag (Read)","User-defined Tag (Read)","Address (Read)","Index (Read)","Data Format (Read)","Enable Notification","Set ON (Notification)","PLC Name (Notification)","Device Type (Notification)","System Tag (Notification)","User-defined Tag (Notification)","Address (Notification)","Index (Notification)","Condition","Trigger Value","Content","Use Label Library","Label Name","Font","Color","Acknowledge Value","Enable Sound","Sound Library Name","Sound Index","No. of Multi-watch","Continuous Beep","Time Interval of Beeps","Send eMail when Event Triggered","Send eMail when Event Cleared","Delay Time","Dynamic Condition"};

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

        write(Arrays.asList(OUTPUT_FILE_HEADER));
    }

    public void write(EasyBuilderRecord record) {
        write(record.toList());
    }

    public void write(List<String> record) {
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
