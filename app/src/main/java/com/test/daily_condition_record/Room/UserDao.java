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

    @Delete
    void delete(User user);

    @Query("SELECT * FROM memoTable")
    List<User> getAll();

//    @Query("SELECT * FROM memoTable WHERE id = :id") // https://ondolroom.tistory.com/679
//    List<User> getWeather1(int id); // https://velog.io/@ptm0304/Android-Room-database-%EC%82%AC%EC%9A%A9%ED%95%B4%EB%B3%B4%EA%B8%B0

    @Query("SELECT Weather FROM memoTable")
    String askWeather();

    @Query("DELETE FROM memoTable")
    void deleteAll();

}
