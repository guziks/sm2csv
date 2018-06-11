package ua.com.elius.sm2csv.writer;

import org.apache.commons.csv.CSVFormat;
import ua.com.elius.sm2csv.record.VijeoDesignerRecord;

import java.nio.file.Path;

import static java.util.Arrays.asList;

public class VijeoDesignerWriter extends CSVWriter {

    private static final String OUTPUT_FILE_NAME = "vijeo-designer-vars.csv";
    private static final String OUTPUT_FILE_ENCODING = "UTF-16LE";

    private static final String[][] HEADERS = {
            {"'5.1.0","Vijeo-Designer 6.2.4 CSV output"},
            {"Type","Name","Data Type","Data Source","Dimension","Description","Initial Value","NumofBytes","Data Sharing","Alarm","Language1 ID","Alarm Message","Alarm Type","Trigger Condition","Deadband","Target","LoLo\\Lo\\Hi\\HiHi","Minor","Major","Alarm Group","Severity","Vibration Pattern","Vibration Time","Sound File","Play Mode","Scan Group","Device Address","Bit Number","Data Format","Signed","Data Length","Offset Bit No","Bit Width","InputRange","Min","Max","DataScaling","RawMin","RawMax","ScaledMin","ScaledMax","IndirectEnabled","IndirectAddress","Retentive","LoggingGroup","LogUserOperationsOnVariable"}
    };

    public VijeoDesignerWriter(Path outputPath) {
        super(CSVFormat.EXCEL.withDelimiter(';').withQuote(null),
                outputPath, OUTPUT_FILE_NAME, OUTPUT_FILE_ENCODING, true,
                CSVWriter.TARGET_TAG_VIJEODESIGNER, CSVWriter.TARGET_TAG_TAG);
    }

    @Override
    void onOpen() {
        for (String[] header : HEADERS) {
            write(asList(header));
        }
    }

    public void write(VijeoDesignerRecord record) {
        write(record.toList());
    }
}
