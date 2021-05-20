package com.test.daily_condition_record;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.daily_condition_record.Room.AppDatabase;
import com.test.daily_condition_record.Room.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import static android.os.Environment.getExternalStorageState;


public class PostActivity extends AppCompatActivity {

    private static final String WEATHER_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst";
    private static final String SERVICE_KEY = "신청하여 승인된 일반 인증키(UTF-8) 값";
    // 신청하여 승인된 일반 인증키(UTF-8) 값

    //    WeatherInfoTask weatherTask;
    TextView textView;

    // 오늘 날짜, 요일 가져오는 클래스
    Today today = new Today();
    // 날짜 TextView
    TextView dateTextView;
    // 요일 TextView
    TextView weekDayTextView;

    // GPS 위경도 -> 좌표값으로 전환하는 계산을 하는 클래스
    ConvGPS convGPS = new ConvGPS();
    // GPS 위경도 -> 좌표값으로 전환하는 계산을 한 클래스의 리턴값을 받아오는 클래스
    LatXLngY latXLngY = new LatXLngY();
    double longitude;
    double latitude;

    // 메모에 사용
    private final int REQUEST_CODE = 200;
    private EditText writeText;
    private TextView viewText;
    private TextView guideTextView;
    private TextView result; // 테스트용
    private AppDatabase db;
    String temp_date = today.getDate();
    String temp_weather = null;
    String temp_img = null;

    // DB에서 받아온 데이터들.
    String item_date = null;
    String item_des = null;
    String item_weather = null;
    String item_img = null;
    int position;

    // 이미지 뷰에 사용
    // 참고 https://velog.io/@moontae/%EC%B9%B4%EB%A9%94%EB%9D%BC%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%98%EC%97%AC-%EC%82%AC%EC%A7%84%EC%B4%AC%EC%98%81-%EB%B0%8F-%EC%82%AC%EC%A7%84%EC%B2%A9%EC%97%90%EC%84%9C-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EB%B6%88%EB%9F%AC%EC%98%A4%EA%B8%B0
    public AlertDialog.Builder dialog;
    private int REQUEST_IMAGE_PICK = 0; // 앨범에서 사진 가져오기 요청 코드
    private ImageView putPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Button button = findViewById(R.id.button); // 저장버튼
        textView = findViewById(R.id.textView); // 날씨
        dateTextView = findViewById(R.id.dateTextView); // 날짜
        weekDayTextView = findViewById(R.id.weekDayTextView); // 요일

        writeText = findViewById(R.id.writeText); // https://mynamewoon.tistory.com/15?category=833237에서 initialized 함수
        viewText = findViewById(R.id.viewText);
        result = findViewById(R.id.result); // 테스트용
        db = AppDatabase.getInstance(this);

        putPhoto = findViewById(R.id.putPhoto);

        writeText.setVisibility(View.INVISIBLE);

        // 이미지 뷰 클릭시 앨범으로부터 사진 가져오기 기능
        // 다이얼로그의 앨범선택, 취소 버튼 리스너 생성
        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakeAlbumAction(); // 앨범에서 이미지를 가져오는 함수
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        // 앨범 다이얼로그 생성
        dialog = new AlertDialog.Builder(this).setTitle("업로드할 이미지 선택").setNeutralButton("취소", cancelListener).setNegativeButton("앨범 선택", albumListener);

        // 저장 버튼 터치시 -> 로컬 db(ROOM)에 저장 이벤트 발생. // https://mynamewoon.tistory.com/15?category=833237
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                db.userDao().insert(new User(writeText.getText().toString(), temp_date, temp_weather, temp_img));
                db.userDao().update(item_weather, item_date, writeText.getText().toString(), temp_img, position + 1); // (position + 1)을 해야 UserDao.java의 id와 맞음.
                System.out.println("이미지 아이디!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+temp_img); // temp_img는 사용자가 수정하기 위해 선택한 이미지.
                System.out.println(db.userDao().getAll().toString());
                result.setText(db.userDao().getAll().toString());
                hideKeyboard(); // 저장버튼 클릭 -> 키보드 숨김.

                writeText.setVisibility(View.INVISIBLE);
                viewText.setVisibility(View.VISIBLE);
                viewText.setText(writeText.getText());

                Intent intent = new Intent();
                intent.putExtra("refresh", REQUEST_CODE);
                //setResult(REQUEST_OK, intent);

                ///////onResume() 되기전에 업데이트 : https://mynamewoon.tistory.com/18 2번의 move()함수 시작/////////
                Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent2, 1);
                /////// https://mynamewoon.tistory.com/18 2번의 move()함수 끝/////////


