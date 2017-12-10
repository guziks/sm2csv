package ua.com.elius.sm2csv.writer;

import java.util.Arrays;
import java.util.List;

public class AlarmConfig {

    public static final String SEVERITY_HIGH = "high";
    public static final String SEVERITY_MID = "mid";
    public static final String SEVERITY_LOW = "low";

    private List<Integer> mBits;
    private List<String> mPrefixes;
    private List<String> mSeverities;
    private String mDigitalSeverity;

    public AlarmConfig() {
        mBits = Arrays.asList(0, 1, 2, 3);
        mPrefixes = Arrays.asList("AH: ", "WH: ", "WL: ", "AL: ");
        mSeverities = Arrays.asList(SEVERITY_HIGH, SEVERITY_MID, SEVERITY_MID, SEVERITY_HIGH);
        mDigitalSeverity = SEVERITY_HIGH;
    }

    public AlarmConfig(List<Integer> bits, List<String> prefixes, List<String> severities, String digitalSeverity) {
        mBits = bits;
        mPrefixes = prefixes;
        mSeverities = severities;
        mDigitalSeverity = digitalSeverity;
    }

    public int getBit(int i) {
        return mBits.get(i);
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
        return Math.min(Math.min(mBits.size(), mPrefixes.size()), mSeverities.size());
    }
}
