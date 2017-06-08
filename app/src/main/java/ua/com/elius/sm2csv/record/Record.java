package ua.com.elius.sm2csv.record;

public class Record {

    /** Tag name */
    private String mName;

    /**
     * Returns tag name
     *
     * @return {@link #mName}
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets tag name
     *
     * @param name new value for {@link #mName}
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
    public boolean isAlarm() {
        return mName.startsWith("f_") ||
               mName.startsWith("break_") ||
               mName.startsWith("sta_");
    }

}
