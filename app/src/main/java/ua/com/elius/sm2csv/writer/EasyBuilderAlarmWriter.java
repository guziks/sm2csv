package ua.com.elius.sm2csv.writer;

import org.apache.commons.csv.CSVFormat;
import ua.com.elius.sm2csv.record.EasyBuilderRecord;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class EasyBuilderAlarmWriter extends CSVWriter {
    private static final String OUTPUT_FILE_NAME = "easybuilder-alarms.csv";
    private static final String OUTPUT_FILE_ENCODING = "windows-1251";
    private static final String[] OUTPUT_FILE_HEADER = {"Category","Priority","Address Type","PLC Name (Read)","Device Type (Read)","System Tag (Read)","User-defined Tag (Read)","Address (Read)","Index (Read)","Data Format (Read)","Enable Notification","Set ON (Notification)","PLC Name (Notification)","Device Type (Notification)","System Tag (Notification)","User-defined Tag (Notification)","Address (Notification)","Index (Notification)","Condition","Trigger Value","Content","Use Label Library","Label Name","Font","Color","Acknowledge Value","Enable Sound","Sound Library Name","Sound Index","No. of Multi-watch","Continuous Beep","Time Interval of Beeps","Send eMail when Event Triggered","Send eMail when Event Cleared","Delay Time","Dynamic Condition","PLC Name (Condition)","Device Type (Condition)","System Tag (Condition)","User-defined Tag (Condition)","Address (Condition)","Index (Condition)"};

    private static final String PLC_NAME_ID = "${plc_name}";
    private static final String TAG_NAME_ID = "${tag_name}";
    private static final String ADDRESS_ID = "${address_type_and_address}";
    private static final String COMMENT_ID = "${comment}";

    private static final String[] RECORD_TEMPLATE_BIT =  {"0","Middle","Bit", PLC_NAME_ID,TAG_NAME_ID,"False","True",ADDRESS_ID,"null","",               "False","False","Local HMI","LB","False","False","0","null","bt: 1", "0",COMMENT_ID,"False","","Arial","139:0:0","11","False","","0","0","False","10","False","False","0","0",PLC_NAME_ID,TAG_NAME_ID,"False","True",ADDRESS_ID,"null","True","False","Local HMI","LW","False","False","0","null","",               "", "", "False","False","False","False"};
    private static final String[] RECORD_TEMPLATE_WORD = {"0","Middle","Word",PLC_NAME_ID,TAG_NAME_ID,"False","True",ADDRESS_ID,"null","16-bit Unsigned","False","False","Local HMI","LB","False","False","0","null","wd: ==","2",COMMENT_ID,"False","","Arial","139:0:0","11","False","","0","0","False","10","False","False","0","0",PLC_NAME_ID,TAG_NAME_ID,"False","True",ADDRESS_ID,"null","True","False","Local HMI","LW","False","False","0","null","16-bit Unsigned","0","0","False","False","False","False"};

    public EasyBuilderAlarmWriter(Path outputPath) {
        super(CSVFormat.EXCEL,
                outputPath, OUTPUT_FILE_NAME, OUTPUT_FILE_ENCODING,
                CSVWriter.TARGET_TAG_EASYBUILDER, CSVWriter.TARGET_TAG_ALARM);
    }

    @Override
    void onOpen() {
        write(Arrays.asList(OUTPUT_FILE_HEADER));
    }

    public void write(EasyBuilderRecord record) {
        ArrayList<String> r = new ArrayList<>(); // record to write

        if (record.isDigital()) {
            r.addAll(Arrays.asList(RECORD_TEMPLATE_BIT));
        } else {
            r.addAll(Arrays.asList(RECORD_TEMPLATE_WORD));
        }

        for (int i = 0; i < r.size(); i++) {
            if (PLC_NAME_ID.equals(r.get(i))) r.set(i, record.getPlcName());
            if (TAG_NAME_ID.equals(r.get(i))) r.set(i, record.getName());
            if (ADDRESS_ID.equals(r.get(i)))  r.set(i, record.getAddressType() + "-" + record.getAddress());
            if (COMMENT_ID.equals(r.get(i)))  r.set(i, record.getComment());
        }

        write(r);
    }
}
