package ua.com.elius.sm2csv.model.alarmconfig;

public class Message {

    public int trigger = 1;
    public Severity severity = Severity.high;
    public String message = "";

    public Message() {}

    public Message(int trigger, Severity severity, String message) {
        this.trigger = trigger;
        this.severity = severity;
        this.message = message;
    }
}
