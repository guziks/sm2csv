package ua.com.elius.sm2csv.reader;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import ua.com.elius.sm2csv.record.VijeoDesignerRecord;
import ua.com.elius.sm2csv.writer.VijeoDesignerWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VijeoDesignerReader {

    private File mInputFile;

    public VijeoDesignerReader(Path inputPath) {
        mInputFile = inputPath.resolve(VijeoDesignerWriter.FILE_NAME).toFile();
    }

    public List<VijeoDesignerRecord> read() throws IOException {
        List<VijeoDesignerRecord> records = new ArrayList<>();

        if (!mInputFile.exists() || !mInputFile.isFile()) {
            return null;
        }

        InputStream inputStream = new FileInputStream(mInputFile);
        inputStream.skip(2); // skip BOM

        CSVParser parser = CSVParser.parse(inputStream,
                Charset.forName(VijeoDesignerWriter.FILE_ENCODING),
                VijeoDesignerWriter.getCSVFormat());

        for (CSVRecord csvRec : parser) {
            if (csvRec.getRecordNumber() > 1) { // skip first record
                VijeoDesignerRecord vdRec = new VijeoDesignerRecord();
                vdRec.setType(csvRec.get(0));
                vdRec.setName(csvRec.get(1));
                vdRec.setDataType(csvRec.get(2));
                vdRec.setDataSource(csvRec.get(3));
                vdRec.setDimension(csvRec.get(4));
                vdRec.setDescription(csvRec.get(5));
                vdRec.setInitialValue(csvRec.get(6));
                vdRec.setNumofBytes(csvRec.get(7));
                vdRec.setDataSharing(csvRec.get(8));
                vdRec.setAlarm(csvRec.get(9));
                vdRec.setLanguage1ID(csvRec.get(10));
                vdRec.setAlarmMessage(csvRec.get(11));
                vdRec.setAlarmType(csvRec.get(12));
                vdRec.setTriggerCondition(csvRec.get(13));
                vdRec.setDeadband(csvRec.get(14));
                vdRec.setTarget(csvRec.get(15));
                vdRec.setAlarmLimits(csvRec.get(16));
                vdRec.setMinor(csvRec.get(17));
                vdRec.setMajor(csvRec.get(18));
                vdRec.setAlarmGroup(csvRec.get(19));
                vdRec.setSeverity(csvRec.get(20));
                vdRec.setVibrationPattern(csvRec.get(21));
                vdRec.setVibrationTime(csvRec.get(22));
                vdRec.setSoundFile(csvRec.get(23));
                vdRec.setPlayMode(csvRec.get(24));
                vdRec.setScanGroup(csvRec.get(25));
                vdRec.setDeviceAddress(csvRec.get(26));
                vdRec.setBitNumber(csvRec.get(27));
                vdRec.setDataFormat(csvRec.get(28));
                vdRec.setSigned(csvRec.get(29));
                vdRec.setDataLength(csvRec.get(30));
                vdRec.setOffsetBitNo(csvRec.get(31));
                vdRec.setBitWidth(csvRec.get(32));
                vdRec.setInputRange(csvRec.get(33));
                vdRec.setMin(csvRec.get(34));
                vdRec.setMax(csvRec.get(35));
                vdRec.setDataScaling(csvRec.get(36));
                vdRec.setRawMin(csvRec.get(37));
                vdRec.setRawMax(csvRec.get(38));
                vdRec.setScaledMin(csvRec.get(39));
                vdRec.setScaledMax(csvRec.get(40));
                vdRec.setIndirectEnabled(csvRec.get(41));
                vdRec.setIndirectAddress(csvRec.get(42));
                vdRec.setRetentive(csvRec.get(43));
                vdRec.setLoggingGroup(csvRec.get(44));
                vdRec.setLogUserOperationsOnVariable(csvRec.get(45));
                records.add(vdRec);
            }
        }

        parser.close();

        return records;
    }
}
