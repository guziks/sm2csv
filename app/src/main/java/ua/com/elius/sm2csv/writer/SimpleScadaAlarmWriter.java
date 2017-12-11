package ua.com.elius.sm2csv.writer;

import com.google.common.io.LittleEndianDataOutputStream;
import ua.com.elius.sm2csv.record.SimpleScadaRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SimpleScadaAlarmWriter {

    /**
     * SimpleScada messages file name
     */
    private static final String OUTPUT_FILE_NAME = "Messages.smg";

    /**
     * Trigger type: use value
     */
    private static final int TRIGGER_TYPE_VALUE = 0;

    /**
     * Trigger type: use bit inside word
     */
    private static final int TRIGGER_TYPE_BIT = 1;

    private List<String> mAlarmPrefixes;
    private BufferedOutputStream mBufferedOutputStream;
    private DataOutput mOut;
    private int mIdShift;
    private AlarmConfig mAlarmConfig;

    public SimpleScadaAlarmWriter(Path outputPath, List<String> alarmPrefixes, int idShift, AlarmConfig alarmConfig) throws FileNotFoundException {
        mAlarmPrefixes = alarmPrefixes;
        mBufferedOutputStream = new BufferedOutputStream(
                new FileOutputStream(outputPath.resolve(OUTPUT_FILE_NAME).toFile()));
        mOut = new LittleEndianDataOutputStream(mBufferedOutputStream);
        mIdShift = idShift;
        mAlarmConfig = alarmConfig;
    }

    public void write(List<SimpleScadaRecord> records) throws IOException {
        int messagesCount = (int) records.stream()
                .filter(r -> r.isAlarm(mAlarmPrefixes))
                .count();
        writeHeader(messagesCount);
        // iterating over not filtered list because we need original indexes
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).isAlarm(mAlarmPrefixes)) {
                writeMessage(i, // message ID equals tag ID
                        "msg_" + records.get(i).getName(),
                        new SimpleScadaMessage(records.get(i))
                );
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

    private void writeMessage(int messageID, String name, SimpleScadaMessage message) throws IOException {
        mOut.writeInt(messageID);
        mOut.writeInt(name.length());
        mOut.write(name.getBytes());
        mOut.writeInt(messageID + mIdShift);
        mOut.writeInt(0);
        writeStrangeCounter();
        mOut.writeShort(0);
        mOut.writeInt(message.getStatesCount()); // latest state ID (equals to states count)
        mOut.write(message.getTriggerType()); // trigger type - value
        mOut.writeInt(0);
        mOut.write(0); // reset is disabled
        mOut.writeInt(message.getStatesCount()); // states count
        for (SimpleScadaMessageState state : message.getStates()) {
            writeState(state.getID(), state.getComment(), state.getTriggerValue(), state.getAlarmType());
        }
    }

    private void writeState(int stateID, String comment, int triggerValue, int alarmType) throws IOException {
        mOut.writeInt(stateID);
        byte[] commentBytes = comment.getBytes(StandardCharsets.UTF_8);
        mOut.writeInt(commentBytes.length);
        mOut.write(commentBytes);
        mOut.write(alarmType);
        mOut.writeInt(triggerValue); // value or bit number to trigger on
        mOut.writeInt(0xFFFFFFFF);
        mOut.write(1); // sound on
        mOut.write(0); // invert off
        mOut.write(1); // unaknowledged list on
        mOut.write(1); // active list on
    }

    private void writeStrangeCounter() throws IOException {
        mOut.writeShort(0x5384);
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

    private class SimpleScadaMessage {
        private static final int ALARM_TYPE_ALARM = 0;
        private static final int ALARM_TYPE_WARNING = 1;
        private static final int ALARM_TYPE_MESSAGE = 2;

        private int mTriggerType;
        private List<SimpleScadaMessageState> mStates;

        public SimpleScadaMessage(SimpleScadaRecord record) {
            String baseComment = record.getComment();
            mStates = new ArrayList<>();

            if (record.isDigital()) {
                mTriggerType = TRIGGER_TYPE_VALUE;
                mStates.add(new SimpleScadaMessageState(1, baseComment,
                        alarmTypeFrom(mAlarmConfig.getDigitalSeverity()), 1));
            } else { // if record is analog
                if (mAlarmConfig.size() == 1) {
                    mTriggerType = TRIGGER_TYPE_VALUE;
                    mStates.add(new SimpleScadaMessageState(1, baseComment,
                            alarmTypeFrom(mAlarmConfig.getSeverity(0)), 1));
                } else { // > 1; it can not be 0 according to rules
                    mTriggerType = TRIGGER_TYPE_BIT;
                    for (int i = 0; i < mAlarmConfig.size(); i++) {
                        mStates.add(new SimpleScadaMessageState(i + 1, // ID starts with 1
                                mAlarmConfig.getPrefix(i) + baseComment,
                                alarmTypeFrom(mAlarmConfig.getSeverity(i)),
                                mAlarmConfig.getTrigger(i))
                        );
                    }
                }
            }
        }

        public int getStatesCount() {
            return mStates.size();
        }

        public int getTriggerType() {
            return mTriggerType;
        }

        public List<SimpleScadaMessageState> getStates() {
            return mStates;
        }

        private int alarmTypeFrom(String severity) {
            switch (severity) {
                case AlarmConfig.SEVERITY_HIGH:
                    return ALARM_TYPE_ALARM;
                case AlarmConfig.SEVERITY_MID:
                    return ALARM_TYPE_WARNING;
                case AlarmConfig.SEVERITY_LOW:
                    return ALARM_TYPE_MESSAGE;
                default:
                    return ALARM_TYPE_ALARM;
            }
        }
    }

    private class SimpleScadaMessageState {
        private int mID;
        private String mComment;
        private int mAlarmType;
        private int mTriggerValue;

        public SimpleScadaMessageState(int ID, String comment, int alarmType, int triggerValue) {
            mID = ID;
            mComment = comment;
            mAlarmType = alarmType;
            mTriggerValue = triggerValue;
        }

        public int getID() {
            return mID;
        }

        public String getComment() {
            return mComment;
        }

        public int getAlarmType() {
            return mAlarmType;
        }

        public int getTriggerValue() {
            return mTriggerValue;
        }
    }
}
