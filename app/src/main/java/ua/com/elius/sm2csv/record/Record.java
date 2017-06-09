package ua.com.elius.sm2csv.record;

import java.util.List;

public class Record {

    /**
     * Tag name
     */
    private String mName;

    /**
     * @return Tag name
     */
    public String getName() {
        return mName;
    }

    /**
     * @param name new tag name
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Checks if this is alarm tag
     * <p>
     * Alarm tags are used by alarm writers.
     *
     * @return {@code true} if tag is alarm
     * @see ua.com.elius.sm2csv.writer.EasyBuilderAlarmWriter#write(EasyBuilderRecord)
     * @see ua.com.elius.sm2csv.writer.WinccAlarmWriter#write(WinccRecord)
     */
    public boolean isAlarm(List<String> prefixes) {
        for (String prefix : prefixes) {
            if (mName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

}
