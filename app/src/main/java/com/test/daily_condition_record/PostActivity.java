package com.test.daily_condition_record;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.test.daily_condition_record.Room.AppDatabase;
import com.test.daily_condition_record.Room.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;



public class PostActivity extends AppCompatActivity {

    private static final String WEATHER_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst";
    private static final String SERVICE_KEY = "Y7f%2FstE428Zaku8UJVA83CK2JHGsFa96LktLpWOZBkWmP4S3mmhewY2DxwxvYZ0H%2F7b4l3XCbOEOE1XLMdwDWA%3D%3D";
    // 신청하여 승인된 일반 인증키(UTF-8) 값

    WeatherInfoTask weatherTask;
    TextView textView;

    // 오늘 날짜, 요일 가져오는 클래스
    Today today = new Today();
    // 날짜 TextView
    TextView dateTextView;
    // 요일 TextView
    TextView weekDayTextView;

    // 메모에 사용
    private final int REQUEST_CODE = 200; // 임의의 값으로 설정
    private EditText writeText;
    private TextView result; // 테스트용
    private AppDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Button button = findViewById(R.id.button); // 저장버튼
        textView = findViewById(R.id.textView);
        dateTextView = findViewById(R.id.dateTextView);
        weekDayTextView = findViewById(R.id.weekDay);

        writeText = findViewById(R.id.writeText); // https://mynamewoon.tistory.com/15?category=833237에서 initialized 함수
        result = findViewById(R.id.result);
        db = AppDatabase.getInstance(this);

        // 저장 버튼 터치시 -> 로컬 db(ROOM)에 저장 이벤트 발생. // https://mynamewoon.tistory.com/15?category=833237
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.userDao().insert(new User(writeText.getText().toString()));
                result.setText(db.userDao().getAll().toString());
                hideKeyboard(); // 저장버튼 클릭 -> 키보드 숨김.

                Intent intent = new Intent();
                intent.putExtra("refresh", REQUEST_CODE);
                setResult(RESULT_OK, intent);

                ///////onResume() 되기전에 업데이트 : https://mynamewoon.tistory.com/18 2번의 move()함수 시작/////////
                Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent2, 1);
                /////// https://mynamewoon.tistory.com/18 2번의 move()함수 끝/////////

                finish();
            }
        });

        getWeatherInfo(); // (버튼이벤트 없이) 날씨 받아오기

        // EditText 터치 시 이미지뷰를 포함하고 있는 layout 감추기
        writeText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN: findViewById(R.id.cView).setVisibility(View.GONE);
                }
                return false;
            }
        });

        //오늘 날짜 텍스트뷰에 받아오기
        dateTextView.setText(today.getDate());
        // 오늘 요일 텍스트뷰에 받아오기
        weekDayTextView.setText(today.getWeekDay());

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
    }

    // 키보드 내리기 함수
    public void hideKeyboard(){
        writeText = findViewById(R.id.writeText);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(writeText.getWindowToken(), 0);
    }

    ////// 상단 툴바 시작 //////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 버튼 ID
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
    ////// 상단 툴바 끝 //////

    private void getWeatherInfo() {
        if (weatherTask != null) {
            weatherTask.cancel(true);
        }
        weatherTask = new WeatherInfoTask();
        weatherTask.execute();
    }

    // 기상청 날씨 API 연동 + 데이터 파싱
    private class WeatherInfoTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder urlBuilder = new StringBuilder(WEATHER_URL); /*URL*/
            StringBuilder sb = null;
            try {
                urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + SERVICE_KEY); /*Service Key*/
                urlBuilder.append("&" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + URLEncoder.encode("-", "UTF-8")); /*공공데이터포털에서 받은 인증키*/
                urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
                urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
                urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON)Default: XML*/
                urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(today.ToApiDate(), "UTF-8")); /*21년 02월 26일발표*/
                urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode("0500", "UTF-8")); /*05시 발표*/
                urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*예보지점 X 좌표값*/
                urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*예보지점의 Y 좌표값*/
                //urlBuilder.append("&" + URLEncoder.encode("ftype", "UTF-8") + "=" + URLEncoder.encode("ODAM", "UTF-8")); /*파일구분 -ODAM: 동네예보실황 -VSRT: 동네예보초단기 -SHRT: 동네예보단기*/
                //urlBuilder.append("&" + URLEncoder.encode("basedatetime", "UTF-8") + "=" + URLEncoder.encode("20210226050000", "UTF-8"));

                /*각각의 base_time 로 검색 참고자료 참조 : 규정된 시각 정보를 넣어주어야 함 */

                URL url = new URL(urlBuilder.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                System.out.println("Response code: " + conn.getResponseCode());

                BufferedReader rd;
                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            //-------------------- 기상청 JSON 데이터 파싱 시작 -----------------------//
            // PTY: 강수형태 fcstValue: PTY값
            // root(json 전체 데이터 객체) -> response 객체 -> body 객체 -> items 객체 -> item 리스트 -> category: "PTY" -> fcstValue: "value~~"
            JSONObject root = null;
            JSONObject parse_response = null;
            JSONObject parse_body = null;
            JSONObject parse_items = null;
            JSONArray parse_item = null;
            JSONObject data = null;
            String category_SKY = "sky";
            String fcstValue_SKY = null;
            String category_PTY = "pty";
            String fcstValue_PTY = null;

            String weather_result = "?";

            try {
                root = new JSONObject(sb.toString());
                parse_response = root.getJSONObject("response");
                parse_body = parse_response.getJSONObject("body");
                parse_items = parse_body.getJSONObject("items");
                parse_item = parse_items.getJSONArray("item");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // item 리스트에서 SKY, PTY 데이터 가져오기
            for (int i = 0; i < 100; i++) {
                try {
                    data = parse_item.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if(!category_SKY.equals("SKY")){ // SKY 파싱
                        category_SKY = data.getString("category");
                        fcstValue_SKY = data.getString("fcstValue");
                    }

                    if(!category_PTY.equals("PTY")){ // PTY 파싱
                        category_PTY = data.getString("category");
                        fcstValue_PTY = data.getString("fcstValue");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(fcstValue_PTY.equals("0")){ // 강수 없음.
                switch(fcstValue_SKY){ // 하늘 상태
                    case "1": weather_result = "맑음"; break;
                    case "3": weather_result = "구름 많음"; break;
                    case "4": weather_result = "흐림"; break;
                }
            }
            else{ // 강수 있다면,
                switch (fcstValue_PTY){
                    case "1": weather_result = "비"; break;
                    case "2": weather_result = "진눈깨비"; break;
                    case "3": weather_result = "눈"; break;
                    case "4": weather_result = "소나기"; break;
                    case "5": weather_result = "빗방울"; break;
                    case "6": weather_result = "빗방울 또는 눈날림"; break;
                    case "7": weather_result = "눈날림"; break;
                }
            }

            return weather_result;
        }
        //-------------------- 기상청 JSON 데이터 파싱 끝 -----------------------//

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            textView.setText(s);
        }
    }

}
