package ua.com.elius.sm2csv.writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import ua.com.elius.sm2csv.record.EasyBuilderRecord;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class EasyBuilderAlarmWriter {

    private static final String OUTPUT_FILE_NAME = "alarms.csv";
    private static final String OUTPUT_FILE_ENCODING = "windows-1251";
    private static final String[] OUTPUT_FILE_HEADER = {"Category","Priority","Address Type","PLC Name (Read)","Device Type (Read)","System Tag (Read)","User-defined Tag (Read)","Address (Read)","Index (Read)","Data Format (Read)","Enable Notification","Set ON (Notification)","PLC Name (Notification)","Device Type (Notification)","System Tag (Notification)","User-defined Tag (Notification)","Address (Notification)","Index (Notification)","Condition","Trigger Value","Content","Use Label Library","Label Name","Font","Color","Acknowledge Value","Enable Sound","Sound Library Name","Sound Index","No. of Multi-watch","Continuous Beep","Time Interval of Beeps","Send eMail when Event Triggered","Send eMail when Event Cleared","Delay Time","Dynamic Condition"};

    private static final String[] RECORD_TEMPLATE = {"0","Middle","Bit","<plc name>","<tag name>","False","True","<address type - address>","null","","False","False","Local HMI","LB","False","False","0","null","bt: 1","0","<comment>","False","","Arial","139:0:0","11","False","","0","0","False","10","False","False","0","0"};
    private static final int PLC_NAME_INDEX = 3;
    private static final int TAG_NAME_INDEX = 4;
    private static final int ADDRESS_INDEX = 7;
    private static final int COMMENT_INDEX = 20;

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
        if (record.isAlarm()) {
            List<String> list = Arrays.asList(RECORD_TEMPLATE);
            list.set(PLC_NAME_INDEX, record.getPlcName());
            list.set(TAG_NAME_INDEX, record.getName());
            list.set(ADDRESS_INDEX, record.getAddressType() + "-" + record.getAddress());
            list.set(COMMENT_INDEX, record.getComment());
            write(list);
        }
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
