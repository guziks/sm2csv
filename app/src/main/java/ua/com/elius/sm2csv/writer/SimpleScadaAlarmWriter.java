package ua.com.elius.sm2csv.writer;

import com.google.common.io.LittleEndianDataOutputStream;
import ua.com.elius.sm2csv.record.SimpleScadaRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public class SimpleScadaAlarmWriter {

    /**
     * SimpleScada messages file name
     */
    private static final String OUTPUT_FILE_NAME = "Messages.smg";

    /**
     * Trigger for digital alarms, i.e. boolean tags
     */
    private static final int TRIGGER_DIGIAL = 1;

    /**
     * Trigger for analog (not digital) alarms, i.g. word, real,...
     *
     * This value is ELIUS-M convention
     */
    private static final int TRIGGER_ANALOG = 2;

    private List<String> mAlarmPrefixes;
    private BufferedOutputStream mBufferedOutputStream;
    private DataOutput mOut;
    private int mIdShift;

    public SimpleScadaAlarmWriter(Path outputPath, List<String> alarmPrefixes, int idShift) throws FileNotFoundException {
        mAlarmPrefixes = alarmPrefixes;
        mBufferedOutputStream = new BufferedOutputStream(
                new FileOutputStream(outputPath.resolve(OUTPUT_FILE_NAME).toFile()));
        mOut = new LittleEndianDataOutputStream(mBufferedOutputStream);
        mIdShift = idShift;
    }

    public void write(List<SimpleScadaRecord> records) throws IOException {
        int messagesCount = (int) records.stream()
                .filter(r -> r.isAlarm(mAlarmPrefixes))
                .count();
        writeHeader(messagesCount);
        // iterating over not filtered list because we need original indexes
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).isAlarm(mAlarmPrefixes)) {
                writeMessage(i,
                        "msg_" + records.get(i).getName(),
                        records.get(i).getComment(),
                        chooseTrigger(records.get(i)));
            }
        }
        writeEnd();
    }

    private void writeHeader(int messagesCount) throws IOException {
        mOut.writeInt(messagesCount);
        mOut.writeInt(1);
        mOut.writeInt(1);
        mOut.writeInt(0);
        mOut.writeInt(3);
        mOut.write(new byte[]{0x2E, 0x2E, 0x5C, 0x4B, 0x00, 0x00, 0x00});
        mOut.writeInt(messagesCount);
    }

    private void writeMessage(int messageID, String name, String message, int trigger) throws IOException {
        mOut.writeInt(messageID);
        mOut.writeInt(name.length());
        mOut.write(name.getBytes());
        mOut.writeInt(messageID + mIdShift); // message ID in its turn equals tag ID
        mOut.writeInt(0);
        writeStrangeCounter();
        mOut.writeShort(0);
        mOut.writeInt(1); // latest state ID (equals to states count)
        mOut.write(0); // trigger type - value
        mOut.writeInt(0);
        mOut.write(0); // reset is disabled
        mOut.writeInt(1); // states count
        writeState(1, message, trigger);
    }

    private void writeState(int stateID, String comment, int trigger) throws IOException {
        mOut.writeInt(stateID);
        byte[] commentBytes = comment.getBytes(StandardCharsets.UTF_8);
        mOut.writeInt(commentBytes.length);
        mOut.write(commentBytes);
        mOut.write(0); // alarm type - alarm
        mOut.writeInt(trigger); // value or bit number to trigger on
        mOut.writeInt(0xFFFFFFFF);
        mOut.write(1); // sound on
        mOut.write(0); // invert off
    }

    private void writeStrangeCounter() throws IOException {
        mOut.writeShort(0x5384);
    }

    private void writeEnd() throws IOException {
        mOut.writeInt(0);
    }

    private int chooseTrigger(SimpleScadaRecord record) {
        if (record.isDigital()) {
            return TRIGGER_DIGIAL;
        } else {
            return TRIGGER_ANALOG;
        }
    }

    public void close() {
        try {
            mBufferedOutputStream.flush();
            mBufferedOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
