package com.example.myapplication;

import android.net.Uri;

public class listdata {

    private Uri additemcard_image;
    private String additemcard_title, additemcard_memo, additemcard_url;

    public listdata(Uri additemcard_image, String additemcard_title, String additemcard_memo, String additemcard_url){
        this.additemcard_image = additemcard_image;
        this.additemcard_title = additemcard_title;
        this.additemcard_memo = additemcard_memo;
        this.additemcard_url = additemcard_url;
    }

    public Uri getAdditemcard_image() {
        return additemcard_image;
    }

    public void setAdditemcard_image(Uri additemcard_image) {
        this.additemcard_image = additemcard_image;
    }

    public String getAdditemcard_title() {
        return additemcard_title;
    }

    public void setAdditemcard_title(String additemcard_title) {
        this.additemcard_title = additemcard_title;
    }

    public String getAdditemcard_memo() {
        return additemcard_memo;
    }

    public void setAdditemcard_memo(String additemcard_memo) {
        this.additemcard_memo = additemcard_memo;
    }

    public String getAdditemcard_url() {
        return additemcard_url;
    }

    public void setAdditemcard_url(String additemcard_url) {
        this.additemcard_url = additemcard_url;
    }
}
