package ua.com.elius.sm2csv;

import org.apache.commons.cli.*;
import ua.com.elius.sm2csv.reader.SoMachineReader;
import ua.com.elius.sm2csv.record.EasyBuilderRecord;
import ua.com.elius.sm2csv.record.SoMachineRecord;
import ua.com.elius.sm2csv.writer.EasyBuilderTagWriter;
import ua.com.elius.sm2csv.writer.EeasyBuilderAlarmWriter;

import java.util.ArrayList;
import java.util.List;

public class main {

    private static final String OPTION_SHORT_EXTENTION = "e";
    private static final String OPTION_LONG_EXTENTION = "extention";
    private static final String DEFAULT_EXTENTION = "var";

    private static CommandLine cmd;

    public static void main(String[] args) {
        prepareOptions(args);
        writeEasyBuilderTables(
                convertToEasyBuilderRecords(
                        readSoMachineRecords()
                )
        );
    }

    private static List<SoMachineRecord> readSoMachineRecords() {

        List<SoMachineRecord> smRecords = new SoMachineReader.Builder()
                .path("")
                .extention(chooseExtention())
                .build()
                .read()
                .getRecords();

        smRecords.sort((rec1, rec2) -> {
            return rec1.getName().compareTo(rec2.getName());
        });

        return smRecords;
    }

    private static List<EasyBuilderRecord> convertToEasyBuilderRecords(List<SoMachineRecord> smRecords) {
        List<EasyBuilderRecord> ebRecords = new ArrayList<>();
        for (SoMachineRecord smRec : smRecords) {
            try {
                EasyBuilderRecord ebRec = EasyBuilderRecord.fromSoMachineRecord(smRec);
                patchWithFakeAddress(ebRec, smRec);
                ebRec.setPlcName("plc");
                ebRecords.add(ebRec);
            } catch (EasyBuilderRecord.UnsupportedAddressException e) {
                e.printStackTrace();
            }
        }

        return  ebRecords;
    }

    private static void writeEasyBuilderTables(List<EasyBuilderRecord> ebRecords) {
        EasyBuilderTagWriter tagWriter = new EasyBuilderTagWriter();
        EeasyBuilderAlarmWriter alarmWriter = new EeasyBuilderAlarmWriter();
        tagWriter.open();
        alarmWriter.open();

        writeDummy(tagWriter);

        for (EasyBuilderRecord ebRec : ebRecords) {
            tagWriter.write(ebRec);
            alarmWriter.write(ebRec);
        }

        tagWriter.close();
        alarmWriter.close();
    }

    private static void writeDummy(EasyBuilderTagWriter writer) {
        EasyBuilderRecord dummyBit = new EasyBuilderRecord.Builder()
                .name("dummy_bit")
                .plcName("plc")
                .addressType("%MW_Bit")
                .address("999900")
                .build();
        EasyBuilderRecord dummyWord = new EasyBuilderRecord.Builder()
                .name("dummy_word")
                .plcName("plc")
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

    private static String chooseExtention() {
        if (cmd.getOptionValue(OPTION_SHORT_EXTENTION) != null) {
            return cmd.getOptionValue(OPTION_SHORT_EXTENTION);
        } else {
            return DEFAULT_EXTENTION;
        }
    }

    private static void prepareOptions(String[] args) {
        Option inputFilesExt = Option.builder(OPTION_SHORT_EXTENTION)
                .longOpt(OPTION_LONG_EXTENTION)
                .argName("extention")
                .desc("search for files with this extention")
                .hasArg()
                .build();

        Options options = new Options();
        options.addOption(inputFilesExt);

        CommandLineParser parser = new DefaultParser();
        try {
            cmd = parser.parse( options, args );
        }
        catch( ParseException exp ) {
            System.err.println(exp.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("sm2csv", options);
            System.exit(1);
        }
    }
}
