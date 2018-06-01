package ua.com.elius.sm2csv.writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

public class CSVWriter {

    public static final String TARGET_TAG_WINCC = "WinCC";
    public static final String TARGET_TAG_EASYBUILDER = "EasyBuilder";
    public static final String TARGET_TAG_SIMPLESCADA = "SimpleScada";
    public static final String TARGET_TAG_LECTUS = "Lectus";
    public static final String TARGET_TAG_TAG = "tag";
    public static final String TARGET_TAG_ALARM = "alarm";

    private CSVPrinter mPrinter;
    private CSVFormat mFormat;
    private boolean mOpened;
    private Path mOutputPath;
    private String mOutputFileName;
    private String mOutputFileEncoding;
    private String mTargetSoftwareName; // e.g. EasyBuilder
    private String mTargetTypeName; // e.g. alarm, tag

    public CSVWriter(CSVFormat format,
                     Path outputPath, String outputFileName, String outputFileEncoding,
                     String targetSoftwareName, String targetTypeName) {
        mFormat = format;
        mOutputPath = outputPath;
        mOutputFileName = outputFileName;
        mOutputFileEncoding = outputFileEncoding;
        mTargetSoftwareName = targetSoftwareName;
        mTargetTypeName = targetTypeName;

        open();
    }

    public boolean isOpened() {
        return mOpened;
    }

    private void open(){
        OutputStreamWriter writer = null;
        File outputFile = mOutputPath.resolve(mOutputFileName).toFile();
        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream(outputFile), mOutputFileEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File " + mOutputFileName + " can not be opened");
        }

        if (writer == null) {
            return;
        }

        try {
            mPrinter = new CSVPrinter(writer, mFormat);
            mOpened = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mOpened) {
            onOpen();
        }
    }

    void onOpen() {}

    void write(List<String> record) {
        try {
            if (mPrinter != null) {
                mPrinter.printRecord(record);
            }
        } catch (IOException e) {
            System.out.println("Failed to write " + mTargetSoftwareName + " " + mTargetTypeName + " record");
        }
    }

    public void close() {
        try {
            if (mPrinter != null) {
                mPrinter.close();
            }
            mOpened = false;
        } catch (IOException e) {
            System.out.println("Failed to close " + mTargetSoftwareName + " " + mTargetTypeName + " file");
        }
    }
}
