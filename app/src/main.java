import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import record.EasyBuilderRecord;
import record.SoMachineRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        final String extention;
        if (cmd.getOptionValue(OPTION_SHORT_EXTENTION) != null) {
            extention = cmd.getOptionValue(OPTION_SHORT_EXTENTION);
        } else {
            extention = DEFAULT_EXTENTION;
        }
        CSVPrinter printer = openCSV();
        try {
            Files.walk(Paths.get("")).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    String curExtention = FilenameUtils.getExtension(filePath.toString());
                    if (curExtention.equals(extention)) {
                        try {
                            Files.lines(filePath).forEach(line -> {
                                if (Character.isWhitespace(line.charAt(0))) {
                                    try {
                                        EasyBuilderRecord ebRec =
                                            EasyBuilderRecord.fromSoMachineRecord(SoMachineRecord.fromString(line));
                                        writeCSV(ebRec.toList(), printer);
                                    } catch (EasyBuilderRecord.UnsupportedAddressException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeCSV(printer);
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
