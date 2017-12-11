package ua.com.elius.sm2csv.model;

import java.util.Arrays;
import java.util.List;

public class AlarmConfigNumeric {

    public String trigger;
    public List<Integer> bit;
    public List<String> prefix;
    public List<String> severity;
    public Integer value;

//    public AlarmConfigNumeric() {
//        this.trigger = "bit";
//        this.bit = Arrays.asList(0, 1, 2);
//        this.prefix = Arrays.asList("blah", "blah", "blah");
//        this.severity = Arrays.asList("high", "high", "high");
//        this.value = 2;
//    }
}
