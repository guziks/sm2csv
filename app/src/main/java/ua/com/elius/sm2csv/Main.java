package ua.com.elius.sm2csv;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import ua.com.elius.sm2csv.reader.SoMachineReader;
import ua.com.elius.sm2csv.record.EasyBuilderRecord;
import ua.com.elius.sm2csv.record.SoMachineRecord;
import ua.com.elius.sm2csv.record.UnsupportedAddressException;
import ua.com.elius.sm2csv.record.WinccRecord;
import ua.com.elius.sm2csv.writer.EasyBuilderAlarmWriter;
import ua.com.elius.sm2csv.writer.EasyBuilderTagWriter;
import ua.com.elius.sm2csv.writer.WinccAlarmWriter;
import ua.com.elius.sm2csv.writer.WinccTagWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Arrays.asList;

public class Main {

    private static final String OPTION_EXTENSION = "e";
    private static final String OPTION_PLC_NAME = "n";
    private static final String OPTION_INCLUDE_ALL = "a";
    private static final String OPTION_WORK_DIR = "d";
    private static final String OPTION_HELP = "h";

    private static final int EXIT_OK = 0;
    private static final int EXIT_ERROR = 1;

    /**
     * Parsed command line options
     */
    private static OptionSet opts;

    private static OptionSpec<String> specExtention;
    private static OptionSpec<String> specPlcName;
    private static OptionSpec<File> specWorkDir;

    public static void main(String[] args) {
        parseArgs(args);
        checkWorkDir();

        List<SoMachineRecord> smRecords = readSoMachineRecords();

        writeEasyBuilderTables(convertToEasyBuilderRecords(smRecords));
        writeWinccTables(convertToWinccRecords(smRecords));
    }

    /**
     * Top level action to read SoMachine variables
     *
     * @return list of {@link SoMachineRecord}
     */
    private static List<SoMachineRecord> readSoMachineRecords() {
        List<SoMachineRecord> smRecords = new SoMachineReader.Builder()
                .path(specWorkDir.value(opts).toPath())
                .extension(specExtention.value(opts))
                .build()
                .read()
                .getRecords();

        smRecords.sort(Comparator.comparing(SoMachineRecord::getName));

        return smRecords;
    }

    /**
     * Converts SoMachine records to EasyBuilder records
     *
     * @param smRecords SoMachine records list
     * @return EasyBuilder records list
     */
    private static List<EasyBuilderRecord> convertToEasyBuilderRecords(List<SoMachineRecord> smRecords) {
        List<EasyBuilderRecord> ebRecords = new ArrayList<>();
        for (SoMachineRecord smRec : smRecords) {
            try {
                EasyBuilderRecord ebRec = EasyBuilderRecord.of(smRec);
                if (opts.has(OPTION_INCLUDE_ALL)) {
                    patchWithFakeAddress(ebRec, smRec);
                }
                ebRec.setPlcName(specPlcName.value(opts));
                if (ebRec.getAddress() != null) {
                    ebRecords.add(ebRec);
                }
            } catch (UnsupportedAddressException e) {
                e.printStackTrace();
            }
        }

        return ebRecords;
    }

    /**
     * Converts SoMachine records to WinCC records
     *
     * @param smRecords SoMachine records
     * @return WinCC records list
     */
    private static List<WinccRecord> convertToWinccRecords(List<SoMachineRecord> smRecords) {
        List<WinccRecord> winccRecords = new ArrayList<>();
        for (SoMachineRecord smRec : smRecords) {
            try {
                WinccRecord winccRec = WinccRecord.of(smRec);
                winccRec.setConnection(specPlcName.value(opts));
                if (winccRec.getAddress() != null) {
                    winccRecords.add(winccRec);
                }
            } catch (UnsupportedAddressException e) {
                e.printStackTrace();
            }
        }

        return winccRecords;
    }

