package com.postpc.mygiftcrads;

public class Notification {
    private String date;
    private String serial;

    Notification(String inputDate, String inputSerial)
    {
        date = inputDate;
        serial = inputSerial;
    }

    public String getSerial() {
        return serial;
    }

    public String getDate() {
        return date;
    }
}
