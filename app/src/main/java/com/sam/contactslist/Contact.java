package com.sam.contactslist;

import android.graphics.Bitmap;

import org.json.JSONObject;

public class Contact {
    String name;
    String mobile;
    String email;
    Bitmap profile;
    boolean isChecked;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Bitmap getProfile() {
        return profile;
    }

    public void setProfile(Bitmap profile) {
        this.profile = profile;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", name);
            obj.put("mobile", mobile);
            obj.put("email", email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}
