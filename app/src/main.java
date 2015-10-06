import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import reader.SoMachineReader;
import record.EasyBuilderRecord;
import record.SoMachineRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class main {

    private static final String OPTION_SHORT_EXTENTION = "e";
    private static final String OPTION_LONG_EXTENTION = "extention";
    private static final String DEFAULT_EXTENTION = "var";

    private static CommandLine cmd;

    public static void main(String[] args) {
        prepareOptions(args);
        readFiles();
    }

    private static void readFiles() {
        CSVPrinter printer = openCSV();

        List<SoMachineRecord> smRecords = new SoMachineReader.Builder()
                .path("")
                .extention(chooseExtention())
                .build()
                .read()
                .getRecords();

        smRecords.sort((rec1, rec2) -> {
            return rec1.getName().compareTo(rec2.getName());
        });

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

        for (EasyBuilderRecord ebRec : ebRecords) {
            writeCSV(ebRec.toList(), printer);
        }

        closeCSV(printer);
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

    private static CSVPrinter openCSV() {
        CSVFormat format = CSVFormat.EXCEL;
        OutputStreamWriter writer = null;

        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream("tags.csv"), "windows-1251"
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        CSVPrinter printer = null;
        try {
            printer = new CSVPrinter(writer, format);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return printer;
    }

    private static void writeCSV(List<String> record, CSVPrinter printer) {
        try {
            printer.printRecord(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void closeCSV(CSVPrinter printer) {
        try {
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Printed");
    }
}
