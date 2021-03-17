package com.test.daily_condition_record.Room;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    private static AppDatabase instance = null;

//    // 싱글톤 패턴 : 어떤 클래스를 최초 단 한번만 생성하여 메모리에 할당하고 그 메모리를 참조해서 사용하는 디자인 패턴
//    private AppDatabase() {
//    // https://mynamewoon.tistory.com/15?category=833237
//    }

    public static synchronized AppDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "memo_Database")
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }


}
