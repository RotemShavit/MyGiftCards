package com.postpc.mygiftcrads;

public class Notification {
    private String date;
    private String serial;
    private String brand;

    Notification(String inputDate, String inputSerial, String inputBrand)
    {
        date = inputDate;
        serial = inputSerial;
        brand = inputBrand;
    }

    public String getSerial() {
        return serial;
    }

    public String getDate() {
        return date;
    }

    public String getBrand() {
        return brand;
    }
}
