package ua.com.elius.sm2csv.writer;

import com.google.common.io.LittleEndianDataOutputStream;
import ua.com.elius.sm2csv.record.SimpleScadaRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public class SimpleScadaAlarmWriter {

    private static final String OUTPUT_FILE_NAME = "Messages.smg";

    List<String> mAlarmPrefixes;
    FileOutputStream mFileOutputStream;
    BufferedOutputStream mBufferedOutputStream;
    DataOutput mOut;

    public SimpleScadaAlarmWriter(Path outputPath, List<String> alarmPrefixes) throws FileNotFoundException {
        mAlarmPrefixes = alarmPrefixes;
        mFileOutputStream = new FileOutputStream(
                outputPath.resolve(OUTPUT_FILE_NAME).toFile());
        mBufferedOutputStream = new BufferedOutputStream(mFileOutputStream);
        mOut = new LittleEndianDataOutputStream(mBufferedOutputStream);
    }

    public void write(List<SimpleScadaRecord> records) throws IOException {
        int messagesCount = (int) records.stream()
                .filter(r -> r.isAlarm(mAlarmPrefixes))
                .count();
        writeHeader(messagesCount);
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).isAlarm(mAlarmPrefixes)) {
                writeMessage(i, "msg_" + records.get(i).getName(), records.get(i).getComment());
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

    private void writeMessage(int messageID, String name, String message) throws IOException {
        mOut.writeInt(messageID);
        mOut.writeInt(name.length());
        mOut.write(name.getBytes());
        mOut.writeInt(messageID); // tag ID for now equals message ID
        mOut.writeInt(0);
        writeStrangeCounter();
        mOut.writeShort(0);
        mOut.write(1);
        mOut.writeInt(0);
        mOut.writeInt(0);
        mOut.writeInt(1); // states count
        writeState(1, message);
    }

    private void writeState(int stateID, String comment) throws IOException {
        mOut.writeInt(stateID);
        byte[] commentBytes = comment.getBytes(StandardCharsets.UTF_8);
        mOut.writeInt(commentBytes.length);
        mOut.write(commentBytes);
        mOut.write(0);
        mOut.writeInt(2); // value to trigger on
        mOut.writeInt(0xFFFFFFFF);
        mOut.write(1); // sound on
        mOut.write(0);
    }

    private void writeStrangeCounter() throws IOException {
        mOut.writeShort(0x5037);
    }

    private void writeEnd() throws IOException {
        mOut.writeInt(0);
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
