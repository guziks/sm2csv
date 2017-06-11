package ua.com.elius.sm2csv.writer;

import org.apache.commons.csv.CSVFormat;
import ua.com.elius.sm2csv.record.EasyBuilderRecord;

import java.nio.file.Path;
import java.util.ArrayList;

import static java.util.Arrays.asList;
import static java.util.Collections.replaceAll;

public class EasyBuilderAlarmWriter extends CSVWriter {

    private static final String OUTPUT_FILE_NAME = "easybuilder-alarms.csv";
    private static final String OUTPUT_FILE_ENCODING = "windows-1251";
    private static final String[] OUTPUT_FILE_HEADER = {"Category","Priority","Address Type","PLC Name (Read)","Device Type (Read)","System Tag (Read)","User-defined Tag (Read)","Address (Read)","Index (Read)","Data Format (Read)","Enable Notification","Set ON (Notification)","PLC Name (Notification)","Device Type (Notification)","System Tag (Notification)","User-defined Tag (Notification)","Address (Notification)","Index (Notification)","Condition","Trigger Value","Content","Use Label Library","Label Name","Font","Color","Acknowledge Value","Enable Sound","Sound Library Name","Sound Index","No. of Multi-watch","Continuous Beep","Time Interval of Beeps","Send eMail when Event Triggered","Send eMail when Event Cleared","Delay Time","Dynamic Condition","PLC Name (Condition)","Device Type (Condition)","System Tag (Condition)","User-defined Tag (Condition)","Address (Condition)","Index (Condition)"};

    private static final String ID_PLC_NAME = "${plc_name}";
    private static final String ID_TAG_NAME = "${tag_name}";
    private static final String ID_ADDRESS = "${address_type_and_address}";
    private static final String ID_COMMENT = "${comment}";

    private static final String COLOR_RED = "139:0:0";
    private static final String COLOR_PURPLE = "139:0:139";

    private static final String[] RECORD_TEMPLATE_BIT =            {"0","Middle","Bit", ID_PLC_NAME, ID_TAG_NAME,"False","True", ID_ADDRESS,"null","",               "False","False","Local HMI","LB","False","False","0","null","bt: 1", "0",ID_COMMENT,"False","","Arial",COLOR_RED,   "11","False","","0","0","False","10","False","False","0","0", ID_PLC_NAME, ID_TAG_NAME,"False","True", ID_ADDRESS,"null","True","False","Local HMI","LW","False","False","0","null","",               "", "", "False","False","False","False"};
    private static final String[] RECORD_TEMPLATE_WORD_GENERAL =   {"0","Middle","Word",ID_PLC_NAME, ID_TAG_NAME,"False","True", ID_ADDRESS,"null","16-bit Unsigned","False","False","Local HMI","LB","False","False","0","null","wd: ==","2",ID_COMMENT,"False","","Arial",COLOR_RED,   "11","False","","0","0","False","10","False","False","0","0", ID_PLC_NAME, ID_TAG_NAME,"False","True", ID_ADDRESS,"null","True","False","Local HMI","LW","False","False","0","null","16-bit Unsigned","0","0","False","False","False","False"};
    private static final String[] RECORD_TEMPLATE_WORD_POTENTIAL = {"1","Middle","Word",ID_PLC_NAME, ID_TAG_NAME,"False","True", ID_ADDRESS,"null","16-bit Unsigned","False","False","Local HMI","LB","False","False","0","null","wd: ==","3",ID_COMMENT,"False","","Arial",COLOR_PURPLE,"11","False","","0","0","False","10","False","False","0","0", ID_PLC_NAME, ID_TAG_NAME,"False","True", ID_ADDRESS,"null","True","False","Local HMI","LW","False","False","0","null","16-bit Unsigned","0","0","False","False","False","False"};

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
            renderTemplate(t, record);
            write(t);
        } else {
            // general alarm
            t.addAll(asList(RECORD_TEMPLATE_WORD_GENERAL));
            renderTemplate(t,record);
            write(t);
            // potential alarm
            t.clear();
            t.addAll(asList(RECORD_TEMPLATE_WORD_POTENTIAL));
            renderTemplate(t,record);
            write(t);
        }
    }

    private void renderTemplate(ArrayList<String> t, EasyBuilderRecord r) {
        replaceAll(t, ID_PLC_NAME, r.getPlcName());
        replaceAll(t, ID_TAG_NAME, r.getName());
        replaceAll(t, ID_ADDRESS, r.getAddressType() + "-" + r.getAddress());
        replaceAll(t, ID_COMMENT, r.getComment());
    }
}
