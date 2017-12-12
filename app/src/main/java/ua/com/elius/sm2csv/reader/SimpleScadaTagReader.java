package ua.com.elius.sm2csv.reader;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import ua.com.elius.sm2csv.record.SimpleScadaRecord;
import ua.com.elius.sm2csv.writer.SimpleScadaTagWriter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SimpleScadaTagReader {

    private File mInputFile;

    public SimpleScadaTagReader(Path inputPath) {
        mInputFile = inputPath.resolve(SimpleScadaTagWriter.FILE_NAME).toFile();
    }

    public List<SimpleScadaRecord> read() throws IOException {
        List<SimpleScadaRecord> records = new ArrayList<>();

        if (!mInputFile.exists() || !mInputFile.isFile()) {
            return null;
        }

        CSVParser parser = CSVParser.parse(mInputFile,
                Charset.forName(SimpleScadaTagWriter.FILE_ENCODING),
                SimpleScadaTagWriter.getCSVFormat());

        String groupName = "";
        for (CSVRecord csvRec : parser) {
            if (csvRec.getRecordNumber() == 3) {
                groupName = csvRec.get(0);
            }
            if (csvRec.getRecordNumber() > 3) {
                SimpleScadaRecord ssRec = new SimpleScadaRecord();
                ssRec.setTagGroup(groupName);
                ssRec.setOpcNode(groupName);
                ssRec.setName(csvRec.get(1));
                ssRec.setDataType(csvRec.get(2));
                ssRec.setArchiveType(csvRec.get(3));
                ssRec.setTrendType(csvRec.get(4));
                ssRec.setDeadband(csvRec.get(5));
                ssRec.setArchveInterval(csvRec.get(6));
                ssRec.setScaleName(csvRec.get(7));
                ssRec.setScaleUnits(csvRec.get(8));
                ssRec.setScaleMin(csvRec.get(9));
                ssRec.setScaleMax(csvRec.get(10));
                ssRec.setComment(csvRec.get(11));
                ssRec.setTagType(csvRec.get(12));
                ssRec.setAddress(csvRec.get(13));
                ssRec.setPcName(csvRec.get(14));
                ssRec.setOpcServer(csvRec.get(15));
                ssRec.setSamplingFrequency(csvRec.get(16));
                ssRec.setFormat(csvRec.get(17));
                ssRec.setShift(csvRec.get(18));
                ssRec.setAlarmHigh(csvRec.get(19));
                ssRec.setWarnHigh(csvRec.get(20));
                ssRec.setWarnLow(csvRec.get(21));
                ssRec.setAlarmLow(csvRec.get(22));
                ssRec.setVisualMin(csvRec.get(23));
                ssRec.setVisualMax(csvRec.get(24));
                records.add(ssRec);
            }
        }

        parser.close();

        return records;
    }
}
