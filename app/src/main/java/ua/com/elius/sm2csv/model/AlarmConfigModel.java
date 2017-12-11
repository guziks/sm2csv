package ua.com.elius.sm2csv.model;

import java.util.List;

public class AlarmConfigModel {

    public AlarmConfigDigital digital;
    public AlarmConfigNumeric numeric;

    public static class AlarmConfigDigital {

        public String severity;

    }

    public static class AlarmConfigNumeric {

        public List<Integer> trigger;
        public List<String> prefix;
        public List<String> severity;

    }
}
