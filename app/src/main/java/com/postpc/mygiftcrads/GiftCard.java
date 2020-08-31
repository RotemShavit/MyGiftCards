package com.postpc.mygiftcrads;

public class GiftCard
{
    private String cardNumber;
    private String expDate;
    private int value;
    private String brand;
    private String address;
    private String phone;
    private String category;
    private String idString;

    GiftCard(String new_cardNumber, String new_expDate, int new_value, String new_brand,
             String new_address, String new_phone, String new_category, String idString)
    {
        this.cardNumber = new_cardNumber;
        this.expDate= new_expDate; // check for valid
        this.value = new_value;
        this.brand = new_brand; // Needs to be from known options - not free text
        this.address = new_address; // check for valid?
        this.phone = new_phone; // check for valid
        this.category = new_category; // Needs to be from known options - not free text
        this.idString = idString; // For identify in firebase
    }

    String getCardNumber()
    {
        return this.cardNumber;
    }

    String getExpDate()
    {
        return this.expDate;
    }

    int getValue()
    {
        return this.value;
    }

    String getBrand()
    {
        return this.brand;
    }

    String getAddress()
    {
        return this.address;
    }

    String getPhone()
    {
        return this.phone;
    }

    String getCategory()
    {
        return this.category;
    }

    String getIdString(){return this.idString;}
}
