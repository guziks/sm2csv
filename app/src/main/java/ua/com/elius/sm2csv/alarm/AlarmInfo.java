package ua.com.elius.sm2csv.alarm;

import ua.com.elius.sm2csv.model.alarmconfig.AlarmConfig;
import ua.com.elius.sm2csv.model.alarmconfig.Message;
import ua.com.elius.sm2csv.model.alarmconfig.Severity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlarmInfo {

    private List<Severity> mSeverities = new ArrayList<>();
    private List<Integer> mTriggers = new ArrayList<>();
    private List<String> mMessages = new ArrayList<>();
    private String mPatternMatched;
    private boolean mIsAlarm = false;

    public AlarmInfo(AlarmConfig config, String name, boolean isDigital) {
        if (isDigital) {
            for (String pattern : config.digital.keySet()) {
                Matcher matcher = Pattern.compile(pattern).matcher(name);
                if (matcher.find()) {
                    mIsAlarm = true;
                    mPatternMatched = pattern;
                    mSeverities.add(config.digital.get(pattern).severity);
                    return;
                }
            }
        } else {
            for (String pattern : config.numeric.keySet()) {
                Matcher matcher = Pattern.compile(pattern).matcher(name);
                if (matcher.find()) {
                    mIsAlarm = true;
                    mPatternMatched = pattern;
                    for (Message message : config.numeric.get(pattern).messages) {
                        mSeverities.add(message.severity);
                        mTriggers.add(message.trigger);
                        mMessages.add(message.message);
                    }
                    return;
                }
            }
        }
    }

    public List<Severity> getSeverities() {
        return mSeverities;
    }

    public List<Integer> getTriggers() {
        return mTriggers;
    }

    public List<String> getMessages() {
        return mMessages;
    }

    public String getPatternMatched() {
        return mPatternMatched;
    }

    public boolean isAlarm() {
        return mIsAlarm;
    }
}
