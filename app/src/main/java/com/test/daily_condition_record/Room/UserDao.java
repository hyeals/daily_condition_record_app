package com.test.daily_condition_record.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    // 참고 : https://mynamewoon.tistory.com/20
    @Query("UPDATE memoTable SET user_weather = :weather, user_date = :date, user_des = :des, user_img = :img WHERE user_id = :id")
    void update(String weather, String date, String des, String img, int id); // id는 사실 position임. ( position + 1을 해줘야 인덱스가 맞음.)

    @Delete
    void delete(User user);

    @Query("SELECT * FROM memoTable")
    List<User> getAll();

//    @Query("SELECT * FROM memoTable WHERE id = :id") // https://ondolroom.tistory.com/679
//    List<User> getWeather1(int id); // https://velog.io/@ptm0304/Android-Room-database-%EC%82%AC%EC%9A%A9%ED%95%B4%EB%B3%B4%EA%B8%B0

    @Query("DELETE FROM memoTable")
    void deleteAll();

}
