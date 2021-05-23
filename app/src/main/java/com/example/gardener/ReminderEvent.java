package com.example.gardener;

public class ReminderEvent {
    public String name;
    public String date;
    public String time;
    public String desc;

    public ReminderEvent(String name, String date, String time, String desc) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.desc = desc;
    }

    public ReminderEvent() {
    }
}
