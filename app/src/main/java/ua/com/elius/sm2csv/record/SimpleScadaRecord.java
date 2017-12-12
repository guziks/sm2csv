package ua.com.elius.sm2csv.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimpleScadaRecord extends Record {

    public static final String DATA_TYPE_BOOLEAN = "Boolean";
    public static final String DATA_TYPE_SMALLINT = "SmallInt";
    public static final String DATA_TYPE_INTEGER = "Integer";
    public static final String DATA_TYPE_SINGLE = "Single";

    public static final String GROUP_DEFAULT = "Main";

    private String mTagGroup = GROUP_DEFAULT;
    private String mOpcNode = GROUP_DEFAULT;

    private String mDataType;

    private String mArchiveType = "не архивировать";
    private String mTrendType = "обычный";
    private String mDeadband = "0";
    private String mArchveInterval = "как в настройках";

    private String mScaleName;
    private String mScaleUnits;
    private String mScaleMin;
    private String mScaleMax;

    private String mComment;
    private String mTagType = "внешний";
    private String mAddress;
    private String mPcName = "localhost";
    private String mOpcServer = "Lectus.OPC.1";
    private String mSamplingFrequency = "как в настройках";
    private String mFormat = "0";
    private String mShift = "0";

    private String mAlarmHigh;
    private String mWarnHigh;
    private String mWarnLow;
    private String mAlarmLow;

    private String mVisualMin;
    private String mVisualMax;

    private static HashMap<String, String> sFromSoMachineType;

    static {
        sFromSoMachineType = new HashMap<>();
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_BOOL, DATA_TYPE_BOOLEAN);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_WORD, DATA_TYPE_SMALLINT);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_INT, DATA_TYPE_SMALLINT);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_DINT, DATA_TYPE_INTEGER);
        sFromSoMachineType.put(SoMachineRecord.DATA_TYPE_REAL, DATA_TYPE_SINGLE);
    }

    public String getTagGroup() {
        return mTagGroup;
    }

    public void setTagGroup(String tagGroup) {
        mTagGroup = tagGroup;
    }

    public String getOpcNode() {
        return mOpcNode;
    }

    public void setOpcNode(String opcNode) {
        mOpcNode = opcNode;
    }

    public String getDataType() {
        return mDataType;
    }

    public void setDataType(String dataType) {
        mDataType = dataType;
    }

    public String getArchiveType() {
        return mArchiveType;
    }

    public void setArchiveType(String archiveType) {
        mArchiveType = archiveType;
    }

    public String getTrendType() {
        return mTrendType;
    }

    public void setTrendType(String trendType) {
        mTrendType = trendType;
    }

    public String getDeadband() {
        return mDeadband;
    }

    public void setDeadband(String deadband) {
        mDeadband = deadband;
    }

    public String getArchveInterval() {
        return mArchveInterval;
    }

    public void setArchveInterval(String archveInterval) {
        mArchveInterval = archveInterval;
    }

    public String getScaleName() {
        return mScaleName;
    }

    public void setScaleName(String scaleName) {
        mScaleName = scaleName;
    }

    public String getScaleUnits() {
        return mScaleUnits;
    }

    public void setScaleUnits(String scaleUnits) {
        mScaleUnits = scaleUnits;
    }

    public String getScaleMin() {
        return mScaleMin;
    }

    public void setScaleMin(String scaleMin) {
        mScaleMin = scaleMin;
    }

    public String getScaleMax() {
        return mScaleMax;
    }

    public void setScaleMax(String scaleMax) {
        mScaleMax = scaleMax;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public String getTagType() {
        return mTagType;
    }

    public void setTagType(String tagType) {
        mTagType = tagType;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getPcName() {
        return mPcName;
    }

    public void setPcName(String pcName) {
        mPcName = pcName;
    }

    public String getOpcServer() {
        return mOpcServer;
    }

    public void setOpcServer(String opcServer) {
        mOpcServer = opcServer;
    }

    public String getSamplingFrequency() {
        return mSamplingFrequency;
    }

    public void setSamplingFrequency(String samplingFrequency) {
        mSamplingFrequency = samplingFrequency;
    }

    public String getFormat() {
        return mFormat;
    }

    public void setFormat(String format) {
        mFormat = format;
    }

    public String getShift() {
        return mShift;
    }

    public void setShift(String shift) {
        mShift = shift;
    }

    public String getAlarmHigh() {
        return mAlarmHigh;
    }

    public void setAlarmHigh(String alarmHigh) {
        mAlarmHigh = alarmHigh;
    }

    public String getWarnHigh() {
        return mWarnHigh;
    }

    public void setWarnHigh(String warnHigh) {
        mWarnHigh = warnHigh;
    }

    public String getWarnLow() {
        return mWarnLow;
    }

    public void setWarnLow(String warnLow) {
        mWarnLow = warnLow;
    }

    public String getAlarmLow() {
        return mAlarmLow;
    }

    public void setAlarmLow(String alarmLow) {
        mAlarmLow = alarmLow;
    }

    public String getVisualMin() {
        return mVisualMin;
    }

    public void setVisualMin(String visualMin) {
        mVisualMin = visualMin;
    }

    public String getVisualMax() {
        return mVisualMax;
    }

    public void setVisualMax(String visualMax) {
        mVisualMax = visualMax;
    }

    public SimpleScadaRecord() {
    }

    public static SimpleScadaRecord of(SoMachineRecord smRec) {

        SimpleScadaRecord rec = new SimpleScadaRecord();

        rec.setName(smRec.getName());

        if (sFromSoMachineType.containsKey(smRec.getType())) {
            rec.setDataType(sFromSoMachineType.get(smRec.getType()));
        }

        rec.updateAddress();
        rec.setComment(smRec.getComment());

        return rec;
    }

    public void updateAddress() {
        setAddress(getOpcNode() + "." + getName());
    }

    public List<String> toList() {
        List<String> list = new ArrayList<>();

        list.add(""); // tag group placeholder
        list.add(getName());
        list.add(mDataType);
        list.add(mArchiveType);
        list.add(mTrendType);
        list.add(mDeadband);
        list.add(mArchveInterval);
        list.add(mScaleName);
        list.add(mScaleUnits);
        list.add(mScaleMin);
        list.add(mScaleMax);
        list.add(mComment);
        list.add(mTagType);
        list.add(mAddress);
        list.add(mPcName);
        list.add(mOpcServer);
        list.add(mSamplingFrequency);
        list.add(mFormat);
        list.add(mShift);
        list.add(mAlarmHigh);
        list.add(mWarnHigh);
        list.add(mWarnLow);
        list.add(mAlarmLow);
        list.add(mVisualMin);
        list.add(mVisualMax);

        return list;
    }

    /**
     * Merges with other SimpleScada record
     * <p>
     * Takes most fields from other record
     * but keeps mName, mDataType, mComment from
     * this one
     *
     * @param eRec other record to merge with
     */
    public void merge(SimpleScadaRecord eRec) {
        // assigning all fields except:
        // mName, mDataType, mComment
        mTagGroup = eRec.getTagGroup();
        mOpcNode = eRec.getOpcNode();
        updateAddress();
        mArchiveType = eRec.getArchiveType();
        mTrendType = eRec.getTrendType();
        mDeadband = eRec.getDeadband();
        mArchveInterval = eRec.getArchveInterval();
        mScaleName = eRec.getScaleName();
        mScaleUnits = eRec.getScaleUnits();
        mScaleMin = eRec.getScaleMin();
        mScaleMax = eRec.getScaleMax();
        mTagType = eRec.getTagType();
        mPcName = eRec.getPcName();
        mOpcServer = eRec.getOpcServer();
        mSamplingFrequency = eRec.getSamplingFrequency();
        mFormat = eRec.getFormat();
        mShift = eRec.getShift();
        mAlarmHigh = eRec.getAlarmHigh();
        mWarnHigh = eRec.getWarnHigh();
        mWarnLow = eRec.getWarnLow();
        mAlarmLow = eRec.getAlarmLow();
        mVisualMin = eRec.getVisualMin();
        mVisualMax = eRec.getVisualMax();
    }

    public boolean isDigital() {
        return DATA_TYPE_BOOLEAN.equals(mDataType);
    }

    public boolean isAnalog() {
        return !isDigital();
    }
}
