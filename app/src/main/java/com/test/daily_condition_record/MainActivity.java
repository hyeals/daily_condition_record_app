package com.test.daily_condition_record;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.test.daily_condition_record.Room.AppDatabase;
import com.test.daily_condition_record.Room.User;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    protected double now_longitude;
    protected double now_latitude;
    protected double now_altitude;


    // 년도 가져오는 클래스
    Today today = new Today();

    public RecyclerAdapter adapter;
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
        int size = users.size(); // size를 구함

        for (int i = 0; i < size; i++) { // size 크기만큼 adapter에 additems 함수를 통해 내용 하나하나 추가.
            adapter.addItem(users.get(i));
            System.out.println("####" + AppDatabase.getInstance(this).userDao().getAll().get(i));
        }

        recyclerView1.setAdapter(adapter);

        // GPS 가져오기
        // [참고] https://developers.google.com/maps/documentation/android-sdk/location?hl=ko
        // [참고] https://bbaktaeho-95.tistory.com/56
        enableLocation();

    }

    // GPS, 앨범 권한 검증
    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) { // GPS 권한 체크

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){ // 앨범 권한 체크

                Log.d("gps", "can use gps");
                // gps 위치 받아오기
                // 내 위치 검색
                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                //String provider = location.getProvider();

                // 내 위치 가져오기
                now_longitude = location.getLongitude(); // 경도
                now_latitude = location.getLatitude(); // 위도
            }else{
                requestPermission();
            }

        } else {
            requestPermission();
        }
    }

    // GPS or 앨범 권한이 없을 경우, 권한 요청 메소드
    void requestPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, 2021);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 2021){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enableLocation();

            }else{
                finish();
            }
        }
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
                // Intent intent = new Intent(this, PostActivity.class);
                // startActivity(intent);
                Intent intent = new Intent(this, PostActivity.class);
                intent.putExtra("longtitude", now_longitude);
                intent.putExtra("latitude", now_latitude);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    //////// 상단 툴바 /////////

}

