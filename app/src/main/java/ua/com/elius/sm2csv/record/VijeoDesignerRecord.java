package ua.com.elius.sm2csv.record;

import ua.com.elius.sm2csv.alarm.AlarmInfo;
import ua.com.elius.sm2csv.model.alarmconfig.Severity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VijeoDesignerRecord {

    public static final String DATA_TYPE_BOOL = "BOOL";
    public static final String DATA_TYPE_INT = "INT";
    public static final String DATA_TYPE_UINT = "UINT";
    public static final String DATA_TYPE_DINT = "DINT";
    public static final String DATA_TYPE_UDINT = "UDINT";
    public static final String DATA_TYPE_REAL = "REAL";
    public static final String DATA_TYPE_STRING = "STRING";

    private String mType = "Variable";
    private String mName;
    private String mDataType;
    private String mDataSource = "External";
    private String mDimension;
    private String mDescription;
    private String mInitialValue;
    private String mNumofBytes;
    private String mDataSharing;
    private String mAlarm;
    private String mLanguage1ID = "1";
    private String mAlarmMessage;
    private String mAlarmType;
    private String mTriggerCondition;
    private String mDeadband;
    private String mTarget;
    private String mAlarmLimits;
    private String mMinor;
    private String mMajor;
    private String mAlarmGroup;
    private String mSeverity;
    private String mVibrationPattern;
    private String mVibrationTime;
    private String mSoundFile;
    private String mPlayMode;
    private String mScanGroup = "ModbusEquipment01";
    private String mDeviceAddress;
    private String mBitNumber;
    private String mDataFormat;
    private String mSigned;
    private String mDataLength;
    private String mOffsetBitNo;
    private String mBitWidth;
    private String mInputRange;
    private String mMin;
    private String mMax;
    private String mDataScaling;
    private String mRawMin;
    private String mRawMax;
    private String mScaledMin;
    private String mScaledMax;
    private String mIndirectEnabled;
    private String mIndirectAddress;
    private String mRetentive;
    private String mLoggingGroup;
    private String mLogUserOperationsOnVariable;

    private static HashMap<String, String> sFromSoMachineType;

    static {
        sFromSoMachineType = new HashMap<>();
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_BIT, DATA_TYPE_BOOL);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_BOOL, DATA_TYPE_BOOL);
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_BYTE, );
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_WORD, DATA_TYPE_UINT);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DWORD, DATA_TYPE_DINT);
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LWORD, );
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_SINT, );
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_USINT, );
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_INT, DATA_TYPE_INT);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_UINT, DATA_TYPE_UINT);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DINT, DATA_TYPE_DINT);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_UDINT, DATA_TYPE_UDINT);
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LINT, );
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_ULINT, );
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_REAL, DATA_TYPE_REAL);
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LREAL, );
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_STRING, DATA_TYPE_STRING);
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_WSTRING, );
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_TIME, DATA_TYPE_INT);
//        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_LTIME, );
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DATE, DATA_TYPE_INT);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DATE_AND_TIME, DATA_TYPE_INT);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_TIME_OF_DAY, DATA_TYPE_INT);
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDataType() {
        return mDataType;
    }

    public void setDataType(String dataType) {
        mDataType = dataType;
    }

    public String getDataSource() {
        return mDataSource;
    }

    public void setDataSource(String dataSource) {
        mDataSource = dataSource;
    }

    public String getDimension() {
        return mDimension;
    }

    public void setDimension(String dimension) {
        mDimension = dimension;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getInitialValue() {
        return mInitialValue;
    }

    public void setInitialValue(String initialValue) {
        mInitialValue = initialValue;
    }

    public String getNumofBytes() {
        return mNumofBytes;
    }

    public void setNumofBytes(String numofBytes) {
        mNumofBytes = numofBytes;
    }

    public String getDataSharing() {
        return mDataSharing;
    }

    public void setDataSharing(String dataSharing) {
        mDataSharing = dataSharing;
    }

    public String getAlarm() {
        return mAlarm;
    }

    public void setAlarm(String alarm) {
        mAlarm = alarm;
    }

    public String getLanguage1ID() {
        return mLanguage1ID;
    }

    public void setLanguage1ID(String language1ID) {
        mLanguage1ID = language1ID;
    }

    public String getAlarmMessage() {
        return mAlarmMessage;
    }

    public void setAlarmMessage(String alarmMessage) {
        mAlarmMessage = alarmMessage;
    }

    public String getAlarmType() {
        return mAlarmType;
    }

    public void setAlarmType(String alarmType) {
        mAlarmType = alarmType;
    }

    public String getTriggerCondition() {
        return mTriggerCondition;
    }

    public void setTriggerCondition(String triggerCondition) {
        mTriggerCondition = triggerCondition;
    }

    public String getDeadband() {
        return mDeadband;
    }

    public void setDeadband(String deadband) {
        mDeadband = deadband;
    }

    public String getTarget() {
        return mTarget;
    }

    public void setTarget(String target) {
        mTarget = target;
    }

    public String getAlarmLimits() {
        return mAlarmLimits;
    }

    public void setAlarmLimits(String alarmLimits) {
        mAlarmLimits = alarmLimits;
    }

    public String getMinor() {
        return mMinor;
    }

    public void setMinor(String minor) {
        mMinor = minor;
    }

    public String getMajor() {
        return mMajor;
    }

    public void setMajor(String major) {
        mMajor = major;
    }

    public String getAlarmGroup() {
        return mAlarmGroup;
    }

    public void setAlarmGroup(String alarmGroup) {
        mAlarmGroup = alarmGroup;
    }

    public String getSeverity() {
        return mSeverity;
    }

    public void setSeverity(String severity) {
        mSeverity = severity;
    }

    public String getVibrationPattern() {
        return mVibrationPattern;
    }

    public void setVibrationPattern(String vibrationPattern) {
        mVibrationPattern = vibrationPattern;
    }

    public String getVibrationTime() {
        return mVibrationTime;
    }

    public void setVibrationTime(String vibrationTime) {
        mVibrationTime = vibrationTime;
    }

    public String getSoundFile() {
        return mSoundFile;
    }

    public void setSoundFile(String soundFile) {
        mSoundFile = soundFile;
    }

    public String getPlayMode() {
        return mPlayMode;
    }

    public void setPlayMode(String playMode) {
        mPlayMode = playMode;
    }

    public String getScanGroup() {
        return mScanGroup;
    }

    public void setScanGroup(String scanGroup) {
        mScanGroup = scanGroup;
    }

    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        mDeviceAddress = deviceAddress;
    }

    public String getBitNumber() {
        return mBitNumber;
    }

    public void setBitNumber(String bitNumber) {
        mBitNumber = bitNumber;
    }

    public String getDataFormat() {
        return mDataFormat;
    }

    public void setDataFormat(String dataFormat) {
        mDataFormat = dataFormat;
    }

    public String getSigned() {
        return mSigned;
    }

    public void setSigned(String signed) {
        mSigned = signed;
    }

    public String getDataLength() {
        return mDataLength;
    }

    public void setDataLength(String dataLength) {
        mDataLength = dataLength;
    }

    public String getOffsetBitNo() {
        return mOffsetBitNo;
    }

    public void setOffsetBitNo(String offsetBitNo) {
        mOffsetBitNo = offsetBitNo;
    }

    public String getBitWidth() {
        return mBitWidth;
    }

    public void setBitWidth(String bitWidth) {
        mBitWidth = bitWidth;
    }

    public String getInputRange() {
        return mInputRange;
    }

    public void setInputRange(String inputRange) {
        mInputRange = inputRange;
    }

    public String getMin() {
        return mMin;
    }

    public void setMin(String min) {
        mMin = min;
    }

    public String getMax() {
        return mMax;
    }

    public void setMax(String max) {
        mMax = max;
    }

    public String getDataScaling() {
        return mDataScaling;
    }

    public void setDataScaling(String dataScaling) {
        mDataScaling = dataScaling;
    }

    public String getRawMin() {
        return mRawMin;
    }

    public void setRawMin(String rawMin) {
        mRawMin = rawMin;
    }

    public String getRawMax() {
        return mRawMax;
    }

    public void setRawMax(String rawMax) {
        mRawMax = rawMax;
    }

    public String getScaledMin() {
        return mScaledMin;
    }

    public void setScaledMin(String scaledMin) {
        mScaledMin = scaledMin;
    }

    public String getScaledMax() {
        return mScaledMax;
    }

    public void setScaledMax(String scaledMax) {
        mScaledMax = scaledMax;
    }

    public String getIndirectEnabled() {
        return mIndirectEnabled;
    }

    public void setIndirectEnabled(String indirectEnabled) {
        mIndirectEnabled = indirectEnabled;
    }

    public String getIndirectAddress() {
        return mIndirectAddress;
    }

    public void setIndirectAddress(String indirectAddress) {
        mIndirectAddress = indirectAddress;
    }

    public String getRetentive() {
        return mRetentive;
    }

    public void setRetentive(String retentive) {
        mRetentive = retentive;
    }

    public String getLoggingGroup() {
        return mLoggingGroup;
    }

    public void setLoggingGroup(String loggingGroup) {
        mLoggingGroup = loggingGroup;
    }

    public String getLogUserOperationsOnVariable() {
        return mLogUserOperationsOnVariable;
    }

    public void setLogUserOperationsOnVariable(String logUserOperationsOnVariable) {
        mLogUserOperationsOnVariable = logUserOperationsOnVariable;
    }

    public VijeoDesignerRecord() {}

    public static VijeoDesignerRecord of(SoMachineRecord smRec, AlarmInfo alarmInfo) throws TypeMapException, LongNameException {
        VijeoDesignerRecord rec = new VijeoDesignerRecord();

        if (smRec.getName().length() > 32) {
            throw new LongNameException(smRec.getName());
        }

        rec.setName(smRec.getName());

        if (sFromSoMachineType.containsKey(smRec.getType())) {
            rec.setDataType(sFromSoMachineType.get(smRec.getType()));
        } else {
            throw new TypeMapException(smRec.getType());
        }

        rec.setDescription(smRec.getComment());

        // numeric alarms not supported
        if (alarmInfo.isAlarm() && smRec.getAddress().isDigital()) {
            rec.setAlarm("Enable");
            rec.setAlarmMessage(smRec.getComment());
            rec.setAlarmGroup("AlarmGroup1");
            rec.setSeverity(chooseSeverity(alarmInfo.getSeverities().get(0)));
        }

        rec.setDeviceAddress("%MW" + smRec.getAddress().getNumber());

        if (smRec.getAddress().isDigital()) {
            rec.setBitNumber("X" + Integer.toString(smRec.getAddress().getDigit()));
        }

        return rec;
    }

    public List<String> toList() {
        List<String> list = new ArrayList<>();

        list.add(mType);
        list.add(mName);
        list.add(mDataType);
        list.add(mDataSource);
        list.add(mDimension);
        list.add(mDescription);
        list.add(mInitialValue);
        list.add(mNumofBytes);
        list.add(mDataSharing);
        list.add(mAlarm);
        list.add(mLanguage1ID);
        list.add(mAlarmMessage);
        list.add(mAlarmType);
        list.add(mTriggerCondition);
        list.add(mDeadband);
        list.add(mTarget);
        list.add(mAlarmLimits);
        list.add(mMinor);
        list.add(mMajor);
        list.add(mAlarmGroup);
        list.add(mSeverity);
        list.add(mVibrationPattern);
        list.add(mVibrationTime);
        list.add(mSoundFile);
        list.add(mPlayMode);
        list.add(mScanGroup);
        list.add(mDeviceAddress);
        list.add(mBitNumber);
        list.add(mDataFormat);
        list.add(mSigned);
        list.add(mDataLength);
        list.add(mOffsetBitNo);
        list.add(mBitWidth);
        list.add(mInputRange);
        list.add(mMin);
        list.add(mMax);
        list.add(mDataScaling);
        list.add(mRawMin);
        list.add(mRawMax);
        list.add(mScaledMin);
        list.add(mScaledMax);
        list.add(mIndirectEnabled);
        list.add(mIndirectAddress);
        list.add(mRetentive);
        list.add(mLoggingGroup);
        list.add(mLogUserOperationsOnVariable);

        return list;
    }

    /**
     * Merges with other VijeoDesigner record
     * <p>
     * Takes most fields from other record but keeps
     * mName, mDataType, mDescription, mAlarm, mAlarmMessage,
     * mDeviceAddress, mBitNumber, mDataFormat, mSigned, mDataLength,
     * mSeverity
     * from this one
     *
     * @param eRec other record to merge with
     */
    public void merge(VijeoDesignerRecord eRec) {
        mType = eRec.getType();
        mDataSource = eRec.getDataSource();
        mDimension = eRec.getDimension();
        mInitialValue = eRec.getInitialValue();
        mNumofBytes = eRec.getNumofBytes();
        mDataSharing = eRec.getDataSharing();
        mLanguage1ID = eRec.getLanguage1ID();
        mAlarmType = eRec.getAlarmType();
        mTriggerCondition = eRec.getTriggerCondition();
        mDeadband = eRec.getDeadband();
        mTarget = eRec.getTarget();
        mAlarmLimits = eRec.getAlarmLimits();
        mMinor = eRec.getMinor();
        mMajor = eRec.getMajor();
        mAlarmGroup = eRec.getAlarmGroup();
        mVibrationPattern = eRec.getVibrationPattern();
        mVibrationTime = eRec.getVibrationTime();
        mSoundFile = eRec.getSoundFile();
        mPlayMode = eRec.getPlayMode();
        mScanGroup = eRec.getScanGroup();
        mOffsetBitNo = eRec.getOffsetBitNo();
        mBitWidth = eRec.getBitWidth();
        mInputRange = eRec.getInputRange();
        mMin = eRec.getMin();
        mMax = eRec.getMax();
        mDataScaling = eRec.getDataScaling();
        mRawMin = eRec.getRawMin();
        mRawMax = eRec.getRawMax();
        mScaledMin = eRec.getScaledMin();
        mScaledMax = eRec.getScaledMax();
        mIndirectEnabled = eRec.getIndirectEnabled();
        mIndirectAddress = eRec.getIndirectAddress();
        mRetentive = eRec.getRetentive();
        mLoggingGroup = eRec.getLoggingGroup();
        mLogUserOperationsOnVariable = eRec.getLogUserOperationsOnVariable();
    }

    private static String chooseSeverity(Severity severity) {
        switch (severity) {
            case high:
                return "3";
            case mid:
                return "2";
            case low:
                return "1";
            default:
                return "3";
        }
    }
}
