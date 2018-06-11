package ua.com.elius.sm2csv;

import com.esotericsoftware.yamlbeans.YamlException;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import ua.com.elius.sm2csv.alarm.AlarmInfo;
import ua.com.elius.sm2csv.model.alarmconfig.*;
import ua.com.elius.sm2csv.reader.*;
import ua.com.elius.sm2csv.reader.AlarmConfigReader.AlarmConfigException;
import ua.com.elius.sm2csv.record.*;
import ua.com.elius.sm2csv.writer.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;

public class Main {

    private static final String OPTION_HELP = "h";
    private static final String OPTION_EXTENSION = "e";
    private static final String OPTION_PLC_NAME = "n";
    private static final String OPTION_INCLUDE_ALL = "a";
    private static final String OPTION_WORK_DIR = "d";
    private static final String OPTION_ALARM_PREFIXES = "p";
    private static final String OPTION_TARGETS = "t";
    private static final String OPTION_ALARM_CONFIG = "alarm-config";
    private static final String OPTION_SYMBOL_CONFIG = "f";

    private static final String TARGET_EASYBUILDER = "easybuilder";
    private static final String TARGET_WINCC = "wincc";
    private static final String TARGET_SIMPLESCADA = "simplescada";
    private static final String TARGET_LECTUS = "lectus";
    private static final String TARGET_VIJEODESIGNER = "vijeodesigner";

    private static final int EXIT_OK = 0;
    private static final int EXIT_ERROR = 1;

    /**
     * Parsed command line options
     */
    private static OptionSet opts;

    private static OptionSpec<String> specExtention;
    private static OptionSpec<String> specPlcName;
    private static OptionSpec<File> specWorkDir;
    private static OptionSpec<String> specAlarmPrefixes;
    private static OptionSpec<String> specTargets;
    private static OptionSpec<Integer> specSimpleScadaIdShift;
    private static OptionSpec<File> specAlarmConfig;
    private static OptionSpec<File> specSymbolConfig;

    public static void main(String[] args) {
        parseArgs(args);
        checkWorkDir();

        List<SoMachineRecord> smRecords = readSoMachineRecords();
        checkIfRecordsFound(smRecords);

        AlarmConfig alarmConfig = readAlarmConfig();
        Map<String,AlarmInfo> alarmInfoMap = createAlarmInfoMap(alarmConfig, smRecords);

        if (haveTarget(TARGET_EASYBUILDER)) {
            writeEasyBuilderTables(convertToEasyBuilderRecords(smRecords), alarmInfoMap);
        }
        if (haveTarget(TARGET_WINCC)) {
            writeWinccTables(convertToWinccRecords(smRecords));
        }
        if (haveTarget(TARGET_SIMPLESCADA)) {
            writeSimpleScadaTables(convertToSimpleScadaRecords(smRecords), alarmInfoMap);
        }
        if (haveTarget(TARGET_LECTUS)) {
            writeLectusTables(convertToLectusRecords(smRecords));
        }
        if (haveTarget(TARGET_VIJEODESIGNER)) {
            writeVijeoDesignerTables(convertToVijeoDesignerRecords(smRecords, alarmInfoMap));
        }
    }

    /**
     * Top level action to read SoMachine variables
     *
     * @return list of {@link SoMachineRecord}
     */
    private static List<SoMachineRecord> readSoMachineRecords() {
        List<SoMachineRecord> smRecords = new ArrayList<SoMachineRecord>();

        List<SoMachineRecord> dirRecords = new SoMachineDirReader.Builder()
                .path(specWorkDir.value(opts).toPath())
                .extension(specExtention.value(opts))
                .build()
                .read()
                .getRecords();
        smRecords.addAll(dirRecords);

        File symbolConfig = specSymbolConfig.value(opts);
        if (symbolConfig.exists()) {
            SoMachineXmlReader xmlReader = new SoMachineXmlReader(symbolConfig);
            List<SoMachineRecord> xmlRecords = null;
            try {
                xmlRecords = xmlReader.read();
            } catch (IOException e) {
                System.out.println("Failed to read SoMachine symbol config file");;
            }
            if (xmlRecords != null) {
                smRecords.addAll(xmlRecords);
            }
        }

        smRecords.sort(Comparator.comparing(SoMachineRecord::getName));
        return smRecords;
    }

