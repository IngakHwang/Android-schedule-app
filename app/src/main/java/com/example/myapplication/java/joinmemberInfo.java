package com.example.myapplication.java;

public class joinmemberInfo {
    String ID,PW,Email;

    public joinmemberInfo(String ID, String PW, String Email) {
        this.ID = ID;
        this.PW = PW;
        this.Email = Email;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPW() {
        return PW;
    }

    public void setPW(String PW) {
        this.PW = PW;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

}
