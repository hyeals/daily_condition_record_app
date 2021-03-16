package com.test.daily_condition_record.Room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "memoTable")
public class User {

    //Room에서 자동으로 id를 할당
    @PrimaryKey(autoGenerate = true)
    private int id; // 할당 번호
//    private String title; // 메모 제목
    private String des; // 메모 내용
    private String date; // 메모 날짜
    private String weather; // 메모 날씨
    private String img; // 이미지

    public User(String des, String date, String weather, String img) {
        this.des = des;
        this.date = date;
        this.weather = weather;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDes() { // RecyclerAdapter.java의 onBind함수에서 쓰임.
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }

    public String getDate() { // RecyclerAdapter.java의 onBind함수에서 쓰임.
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeather() { // RecyclerAdapter.java의 onBind함수에서 쓰임.
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "User{" + "입력값(des)='" + des + '\'' + weather + img + '}';
    }
}
