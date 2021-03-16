package com.test.daily_condition_record;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

    TextView tv;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText(today.getYear());
        setSupportActionBar(toolbar);

        tv = (TextView) findViewById(R.id.textView2); // 결과창
        tv.setText("위치정보 미수신중");

        // LocationManager 객체를 얻어온다.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Location 제공자에서 정보를 얻어오기(GPS)
        // 1. Location을 사용하기 위한 권한을 얻어와야한다 AndroidManifest.xml
        //     ACCESS_FINE_LOCATION : NETWORK_PROVIDER, GPS_PROVIDER
        //     ACCESS_COARSE_LOCATION : NETWORK_PROVIDER
        // 2. LocationManager 를 통해서 원하는 제공자의 리스너 등록
        // 3. GPS 는 에뮬레이터에서는 기본적으로 동작하지 않는다
        // 4. 실내에서는 GPS_PROVIDER 를 요청해도 응답이 없다.  특별한 처리를 안하면 아무리 시간이 지나도
        //    응답이 없다.
        //    해결방법은
        //     ① 타이머를 설정하여 GPS_PROVIDER 에서 일정시간 응답이 없는 경우 NETWORK_PROVIDER로 전환
        //     ② 혹은, 둘다 한꺼번헤 호출하여 들어오는 값을 사용하는 방식.

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

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) { // 앨범 권한 체크

                Log.d("gps", "can use gps");

                // gps 위치 받아오기
                // 내 위치 검색
//                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                Log.d("위치 오류 확인", String.valueOf(location));
                //String provider = location.getProvider();

                tv.setText("수신중..");
                // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자(GPS)
                        100, // 통지사이의 최소 시간간격 (miliSecond)
                        1, // 통지사이의 최소 변경거리 (m)
                        mLocationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자(NetWork)
                        100, // 통지사이의 최소 시간간격 (miliSecond)
                        1, // 통지사이의 최소 변경거리 (m)
                        mLocationListener);

//                // 내 위치 가져오기(하단 LocationListener 함수로 이동)
//                now_longitude = location.getLongitude(); // 경도
//                now_latitude = location.getLatitude(); // 위도
            } else {
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
        if (requestCode == 2021) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                enableLocation();

            } else {
                finish();
            }
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            tv.setText("위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
                    + "\n고도 : " + altitude + "\n정확도 : " + accuracy);

            // 내 위치 가져오기
            now_longitude = longitude; // 경도
            now_latitude = latitude; // 위도
        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

    //////// 상단 툴바 /////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_plus_button: // 피드 추가 기능 버튼
                Intent intent = new Intent(this, PostActivity.class);
                intent.putExtra("longtitude", now_longitude);
                intent.putExtra("latitude", now_latitude);

                tv.setText("위치정보 미수신중");
                locationManager.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.

                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    //////// 상단 툴바 /////////

}
