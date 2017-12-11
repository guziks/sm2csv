package ua.com.elius.sm2csv.writer;

import ua.com.elius.sm2csv.model.AlarmConfigModel;

import java.util.Arrays;
import java.util.List;

public class AlarmConfig {

    public static final String SEVERITY_HIGH = "high";
    public static final String SEVERITY_MID = "mid";
    public static final String SEVERITY_LOW = "low";

    private List<Integer> mTriggers;
    private List<String> mPrefixes;
    private List<String> mSeverities;
    private String mDigitalSeverity;

    public AlarmConfig() {
        mTriggers = Arrays.asList(0, 1, 2, 3);
        mPrefixes = Arrays.asList("AH: ", "WH: ", "WL: ", "AL: ");
        mSeverities = Arrays.asList(SEVERITY_HIGH, SEVERITY_MID, SEVERITY_MID, SEVERITY_HIGH);
        mDigitalSeverity = SEVERITY_HIGH;
    }

    public AlarmConfig(List<Integer> triggers, List<String> prefixes, List<String> severities, String digitalSeverity) {
        mTriggers = triggers;
        mPrefixes = prefixes;
        mSeverities = severities;
        mDigitalSeverity = digitalSeverity;
    }

    public AlarmConfig(AlarmConfigModel model) {
        mTriggers = model.numeric.trigger;
        mPrefixes = model.numeric.prefix;
        mSeverities = model.numeric.severity;
        mDigitalSeverity = model.digital.severity;
    }

    public int getTrigger(int i) {
        return mTriggers.get(i);
    }

    public String getPrefix(int i) {
        return mPrefixes.get(i);
    }

    public String getSeverity(int i) {
        return mSeverities.get(i);
    }

    public String getDigitalSeverity() {
        return mDigitalSeverity;
    }

    public int size() {
        return mTriggers.size();
    }
}
