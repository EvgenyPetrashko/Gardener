package com.example.gardener;

public class EventItem {
    public int id;
    public String title;
    public String description;
    public boolean notifications;
    public String time;

    public EventItem(int id, String title, String description, boolean notifications, String time) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.notifications = notifications;
        this.time = time;
    }

    public EventItem() {
    }
}
