package com.test.daily_condition_record;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.test.daily_condition_record.Room.AppDatabase;
import com.test.daily_condition_record.Room.User;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // 년도 가져오는 클래스
    Today today = new Today();

    private RecyclerAdapter adapter;
    private List<User> users; // 로컬DB에 저장되어 있는 값을 불러오기 위해 : https://mynamewoon.tistory.com/18

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText(today.getYear());
        setSupportActionBar(toolbar);

        RecyclerView recyclerView1 = findViewById(R.id.recyclerView1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView1.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();

        // 로컬DB에 저장되어있는 내용을 리사이클러뷰에 띄움.
        // int size = AppDatabase.getInstance(this).userDao().getAll().size();
        users = AppDatabase.getInstance(this).userDao().getAll(); // // getInstance 함수에 userDao 인터페이스 안에 존재하는 모든 데이터를 불러오는 getAll( ) 함수를 사용해
        int size = users.size(); // size를 구함.

        for (int i = 0; i <size; i++) { // size 크기만큼 adapter에 additems 함수를 통해 내용 하나하나 추가.
            adapter.addItem(users.get(i));
            System.out.println("####" + AppDatabase.getInstance(this).userDao().getAll().get(i));
        }

        recyclerView1.setAdapter(adapter);

    }

    //////// 상단 툴바 /////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbar_plus_button: // 피드 추가 기능 버튼
                Intent intent = new Intent(this, PostActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    //////// 상단 툴바 /////////

}

