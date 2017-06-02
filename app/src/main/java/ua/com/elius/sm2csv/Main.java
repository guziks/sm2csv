package ua.com.elius.sm2csv;

import org.apache.commons.cli.*;
import ua.com.elius.sm2csv.reader.SoMachineReader;
import ua.com.elius.sm2csv.record.EasyBuilderRecord;
import ua.com.elius.sm2csv.record.SoMachineRecord;
import ua.com.elius.sm2csv.record.UnsupportedAddressException;
import ua.com.elius.sm2csv.record.WinccRecord;
import ua.com.elius.sm2csv.writer.EasyBuilderAlarmWriter;
import ua.com.elius.sm2csv.writer.EasyBuilderTagWriter;
import ua.com.elius.sm2csv.writer.WinccAlarmWriter;
import ua.com.elius.sm2csv.writer.WinccTagWriter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {
    private static final String OPTION_EXTENSION_SHORT = "e";
    private static final String OPTION_EXTENSION_LONG = "extension";
    private static final String OPTION_EXTENSION_DEFAULT_VALUE = "var";

    private static final String OPTION_PLC_NAME_SHORT = "n";
    private static final String OPTION_PLC_NAME_LONG = "plc-name";
    private static final String OPTION_PLC_NAME_DEFAULT_VALUE = "plc";

    private static final String OPTION_INCLUDE_ALL_SHORT = "a";
    private static final String OPTION_INCLUDE_ALL_LONG = "include-all";

    private static CommandLine mCmd;

    public static void main(String[] args) {
        prepareOptions(args);

        List<SoMachineRecord> smRecords = readSoMachineRecords();

        writeEasyBuilderTables(convertToEasyBuilderRecords(smRecords));
        writeWinccTables(convertToWinccRecords(smRecords));
    }

    private static List<SoMachineRecord> readSoMachineRecords() {
        List<SoMachineRecord> smRecords = new SoMachineReader.Builder()
                .path("")
                .extension(chooseExtension())
                .build()
                .read()
                .getRecords();

        smRecords.sort(Comparator.comparing(SoMachineRecord::getName));

        return smRecords;
    }

    private static List<EasyBuilderRecord> convertToEasyBuilderRecords(List<SoMachineRecord> smRecords) {
        List<EasyBuilderRecord> ebRecords = new ArrayList<>();
        for (SoMachineRecord smRec : smRecords) {
            try {
                EasyBuilderRecord ebRec = EasyBuilderRecord.fromSoMachineRecord(smRec);
                if (mCmd.hasOption(OPTION_INCLUDE_ALL_SHORT)) {
                    patchWithFakeAddress(ebRec, smRec);
                }
                ebRec.setPlcName(choosePlcName());
                if (ebRec.getAddress() != null) {
                    ebRecords.add(ebRec);
                }
            } catch (UnsupportedAddressException e) {
                e.printStackTrace();
            }
        }

        return  ebRecords;
    }

    private static List<WinccRecord> convertToWinccRecords(List<SoMachineRecord> smRecords) {
        List<WinccRecord> winccRecords = new ArrayList<>();
        for (SoMachineRecord smRec : smRecords) {
            try {
                WinccRecord winccRec = WinccRecord.fromSoMachineRecord(smRec);
                winccRec.setConnection(choosePlcName());
                if (winccRec.getAddress() != null) {
                    winccRecords.add(winccRec);
                }
            } catch (UnsupportedAddressException e) {
                e.printStackTrace();
            }
        }

        return  winccRecords;
    }

    private static void writeEasyBuilderTables(List<EasyBuilderRecord> ebRecords) {
        EasyBuilderTagWriter tagWriter = new EasyBuilderTagWriter();
        EasyBuilderAlarmWriter alarmWriter = new EasyBuilderAlarmWriter();

        writeDummy(tagWriter);

        for (EasyBuilderRecord ebRec : ebRecords) {
            tagWriter.write(ebRec);
            alarmWriter.write(ebRec);
        }

        tagWriter.close();
        alarmWriter.close();
    }

    private static void writeWinccTables(List<WinccRecord> winccRecords) {
        WinccTagWriter tagWriter = new WinccTagWriter();
        WinccAlarmWriter alarmWriter = new WinccAlarmWriter();
        alarmWriter.open();

        for (WinccRecord winccRec : winccRecords) {
            tagWriter.write(winccRec);
            alarmWriter.write(winccRec);
        }

        tagWriter.close();
        alarmWriter.close();
    }

    private static void writeDummy(EasyBuilderTagWriter writer) {
        EasyBuilderRecord dummyBit = new EasyBuilderRecord.Builder()
                .name("dummy_bit")
                .plcName(choosePlcName())
                .addressType("%MW_Bit")
                .address("999900")
                .build();
        EasyBuilderRecord dummyWord = new EasyBuilderRecord.Builder()
                .name("dummy_word")
                .plcName(choosePlcName())
                .addressType("%MW")
                .address("9998")
                .build();
        writer.write(dummyBit);
        writer.write(dummyWord);
    }

    private static void patchWithFakeAddress(EasyBuilderRecord ebRec, SoMachineRecord smRec) {
        if (!smRec.isExported()) {
            switch (smRec.getType()) {
                case "BOOL":
                    ebRec.setAddressType(EasyBuilderRecord.EB_ADDRESS_TYPE_DIGITAL_IN_ANALOG);
                    ebRec.setAddress("000");
                    break;
                default:
                    ebRec.setAddressType(EasyBuilderRecord.EB_ADDRESS_TYPE_ANALOG);
                    ebRec.setAddress("0");
                    break;
            }
        }
    }

    private static String chooseExtension() {
        if (mCmd.getOptionValue(OPTION_EXTENSION_SHORT) != null) {
            return mCmd.getOptionValue(OPTION_EXTENSION_SHORT);
        } else {
            return OPTION_EXTENSION_DEFAULT_VALUE;
        }
    }

    private static String choosePlcName() {
        if (mCmd.getOptionValue(OPTION_PLC_NAME_SHORT) != null) {
            return mCmd.getOptionValue(OPTION_PLC_NAME_SHORT);
        } else {
            return OPTION_PLC_NAME_DEFAULT_VALUE;
        }
    }

    private static void prepareOptions(String[] args) {
        Option inputFilesExt = Option.builder(OPTION_EXTENSION_SHORT)
                .longOpt(OPTION_EXTENSION_LONG)
                .argName("extension")
                .desc("search for files with this extension")
                .hasArg()
                .build();
        Option plcName = Option.builder(OPTION_PLC_NAME_SHORT)
                .longOpt(OPTION_PLC_NAME_LONG)
                .argName("name")
                .desc("plc name specified in EasyBuilder project System Parameters section")
                .hasArg()
                .build();
        Option includeAll = Option.builder(OPTION_INCLUDE_ALL_SHORT)
                .longOpt(OPTION_INCLUDE_ALL_LONG)
                .desc("include all tags, even if they have no address")
                .build();

        Options options = new Options();
        options.addOption(inputFilesExt);
        options.addOption(plcName);
        options.addOption(includeAll);

        CommandLineParser parser = new DefaultParser();
        try {
            mCmd = parser.parse( options, args );
        }
        catch( ParseException exp ) {
            System.err.println(exp.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("sm2csv", options);
            System.exit(1);
        }
    }
}
