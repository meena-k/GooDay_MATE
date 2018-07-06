package com.example.mate.gooday_mate;

public class Item_Main {
    private String name, birth, sex, enterdate, phone, channel, port;
    private int img;

    public Item_Main(String name, String birth, String sex, String enterdate, String phone, int img, String channel, String port) {
        setName(name);
        setBirth(birth);
        setSex(sex);
        setEnterdate(enterdate);
        setPhone(phone);
        setImg(img);
        setChannel(channel);
        setPort(port);
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

    public String getEnterdate() {
        return enterdate;
    }

    public void setEnterdate(String enterdate) {
        this.enterdate = enterdate;
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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
