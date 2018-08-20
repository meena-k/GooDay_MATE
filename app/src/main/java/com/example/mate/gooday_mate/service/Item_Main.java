package com.example.mate.gooday_mate.service;

public class Item_Main {
    private String name, birth, sex, enterdate, phone, uid;
    private int img;

    public Item_Main(String name, String birth, String sex, String phone, int img, String enterdate, String uid) {
        setName(name);
        setBirth(birth);
        setSex(sex);
        setPhone(phone);
        setImg(img);
        setEnterdate(enterdate);
        setUid(uid);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getEnterdate() {
        return enterdate;
    }

    public void setEnterdate(String enterdate) {
        this.enterdate = enterdate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
