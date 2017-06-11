package ua.com.elius.sm2csv.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimpleScadaRecord extends Record {

    private String mTagGroup = "Main";
    private String mOpcNode = "Main";

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

    private static HashMap<String, String> sFromSoMachineType;

    static {
        sFromSoMachineType = new HashMap<>();
        sFromSoMachineType.put("BOOL", "Boolean");
        sFromSoMachineType.put("WORD", "SmallInt");
        sFromSoMachineType.put("INT", "SmallInt");
        sFromSoMachineType.put("DINT", "Integer");
        sFromSoMachineType.put("REAL", "Single");
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

    public SimpleScadaRecord() {
    }

    public static SimpleScadaRecord of(SoMachineRecord smRec) {

        SimpleScadaRecord rec = new SimpleScadaRecord();

        rec.setName(smRec.getName());

        if (sFromSoMachineType.containsKey(smRec.getType())) {
            rec.setDataType(sFromSoMachineType.get(smRec.getType()));
        }

        rec.setAddress(rec.getOpcNode() + "." + rec.getName());
        rec.setComment(smRec.getComment());

        return rec;
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

        return list;
    }
}
