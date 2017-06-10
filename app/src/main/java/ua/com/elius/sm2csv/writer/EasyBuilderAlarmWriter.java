package ua.com.elius.sm2csv.writer;

import org.apache.commons.csv.CSVFormat;
import ua.com.elius.sm2csv.record.EasyBuilderRecord;

import java.nio.file.Path;
import java.util.ArrayList;

import static java.util.Arrays.asList;
import static ua.com.elius.sm2csv.writer.Util.findAndReplace;

public class EasyBuilderAlarmWriter extends CSVWriter {

    private static final String OUTPUT_FILE_NAME = "easybuilder-alarms.csv";
    private static final String OUTPUT_FILE_ENCODING = "windows-1251";
    private static final String[] OUTPUT_FILE_HEADER = {"Category","Priority","Address Type","PLC Name (Read)","Device Type (Read)","System Tag (Read)","User-defined Tag (Read)","Address (Read)","Index (Read)","Data Format (Read)","Enable Notification","Set ON (Notification)","PLC Name (Notification)","Device Type (Notification)","System Tag (Notification)","User-defined Tag (Notification)","Address (Notification)","Index (Notification)","Condition","Trigger Value","Content","Use Label Library","Label Name","Font","Color","Acknowledge Value","Enable Sound","Sound Library Name","Sound Index","No. of Multi-watch","Continuous Beep","Time Interval of Beeps","Send eMail when Event Triggered","Send eMail when Event Cleared","Delay Time","Dynamic Condition","PLC Name (Condition)","Device Type (Condition)","System Tag (Condition)","User-defined Tag (Condition)","Address (Condition)","Index (Condition)"};

    private static final String ID_PLC_NAME = "${plc_name}";
    private static final String ID_TAG_NAME = "${tag_name}";
    private static final String ID_ADDRESS = "${address_type_and_address}";
    private static final String ID_COMMENT = "${comment}";
    private static final String ID_TRIGGER_VALUE = "${trigger_value}";
    private static final String ID_COLOR = "${color}";

    private static final String COLOR_RED = "139:0:0";
    private static final String COLOR_PURPLE = "139:0:139";

    private static final String TRIGGER_GENERAL = "2";
    private static final String TRIGGER_POTENTIAL = "3";

    private static final String[] RECORD_TEMPLATE_BIT =  {"0","Middle","Bit", ID_PLC_NAME, ID_TAG_NAME,"False","True", ID_ADDRESS,"null","",               "False","False","Local HMI","LB","False","False","0","null","bt: 1", "0",             ID_COMMENT,"False","","Arial",COLOR_RED,"11","False","","0","0","False","10","False","False","0","0", ID_PLC_NAME, ID_TAG_NAME,"False","True", ID_ADDRESS,"null","True","False","Local HMI","LW","False","False","0","null","",               "", "", "False","False","False","False"};
    private static final String[] RECORD_TEMPLATE_WORD = {"0","Middle","Word",ID_PLC_NAME, ID_TAG_NAME,"False","True", ID_ADDRESS,"null","16-bit Unsigned","False","False","Local HMI","LB","False","False","0","null","wd: ==",ID_TRIGGER_VALUE,ID_COMMENT,"False","","Arial",ID_COLOR, "11","False","","0","0","False","10","False","False","0","0", ID_PLC_NAME, ID_TAG_NAME,"False","True", ID_ADDRESS,"null","True","False","Local HMI","LW","False","False","0","null","16-bit Unsigned","0","0","False","False","False","False"};

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
            // general alarm
            t.addAll(asList(RECORD_TEMPLATE_WORD));
            renderTemplateCommon(t,record);
            renderTemplateWord(t,record,TRIGGER_GENERAL, COLOR_RED);
            write(t);
            // potential alarm
            t.clear();
            t.addAll(asList(RECORD_TEMPLATE_WORD));
            renderTemplateCommon(t,record);
            renderTemplateWord(t,record,TRIGGER_POTENTIAL, COLOR_PURPLE);
            write(t);
        }
    }

    private void renderTemplateCommon(ArrayList<String> t, EasyBuilderRecord r) {
        findAndReplace(t, ID_PLC_NAME, r.getPlcName());
        findAndReplace(t, ID_TAG_NAME, r.getName());
        findAndReplace(t, ID_ADDRESS, r.getAddressType() + "-" + r.getAddress());
        findAndReplace(t, ID_COMMENT, r.getComment());
    }

    private void renderTemplateWord(ArrayList<String> t, EasyBuilderRecord r, String triggerValue, String color) {
        findAndReplace(t, ID_COLOR, color);
        findAndReplace(t, ID_TRIGGER_VALUE, triggerValue);
    }
}