                finish();
            }
        });

        Intent intent = getIntent();
        item_date = intent.getStringExtra("item_date");
        item_des = intent.getStringExtra("item_des");
        item_weather = intent.getStringExtra("item_weather");
        item_img = intent.getStringExtra("item_img");
        position = intent.getIntExtra("position", 100);

//        dateTextView.setText(item_date); // 저장된 날짜 텍스트뷰에 받아오기
        textView.setText(item_weather);
//        weekDayTextView.setText(item_@@@); // 저장된 요일 텍스트뷰에 받아오기
        viewText.setText(item_des); // 저장된 메모 텍스트뷰에 받아오기

        if (item_img == null) { // 선택한 아이템에서, DB에 저장된 이미지가 없을 때(예외처리 해줘야 오류 안남.)

        }
        else { // 선택한 아이템에서, DB에 저장된 이미지가 있을 때
            System.out.println(item_img + "이미지이미지이미지이미지이미지이미지");
            System.out.println(Uri.parse(item_img) + "ddddddddddddddddddddddd");
            putPhoto.setImageURI(Uri.parse(item_img)); // 저장된 이미지(string) 이미지뷰(uri)에 받아오기 // 참고 : https://hashcode.co.kr/questions/1080/string%EC%9D%84-uri%EB%A1%9C-%EB%B0%94%EA%BE%B8%EB%8A%94-%EB%B0%A9%EB%B2%95
        }

        // viewText 터치 시 이미지뷰를 포함하고 있는 layout 감추기
        writeText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        findViewById(R.id.cView).setVisibility(View.GONE);
                }
                return false;
            }
        });

        // editText(writeText) 키보드 이외에 다른 곳 누르면 키보드 내림과 동시에 이미지뷰를 포함하고 있는 레이아웃 나타내기
        findViewById(R.id.postActicity).setOnClickListener(new View.OnClickListener(
        ) {
            @Override
            public void onClick(View v) {
                Log.d("Ltest", "layout touch event");
                findViewById(R.id.cView).setVisibility(View.VISIBLE);
                hideKeyboard();
            }
        });

        // GPS로 얻은 위경도 값 -> 좌표 값으로 변환
        Intent getIntent = getIntent();
        latitude = getIntent.getDoubleExtra("latitude", 0); // 위도
        longitude = getIntent.getDoubleExtra("longtitude", 0); // 경도

        latXLngY = convGPS.convertGRID_GPS(true, latitude, longitude);

        Log.d("x좌표값:", String.valueOf((int) latXLngY.x));
        Log.d("y좌표값:", String.valueOf((int) latXLngY.y));

        // GPS 가져오기 테스트
        // viewText.setText("longtitude: " + String.valueOf(latXLngY.x) + "latitude: " + String.valueOf(latXLngY.y));

    }


    // 키보드 내리기 함수
    public void hideKeyboard() {
        writeText = findViewById(R.id.writeText);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(writeText.getWindowToken(), 0);
    }


    ////// 상단 툴바 시작 //////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // 메뉴 생성
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 버튼 ID
                finish();

            case R.id.edit: // 포스팅 수정
                viewText.setVisibility(View.INVISIBLE);
                writeText.setVisibility(View.VISIBLE);
                writeText.setText(viewText.getText());

                putPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.show();
                    }
                });
//            case R.id.delete: // 포스팅 삭제 - PostActivity.java에서의 메모삭제는 추후에 진행.
//                db.userDao().delete(position + 1); // (position + 1)을 해야 UserDao.java의 id와 맞음.
        }
        return super.onOptionsItemSelected(item);
    }
    ////// 상단 툴바 끝 //////

    //// 앨범에서 이미지 가져오기 ////
    public void doTakeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // ACTION_GET_CONTENT <- ACTION_PICK 참고 : https://o-s-z.tistory.com/60
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    // doTakeAlbumAction함수 실행 후 startActivityForResult로부터 받아온 결과값 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK) {
            if (data == null) { // data가 null일때는 앨범에서 뒤로가기 눌렀을때 data가 없기때문에 생기는 오류를 잡아주기 위함. 참고 : https://namhandong.tistory.com/43
            } else {
                putPhoto.setImageURI(data.getData());
                temp_img = data.getData().toString(); // Uri(uri) -> Uri(String)으로 변경해서 temp_img변수에 저장.

                // 이미지 선택하면 앨번선택 안내문구 안보이게 하기
                guideTextView = findViewById(R.id.guideTextView);
                guideTextView.setVisibility(View.GONE);
            }
        }
    }


}