    /**
     * Reads alarm configuration file
     *
     * @return Alarm configuration object
     */
    private static AlarmConfig readAlarmConfig() {
        AlarmConfig config = null;
        try {
            AlarmConfigReader configReader = new AlarmConfigReader(specAlarmConfig.value(opts));
            config = configReader.read();
        } catch (FileNotFoundException e) {
            if (opts.has(OPTION_ALARM_CONFIG)) {
                System.out.println("WARNING: Alarm config file can not be opened, using default");;
            }
        } catch (YamlException e) {
            System.out.println("WARNING: Alarm config is not correct, using default");;
        } catch (AlarmConfigException e) {
            System.out.println("WARNING: Alarm config logic is not correct, using default");;
        }
        // default config
        if (config == null) {
            config = new AlarmConfig();
            config.digital = new HashMap<>();
            config.digital.put("^f_", new Digital(Severity.high));
            config.digital.put("^break_", new Digital(Severity.high));
            config.numeric = new HashMap<>();
            List<Message> messages = new ArrayList<>();
            messages.add(new Message(2, Severity.high, ""));
            config.numeric.put("^sta_", new Numeric(messages));
        }
        return config;
    }

    /**
     * Creates alarm information map
     * <p>
     * This map can be used to get alarm information by tag name
     *
     * @param config Alarm configuration
     * @param records SoMachine records list
     * @return Tag name to AlarmInfo map
     */
    private static Map<String,AlarmInfo> createAlarmInfoMap(AlarmConfig config, List<SoMachineRecord> records) {
        Map<String,AlarmInfo> map = new HashMap<>();
        for (SoMachineRecord record : records) {
            if (record.isExported()) {
                AlarmInfo info = new AlarmInfo(config, record.getName(), record.getAddress().isDigital());
                map.put(record.getName(), info);
            }
        }
        return map;
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
                if (opts.has(OPTION_INCLUDE_ALL)) { // TODO move to top level
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
     * Converts SoMachine records to SimpleScada records
     *
     * @param smRecords SoMachine records
     * @return SimpleScada records list
     */
    private static List<SimpleScadaRecord> convertToSimpleScadaRecords(List<SoMachineRecord> smRecords) {
        List<SimpleScadaRecord> ssRecords = new ArrayList<>();
        for (SoMachineRecord smRec : smRecords) {
            SimpleScadaRecord ssRec = SimpleScadaRecord.of(smRec);
            if (smRec.isExported()) {
                ssRecords.add(ssRec);
            }
        }

        return ssRecords;
    }

    /**
     * Converts SoMachine records to Lectus records
     *
     * @param smRecords SoMachine records
     * @return Lectus records
     */
    private static List<LectusRecord> convertToLectusRecords(List<SoMachineRecord> smRecords) {
        List<LectusRecord> lectusRecords = new ArrayList<>();
        for (SoMachineRecord smRec : smRecords) {
            if (smRec.isExported()) {
                LectusRecord rec = LectusRecord.of(smRec);
                lectusRecords.add(rec);
            }
        }

        return lectusRecords;
    }

    /**
     * Converts SoMachine records to VijeoDesigner records
     *
     * @param smRecords SoMachine records
     * @param alarmInfo Map of tag names to {@link AlarmInfo}
     * @return VijeoDesigner records
     */
    private static List<VijeoDesignerRecord> convertToVijeoDesignerRecords(List<SoMachineRecord> smRecords, Map<String,AlarmInfo> alarmInfo) {
        List<VijeoDesignerRecord> vdRecords = new ArrayList<>();
        for (SoMachineRecord smRec : smRecords) {
            if (smRec.isExported()) {
                AlarmInfo info = alarmInfo.get(smRec.getName());
                VijeoDesignerRecord rec = null;
                try {
                    rec = VijeoDesignerRecord.of(smRec, info);
                    vdRecords.add(rec);
                } catch (TypeMapException e) {
                    System.out.println("WARNING: Vijeo Designer: " + e.getMessage() + " type is not supported");
                } catch (LongNameException e) {
                    System.out.println("WARNING: Vijeo Designer: '" + e.getMessage() + "' name is too long (max=32)");
                }
            }
        }

        return vdRecords;
    }

    /**
     * Top level action to write EasyBuilder targeted files
     *
     * @param ebRecords EasyBuilder records list
     * @param alarmInfo Map of tag names to {@link AlarmInfo}
     */
    private static void writeEasyBuilderTables(List<EasyBuilderRecord> ebRecords, Map<String,AlarmInfo> alarmInfo) {
        EasyBuilderTagWriter tagWriter = new EasyBuilderTagWriter(specWorkDir.value(opts).toPath());
        EasyBuilderAlarmWriter alarmWriter = new EasyBuilderAlarmWriter(specWorkDir.value(opts).toPath());

        writeDummy(tagWriter);

        for (EasyBuilderRecord ebRec : ebRecords) {
            tagWriter.write(ebRec);

            AlarmInfo info = alarmInfo.get(ebRec.getName());
            if (info.isAlarm()) {
                alarmWriter.write(ebRec, info);
            }
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
            if (winccRec.isAlarm(specAlarmPrefixes.values(opts))) {
                alarmWriter.write(winccRec);
            }
        }

        tagWriter.close();
        alarmWriter.close();
    }

    /**
     * Top level action to write SimpleScada targeted files
     *
     * @param newRecords SimpleScada records list
     * @param alarmInfo Map of tag names to {@link AlarmInfo}
     */
    private static void writeSimpleScadaTables(List<SimpleScadaRecord> newRecords, Map<String,AlarmInfo> alarmInfo) {
        // read existing file
        SimpleScadaTagReader tagReader = new SimpleScadaTagReader(specWorkDir.value(opts).toPath());
        List<SimpleScadaRecord> existRecords = null;
        try {
            existRecords = tagReader.read();
        } catch (IOException e) {
            System.out.println("Failed to read existing SimpleScada tags, skip update");
            return;
        }

        // merge
        if (existRecords != null) {
            for (SimpleScadaRecord nRec : newRecords) {
                for (SimpleScadaRecord eRec : existRecords) {
                    if (eRec.getName().equals(nRec.getName())) {
                        nRec.merge(eRec);
                    }
                }
            }
        }

        // write tags
        SimpleScadaTagWriter tagWriter = new SimpleScadaTagWriter(specWorkDir.value(opts).toPath());
        for (SimpleScadaRecord rec : newRecords) {
            tagWriter.write(rec);
        }
        tagWriter.close();

        // read variable IDs
        Map<String,Integer> varIds = new HashMap<>();
        try {
            SimpleScadaVarReader varReader = new SimpleScadaVarReader(specWorkDir.value(opts).toPath());
            varIds = varReader.read();
        } catch (IOException e) {
            System.out.println("WARNING: Unable to read SimpleScada variables file, messages file may not be correct");
        }

        // write alarms
        SimpleScadaAlarmWriter alarmWriter = null;
        try {
            alarmWriter = new SimpleScadaAlarmWriter(
                    specWorkDir.value(opts).toPath(),
                    specSimpleScadaIdShift.value(opts),
                    alarmInfo,
                    varIds);
            alarmWriter.write(newRecords);
        } catch (FileNotFoundException e) {
            System.out.println("SimpleScada alarm file can not be opened");
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Failed to write to SimpleScada alarm file");
            System.err.println(e.getMessage());
        } finally {
            if (alarmWriter != null) {
                alarmWriter.close();
            }
        }

        // read  assignment script
        SimpleScadaAssignmentReader aReader = new SimpleScadaAssignmentReader(specWorkDir.value(opts).toPath());
        List<String> toUncomment = null;
        try {
            toUncomment = aReader.read();
        } catch (IOException e) {
            System.out.println("Failed to read existing SimpleScada assign script, skip update");
            return;
        }

        // write assignment script
        SimpleScadaAssignmentWriter aWriter = null;
        try {
            aWriter = new SimpleScadaAssignmentWriter(specWorkDir.value(opts).toPath());
            aWriter.write(newRecords, toUncomment);
        } catch (IOException e) {
            System.out.println("Failed to write to SimpleScada assign script");
            System.err.println(e.getMessage());
        } finally {
            if (aWriter != null) {
                aWriter.close();
            }
        }
    }

    /**
     * Top level action to write Lectus targeted files
     *
     * @param lectusRecords Lectus records
     */
    private static void writeLectusTables(List<LectusRecord> lectusRecords) {
        LectusTagWriter tagWriter = new LectusTagWriter(specWorkDir.value(opts).toPath());

        for (LectusRecord rec : lectusRecords) {
            tagWriter.write(rec);
        }

        tagWriter.close();
    }

    /**
     * Top level action to write VijeoDesigner targeted files
     *
     * @param vdRecords VijeoDesigner records
     */
    private static void writeVijeoDesignerTables(List<VijeoDesignerRecord> vdRecords) {
        VijeoDesignerWriter writer = new VijeoDesignerWriter(specWorkDir.value(opts).toPath());

        for (VijeoDesignerRecord vdRec : vdRecords) {
            writer.write(vdRec);
        }

        writer.close();
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

        parser.acceptsAll(asList(OPTION_HELP, "?", "help")).forHelp();

        specExtention = parser.acceptsAll(asList(
                OPTION_EXTENSION, "extention"),
                "Search for files with this extension")
                .withRequiredArg()
                .describedAs("extention")
                .defaultsTo("var");

        specPlcName = parser.acceptsAll(asList(
                OPTION_PLC_NAME, "plc-name"),
                "PLC name specified in EasyBuilder project System Parameters section")
                .withRequiredArg()
                .describedAs("name")
                .defaultsTo("plc");

        specWorkDir = parser.acceptsAll(asList(
                OPTION_WORK_DIR, "directory"),
                "Directory to start searching input files and output generated files")
                .withRequiredArg()
                .describedAs("dir")
                .ofType(File.class)
                .defaultsTo(new File("."));

        parser.acceptsAll(asList(
                OPTION_INCLUDE_ALL, "include-all"),
                "Include all tags, even if they have no address");

        specAlarmPrefixes = parser.acceptsAll(asList(
                OPTION_ALARM_PREFIXES, "alarm-prefixes"),
                "Tag name prefixes to recognize alarms")
                .withRequiredArg()
                .describedAs("p,p,...")
                .withValuesSeparatedBy(',')
                .defaultsTo("f_", "break_", "sta_");

        specTargets = parser.acceptsAll(asList(
                OPTION_TARGETS, "targets"),
                "Targets to generate files for. See defaults for available targets")
                .withRequiredArg()
                .describedAs("t,t,...")
                .withValuesSeparatedBy(',')
                .defaultsTo(TARGET_EASYBUILDER, TARGET_WINCC, TARGET_SIMPLESCADA, TARGET_LECTUS, TARGET_VIJEODESIGNER);

        specSimpleScadaIdShift = parser.accepts(
                "simplescada-id-shift",
                "First tag ID in SimpleScada IDE")
                .withRequiredArg()
                .describedAs("shift")
                .ofType(Integer.class)
                .defaultsTo(0);

        specAlarmConfig = parser.accepts(
                OPTION_ALARM_CONFIG,
                "Alarm states configuration file")
                .withRequiredArg()
                .describedAs("file")
                .ofType(File.class)
                .defaultsTo(new File("alarm-config.yaml"));

        specSymbolConfig = parser.acceptsAll(asList(
                OPTION_SYMBOL_CONFIG, "symbol-config"),
                "Symbol configuration XML file")
                .withRequiredArg()
                .describedAs("file")
                .ofType(File.class)
                .defaultsTo(new File("tags.xml"));

        try {
            opts = parser.parse(args);
        } catch (OptionException e) {
            System.out.println(e.getMessage());
            System.out.println();
            try {
                parser.printHelpOn(System.out);
            } catch (IOException e1) {}
            System.exit(EXIT_ERROR);
        }

        if (opts.has(OPTION_HELP)) {
            try {
                parser.printHelpOn(System.out);
                System.exit(EXIT_OK);
            } catch (IOException e) {}
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

    /**
     * Checks if list contains records, if not then exit
     *
     * @param smRecords list to check
     */
    private static void checkIfRecordsFound(List<SoMachineRecord> smRecords) {
        if (smRecords.size() == 0) {
            // nothing to do
            System.out.println("SoMachine records not found");
            System.exit(EXIT_OK);
        }
    }

    /**
     * Checks if target asked by the user within options
     *
     * @param target to check
     * @return <code>true</code> if target asked for,
     * else <code>false</code>
     */
    private static boolean haveTarget(String target) {
        for (String t : specTargets.values(opts)) {
            if (target.equals(t.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
