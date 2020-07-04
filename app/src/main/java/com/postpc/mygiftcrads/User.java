package com.postpc.mygiftcrads;

public class User
{
    private String name;
    private String email;
    private String password;

    User(String new_name, String new_email, String new_password)
    {
        // check before creating this instance for valid inputs (in the activity)
        this.name = new_name;
        this.email = new_email;
        this.password = new_password;
    }

    String getname()
    {
        return this.name;
    }

    String getemail()
    {
        return this.email;
    }

    String getPassword()
    {
        return this.password;
    }
}
