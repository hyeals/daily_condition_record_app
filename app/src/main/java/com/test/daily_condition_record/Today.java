package com.test.daily_condition_record;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Today {

    // 현재 시간 구하기
    long now = System.currentTimeMillis();

    // 현재 시간을 날짜에 저장
    Date date = new Date(now);

    // 년도만 나타내는 포맷 지정
    SimpleDateFormat yFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
    // 날짜, 시간을 나타내려고 하는 포맷 지정
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);

    // 년도 값
    String year = yFormat.format(date);
    // 날짜 값
    String time = mFormat.format(date);


    // 현재 요일 구하기
    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat weekDayFormat = new SimpleDateFormat("EE요일", Locale.KOREA);
    String weekDay = weekDayFormat.format(currentTime);

    public String getYear(){
        return year;
    }

    public String getDate(){
        return time;
    }

    public String getWeekDay(){
        return weekDay;
    }

}
