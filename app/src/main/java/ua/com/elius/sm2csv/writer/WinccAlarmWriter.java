package ua.com.elius.sm2csv.writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import ua.com.elius.sm2csv.record.WinccRecord;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class WinccAlarmWriter {
    private static final String OUTPUT_FILE_NAME = "wincc-alarms.txt";
    private static final String OUTPUT_FILE_ENCODING = "UTF-16";

    private static final String[] HEADER1 = {"ALG_Alarm","Messages","Message","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""};
    private static final String[] HEADER2 = {"[ALARM_NUMBER][1][1]","[ALARM_EVENTTAG][6][1]","[ALARM_EVENTBIT][7][1]","[ALARM_STATETAG][8][1]","[ALARM_STATEBIT][9][1]","[ALARM_QUITTAG][10][1]","[ALARM_QUITBIT][11][1]","[ALARM_CLASS][2][1]","[ALARM_TYPE][3][1]","[ALARM_GROUP][4][1]","[ALARM_PRIORITY][5][1]","[ALARM_USERBLOCK_1_TEXTID][65637][1]","[ALARM_USERBLOCK_1_L1033][67698789][7]","[ALARM_USERBLOCK_2_TEXTID][65638][1]","[ALARM_USERBLOCK_2_L1033][67698790][7]","[ALARM_INFO][181][1]","[ALARM_PARAM_SINGLEQUIT][200][2]","[ALARM_PARAM_HORN][201][2]","[ALARM_PARAM_MSGARCH][202][2]","[ALARM_PARAM_NEGEDGE][203][2]","[ALARM_PARAM_ACTION][204][2]","[ALARM_PARAM_ADVANCED][205][2]","[ALARM_HIDMASK][214][1]","[ALARM_NORM][215][1]","[ALARM_LOOP][212][2]","[ALARM_ACTIONNAME][216][1]","[ALARM_ACTIONPARAM][217][1]","[ALARM_AG_NR][218][1]","[ALARM_CPU_NR][219][1]","[ALARM_S7PLUSADR][220][1]","[ALARM_S7PLUSALVER][221][1]","[ALARM_PRODUCERID][222][1]","[ALARM_CONNECTIONNAME][223][1]","[ALARM_CREATORID][224][1]"};
    private static final String[] HEADER3 = {"Number","Message tag","Message bit","Status tag","Status bit","Acknowledgment tag","Acknowledgment bit","Message class","Message Type","Message Group","Priority","Message text (ID)","Message text (ENU)","Point of error (ID)","Point of error (ENU)","Info text","Single acknowledgment","Central signaling device","Archived","Falling edge","Triggers action","Extended associated value data","Hide mask","Format DLL","Loop In Alarm","Function name","Function parameters","Controller number","CPU Number","Address","Version","Author ID","Connection","Author"};

    private static final String[] RECORD_TEMPLATE = {"<number>","<tag>","0","","0","","0","Error","Alarm","","0","0","<comment>","0","","","0","0","1","0","0","0","","","0","","","0","0","","0","0","","0"};
    private static final int NUMBER_INDEX = 0;
    private static final int TAG_INDEX = 1;
    private static final int COMMENT_INDEX = 12;

    private int writtenAmount;

    private CSVPrinter printer;

    public void open() {
        CSVFormat format = CSVFormat.DEFAULT
                .withDelimiter('\t')
                .withQuote(null);
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

        write(Arrays.asList(HEADER1));
        write(Arrays.asList(HEADER2));
        write(Arrays.asList(HEADER3));
    }

    public void write(WinccRecord record) {
        if (record.isAlarm()) {
            List<String> list = Arrays.asList(RECORD_TEMPLATE);
            list.set(NUMBER_INDEX, Integer.toString(++writtenAmount));
            list.set(TAG_INDEX, record.getName());
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
