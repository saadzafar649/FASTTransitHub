package com.example.fasttransithub.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class Student {
    public String uid,name,rollNo,busStop,route,phone,email,imageUrl;
    public boolean verified = false;
    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getBusStop() {
        return busStop;
    }

    public void setBusStop(String busStop) {
        this.busStop = busStop;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Student() {
    }

    public Student( String name, String rollNo, String busStop, String route, String phone, String email, String imageUrl,String uid) {
        this.uid = uid;
        this.name = name;
        this.rollNo = rollNo;
        this.busStop = busStop;
        this.route = route;
        this.phone = phone;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public Student( String name, String rollNo, String busStop, String route, String phone, String email, String imageUrl) {
        this.name = name;
        this.rollNo = rollNo;
        this.busStop = busStop;
        this.route = route;
        this.phone = phone;
        this.email = email;
        this.imageUrl = imageUrl;
    }
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", uid);
            jsonObject.put("name", name);
            jsonObject.put("rollNo", rollNo);
            jsonObject.put("busStop", busStop);
            jsonObject.put("route", route);
            jsonObject.put("phone", phone);
            jsonObject.put("email", email);
            jsonObject.put("imageUrl", imageUrl);
            jsonObject.put("verified", verified);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
