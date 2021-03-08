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

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // 년도 가져오는 클래스
    Today today = new Today();

    private RecyclerAdapter adapter;

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
        recyclerView1.setAdapter(adapter);

        getData();
    }

    private void getData() {
        List<String> titleList = Arrays.asList("너구리","안성탕면","삼양라면","신라면","튀김우동","짜파게티");
        List<String> contentList = Arrays.asList("구수한 우동맛입니다.","션합니다.","제일 무난합니다.","마니 매워요~","피씨방에서는 쵝오","짜장라면입니다.");
//        List<Integer> rsIdList = Arrays.asList(
//                R.drawable.nuguri,R.drawable.an,
//                R.drawable.sam,R.drawable.sin,R.drawable.woo,R.drawable.jja
//        );

        for (int i=0;i<6;i++) {
            Data data = new Data();
            data.setTitle(titleList.get(i));
            data.setToday_Weather(contentList.get(i));
            //data.setContents(rsIdList.get(i));

            // adapter에 방금 만든 Data 객체를 추가해 넣는다.
            adapter.addItem(data);
        }

        // adapter 내용의 값이 변경되었음을 알려준다. 이 함수를 쓰지않으면 data가 노출안된다.
        // 다만, recyclerView1.setAdapter() 함수가 data를 추가시켜준 뒤에 호출되었다면 정상적으로  data 노출된다.
        adapter.notifyDataSetChanged();
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

