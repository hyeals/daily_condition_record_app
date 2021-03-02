package com.test.daily_condition_record;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    //    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Button button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag", "onClick");
                getWeatherInfo();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: // 뒤로가기 버튼 ID
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getWeatherInfo() {
        if (weatherTask != null) {
            weatherTask.cancel(true);
        }
        weatherTask = new WeatherInfoTask();
        weatherTask.execute();
    }

    private class WeatherInfoTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder urlBuilder = new StringBuilder(WEATHER_URL); /*URL*/
            StringBuilder sb = null;
            try {
                urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + SERVICE_KEY); /*Service Key*/
                urlBuilder.append("&" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + URLEncoder.encode("-", "UTF-8")); /*공공데이터포털에서 받은 인증키*/
                urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
                urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
                urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON)Default: XML*/
                urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode("20210226", "UTF-8")); /*21년 02월 26일발표*/
                urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("0500", "UTF-8")); /*05시 발표*/
                urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*예보지점 X 좌표값*/
                urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*예보지점의 Y 좌표값*/
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

            //-------------------- 기상청 JSON 데이터 파싱 시작 -----------------------
            // PTY: 강수형태 fcstValue: PTY값
            // root(json 전체 데이터 객체) -> response 객체 -> body 객체 -> items 객체 -> item 리스트 -> category: "PTY" -> fcstValue: "value~~"
            JSONObject root = null;
            JSONObject parse_response = null;
            JSONObject parse_body = null;
            JSONObject parse_items = null;
            JSONArray parse_item = null;
            JSONObject data = null;
            String category = null;
            String fcstValue = null;
            try {
                root = new JSONObject(sb.toString());
                parse_response = root.getJSONObject("response");
                parse_body = parse_response.getJSONObject("body");
                parse_items = parse_body.getJSONObject("items");
                parse_item = parse_items.getJSONArray("item");
            } catch (JSONException e) {
                e.printStackTrace();
            }
// item 리스트에서 PTY 데이터 가져오기
            for(int i=0; i<100; i++) {
                try {
                    data = parse_item.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    category = data.getString("category");
                    fcstValue = data.getString("fcstValue");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (category.equals("PTY")) break;
            }
            return "category: " + category + " " + "fcstValue: " + fcstValue;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            textView.setText(s);
        }
    }

}