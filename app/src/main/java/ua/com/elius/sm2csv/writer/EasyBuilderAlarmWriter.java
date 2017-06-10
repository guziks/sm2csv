package ua.com.elius.sm2csv.writer;

import org.apache.commons.csv.CSVFormat;
import ua.com.elius.sm2csv.record.EasyBuilderRecord;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Arrays.asList;

public class EasyBuilderAlarmWriter extends CSVWriter {
    private static final String OUTPUT_FILE_NAME = "easybuilder-alarms.csv";
    private static final String OUTPUT_FILE_ENCODING = "windows-1251";
    private static final String[] OUTPUT_FILE_HEADER = {"Category","Priority","Address Type","PLC Name (Read)","Device Type (Read)","System Tag (Read)","User-defined Tag (Read)","Address (Read)","Index (Read)","Data Format (Read)","Enable Notification","Set ON (Notification)","PLC Name (Notification)","Device Type (Notification)","System Tag (Notification)","User-defined Tag (Notification)","Address (Notification)","Index (Notification)","Condition","Trigger Value","Content","Use Label Library","Label Name","Font","Color","Acknowledge Value","Enable Sound","Sound Library Name","Sound Index","No. of Multi-watch","Continuous Beep","Time Interval of Beeps","Send eMail when Event Triggered","Send eMail when Event Cleared","Delay Time","Dynamic Condition","PLC Name (Condition)","Device Type (Condition)","System Tag (Condition)","User-defined Tag (Condition)","Address (Condition)","Index (Condition)"};

    private static final String PLC_NAME_ID = "${plc_name}";
    private static final String TAG_NAME_ID = "${tag_name}";
    private static final String ADDRESS_ID = "${address_type_and_address}";
    private static final String COMMENT_ID = "${comment}";
    private static final String TRIGGER_VALUE_ID = "${trigger_value}";
    private static final String COLOR_ID = "${color}";

    private static final String RED = "139:0:0";
    private static final String PURPLE = "139:0:139";

    private static final String TRIGGER_GENERAL = "2";
    private static final String TRIGGER_POTENTIAL = "3";

    private static final String[] RECORD_TEMPLATE_BIT =  {"0","Middle","Bit", PLC_NAME_ID,TAG_NAME_ID,"False","True",ADDRESS_ID,"null","",               "False","False","Local HMI","LB","False","False","0","null","bt: 1", "0",             COMMENT_ID,"False","","Arial",RED,     "11","False","","0","0","False","10","False","False","0","0",PLC_NAME_ID,TAG_NAME_ID,"False","True",ADDRESS_ID,"null","True","False","Local HMI","LW","False","False","0","null","",               "", "", "False","False","False","False"};
    private static final String[] RECORD_TEMPLATE_WORD = {"0","Middle","Word",PLC_NAME_ID,TAG_NAME_ID,"False","True",ADDRESS_ID,"null","16-bit Unsigned","False","False","Local HMI","LB","False","False","0","null","wd: ==",TRIGGER_VALUE_ID,COMMENT_ID,"False","","Arial",COLOR_ID,"11","False","","0","0","False","10","False","False","0","0",PLC_NAME_ID,TAG_NAME_ID,"False","True",ADDRESS_ID,"null","True","False","Local HMI","LW","False","False","0","null","16-bit Unsigned","0","0","False","False","False","False"};

    public EasyBuilderAlarmWriter(Path outputPath) {
        super(CSVFormat.EXCEL,
                outputPath, OUTPUT_FILE_NAME, OUTPUT_FILE_ENCODING,
                CSVWriter.TARGET_TAG_EASYBUILDER, CSVWriter.TARGET_TAG_ALARM);
    }

    @Override
    void onOpen() {
        write(asList(OUTPUT_FILE_HEADER));
    }

    public void write(EasyBuilderRecord record) {
        ArrayList<String> t = new ArrayList<>(); // record to write

        if (record.isDigital()) {
            t.addAll(asList(RECORD_TEMPLATE_BIT));
            renderTemplateCommon(t, record);
            write(t);
        } else {
            t.addAll(asList(RECORD_TEMPLATE_WORD));
            renderTemplateCommon(t,record);
            renderTemplateWord(t,record,TRIGGER_GENERAL, RED);
            write(t);
            t.clear();
            t.addAll(asList(RECORD_TEMPLATE_WORD));
            renderTemplateCommon(t,record);
            renderTemplateWord(t,record,TRIGGER_POTENTIAL, PURPLE);
            write(t);
        }
    }

    private void renderTemplateCommon(ArrayList<String> t, EasyBuilderRecord r) {
        for (int i = 0; i < t.size(); i++) {
            if (PLC_NAME_ID.equals(t.get(i))) t.set(i, r.getPlcName());
            if (TAG_NAME_ID.equals(t.get(i))) t.set(i, r.getName());
            if (ADDRESS_ID.equals(t.get(i)))  t.set(i, r.getAddressType() + "-" + r.getAddress());
            if (COMMENT_ID.equals(t.get(i)))  t.set(i, r.getComment());
        }
    }

    private void renderTemplateWord(ArrayList<String> t, EasyBuilderRecord r, String triggerValue, String color) {
        for (int i = 0; i < t.size(); i++) {
            if (COLOR_ID.equals(t.get(i))) t.set(i, color);
            if (TRIGGER_VALUE_ID.equals(t.get(i))) t.set(i, triggerValue);
        }
    }
}