    /**
     * Top level action to write EasyBuilder targeted files
     *
     * @param ebRecords EasyBuilder records list
     */
    private static void writeEasyBuilderTables(List<EasyBuilderRecord> ebRecords) {
        EasyBuilderTagWriter tagWriter = new EasyBuilderTagWriter(specWorkDir.value(opts).toPath());
        EasyBuilderAlarmWriter alarmWriter = new EasyBuilderAlarmWriter(specWorkDir.value(opts).toPath());

        writeDummy(tagWriter);

        for (EasyBuilderRecord ebRec : ebRecords) {
            tagWriter.write(ebRec);
            alarmWriter.write(ebRec);
        }

        tagWriter.close();
        alarmWriter.close();
    }

    /**
     * Top level action to write WinCC targeted files
     *
     * @param winccRecords WinCC records list
     */
    private static void writeWinccTables(List<WinccRecord> winccRecords) {
        WinccTagWriter tagWriter = new WinccTagWriter(specWorkDir.value(opts).toPath());
        WinccAlarmWriter alarmWriter = new WinccAlarmWriter(specWorkDir.value(opts).toPath());

        for (WinccRecord winccRec : winccRecords) {
            tagWriter.write(winccRec);
            alarmWriter.write(winccRec);
        }

        tagWriter.close();
        alarmWriter.close();
    }

    /**
     * Write dummy tags
     * <p>
     * These are convenience tags to use as placeholders in HMI
     *
     * @param writer to write with
     */
    private static void writeDummy(EasyBuilderTagWriter writer) {
        EasyBuilderRecord dummyBit = new EasyBuilderRecord.Builder()
                .name("dummy_bit")
                .plcName(specPlcName.value(opts))
                .addressType("%MW_Bit")
                .address("999900")
                .build();
        EasyBuilderRecord dummyWord = new EasyBuilderRecord.Builder()
                .name("dummy_word")
                .plcName(specPlcName.value(opts))
                .addressType("%MW")
                .address("9998")
                .build();
        writer.write(dummyBit);
        writer.write(dummyWord);
    }

    /**
     * Assigns fake address to EasyBuilderRecord based on SoMachineRecord type
     * <p>
     * If SoMachineRecord does not have address then do nothing
     *
     * @param ebRec {@link EasyBuilderRecord} to patch
     * @param smRec {@link SoMachineRecord} to analyze
     */
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

    /**
     * Parses arguments and assigns a result to {@link #opts}
     *
     * @param args command line arguments
     */
    private static void parseArgs(String[] args) {
        OptionParser parser = new OptionParser();

        specExtention = parser.acceptsAll(asList(
                OPTION_EXTENSION, "extention"),
                "search for files with this extension")
                .withRequiredArg()
                .describedAs("extention")
                .defaultsTo("var");

        specPlcName = parser.acceptsAll(asList(
                OPTION_PLC_NAME, "plc-name"),
                "plc name specified in EasyBuilder project System Parameters section")
                .withRequiredArg()
                .describedAs("name")
                .defaultsTo("plc");

        specWorkDir = parser.acceptsAll(asList(
                OPTION_WORK_DIR, "directory"),
                "directory to start searching input files and output generated files")
                .withRequiredArg()
                .describedAs("dir")
                .ofType(File.class)
                .defaultsTo(new File("."));

        parser.acceptsAll(asList(
                OPTION_INCLUDE_ALL, "include-all"),
                "include all tags, even if they have no address");

        parser.acceptsAll(asList(OPTION_HELP, "?", "help")).forHelp();

        opts = parser.parse(args);

        if (opts.has(OPTION_HELP)) {
            try {
                parser.printHelpOn(System.out);
                System.exit(EXIT_OK);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if provided working directory is correct
     * <p>
     * Exits if provided File does not exist or
     * is not a directory
     */
    private static void checkWorkDir() {
        File dir = specWorkDir.value(opts);
        String path = dir.getAbsolutePath();
        if (!dir.exists()) {
            System.out.println(path + " does not exist");
            System.exit(EXIT_ERROR);
        } else if (!dir.isDirectory()) {
            System.out.println(path + " is not a directory");
            System.exit(EXIT_ERROR);
        }
    }
}
