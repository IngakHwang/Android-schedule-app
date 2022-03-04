package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.java.mainListdata;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class todayAct extends AppCompatActivity {

    long mNow;
    Date mDate;
    String LOG = "TodayAct";
    ArrayList<mainListdata> todaylist = new ArrayList<>();
    ArrayList<mainListdata> nottodaytlist = new ArrayList<>();
    mainListdata edlist;
    mainListdata restlist;
    TextView today_noitem;
    ImageView today_img;
    private MainHandler mainHandler = new MainHandler();

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private subAdapter subAdapter;

    private AlarmManager alarmManager;
    private NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    int num = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);


        setTitle("오늘 할 일");

        mNow = System.currentTimeMillis(); // 현재시간을 msec으로 구한다.
        mDate = new Date(mNow); // 현재시간을 date변수에 저장한다.

        SimpleDateFormat mNow = new SimpleDateFormat("MM/dd");
        SimpleDateFormat mNow_year = new SimpleDateFormat("yyyy");
        SimpleDateFormat mNow_month = new SimpleDateFormat("M");
        SimpleDateFormat mNow_day = new SimpleDateFormat("d");
        SimpleDateFormat mNow_Time = new SimpleDateFormat("HH:00");
        SimpleDateFormat mNow_Hour = new SimpleDateFormat("HH");

        SimpleDateFormat mNowToday = new SimpleDateFormat("yyyy-MM-dd");

        Date currentTime = Calendar.getInstance().getTime();

        String syear = mNow_year.format(currentTime);
        String smonth = mNow_month.format(currentTime);
        String sday = mNow_day.format(currentTime);

        String todaynow = syear + "/"+smonth +"/"+ sday;
        Log.i("오늘날짜",todaynow+"");

        SharedPreferences mainShared = getSharedPreferences(mainAct.ID+" reminder", MODE_PRIVATE);
        String load = mainShared.getString(mainAct.ID,null);

        try {
            JSONArray jarray = new JSONArray(load);
            String JSON길이 = Integer.toString(jarray.length());
            for (int i = 0; i < jarray.length(); i++){

                Log.i("todayAct","JSON 길이"+JSON길이);
                JSONObject jsonObject = jarray.getJSONObject(i);
                String title = jsonObject.optString("additemcard_title","");
                String memo = jsonObject.optString("additemcard_memo","");
                String location = jsonObject.optString("additemcard_location","");
                String date = jsonObject.optString("additemcard_date","");
                String time = jsonObject.optString("additemcard_time","");
                String important = jsonObject.optString("additemcard_important","");

                if(date.equals(todaynow)){
                    edlist = new mainListdata(title,memo,location,date,time,important);
                    String 데이터추가 = Integer.toString(i);

                    Log.i("lmportantAct","추가하기"+ 데이터추가);
                    Log.i(LOG, date); // 2021/5/31
                    Log.i(LOG, time); // 7:26

                    todaylist.add(edlist);
                    Log.i("리스트 사이즈",""+todaylist.size());

                    notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

                    int requestID = (int) System.currentTimeMillis();

                    Intent receiverIntent = new Intent(todayAct.this, AlarmRecevier.class);
                    receiverIntent.putExtra("Title",title);
                    receiverIntent.putExtra("Memo",memo);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(todayAct.this,requestID,receiverIntent,0);


                    String from = date +" "+time;
                    Log.i(LOG, from); // 2021/5/31 7:26

                    // String test = "2021-05-31 10:31";
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/M/d HH:mm");
                    Date datetime = null;
                    try{
                      datetime = dateFormat.parse(from);
                    }catch (ParseException e){
                        e.printStackTrace();
                    }

                    Log.i(LOG,""+datetime);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(datetime);

                    alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(),pendingIntent); // 145

                }
                else{
                    restlist = new mainListdata(title,memo,location,date,time,important);
                    nottodaytlist.add(restlist);
                    Log.i("리스트 사이즈",""+nottodaytlist.size());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        today_noitem = (TextView) findViewById(R.id.today_noitem);
        if(todaylist.size() <=0 ){
            today_noitem.setText("오늘 할 일이 없습니다.");
        } else if(todaylist.size() > 1){
            today_noitem.setText("");
        }

        recyclerView = (RecyclerView) findViewById(R.id.today_recView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        subAdapter = new subAdapter(todaylist, this);

        recyclerView.setAdapter(subAdapter);

        today_img = (ImageView) findViewById(R.id.today_img);
        BackgroundThread thread = new BackgroundThread();
        thread.start();

        mainHandler = new MainHandler();


        /*String result = "";
        try{

          JSONArray ja = new JSONArray(load);
          for (int i = 0; i < ja.length(); i++){
              JSONObject order = ja.getJSONObject(i);
              result += order.getString("additemcard_date")+order.getString("additemcard_location")+order.getString("additemcard_memo")+order.getString("additemcard_title")+"\n";
          }
        }catch (JSONException e){

        }

        Log.i("데이터 분리하기",result+"");*/

        /*try{
            JSONObject jsonObject = new JSONObject(load);
            JSONArray jsonArray = jsonObject.getJSONArray(mainAct.ID+" reminder");
            ArrayList<String> list = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++){
                jsonObject = jsonArray.getJSONObject(i);
                //list.add(jsonObject.getInt)
                //https://mailmail.tistory.com/11
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        // 쉐어드 가져오기 -> Arraylist 분리 -> 오늘날짜로 같은 애들 가져오기
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action:
                Toast.makeText(getApplicationContext(),"저장 중", Toast.LENGTH_SHORT).show();
                if(todaylist.size()==0){
                    SharedPreferences mainShared = getSharedPreferences(mainAct.ID+" reminder", MODE_PRIVATE);
                    SharedPreferences.Editor editor = mainShared.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(nottodaytlist);
                    editor.putString(mainAct.ID,json);
                    editor.apply();

                    Intent intent = new Intent(todayAct.this,saveAct.class);
                    startActivity(intent);
                }
                else if(todaylist.size()>=1){
                    for (int i = 0; i < todaylist.size(); i++){
                        nottodaytlist.add(todaylist.get(i));
                    }
                    SharedPreferences mainShared = getSharedPreferences(mainAct.ID+" reminder", MODE_PRIVATE);
                    SharedPreferences.Editor editor = mainShared.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(nottodaytlist);
                    editor.putString(mainAct.ID,json);
                    editor.apply();

                    Intent intent = new Intent(todayAct.this,saveAct.class);
                    startActivity(intent);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private class MainHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int img = bundle.getInt("img");
            String uri = bundle.getString("uri");
            today_img.setImageResource(img);
            today_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(uri));
                    startActivity(i);
                }
            });
        }
    }
    private class BackgroundThread extends Thread{
        int value=0;
        int img;
        String uri;

        @Override
        public void run() {
            while(true){
                value = value+1;
                if(value==1){
                    img = R.drawable.google2;
                    uri = "https://www.google.com";
                }
                else if(value==2){
                    img = R.drawable.naver2;
                    uri = "https://m.naver.com";
                }
                else if(value==3){
                    img = R.drawable.kakao2;
                    uri = "https://m.daum.net";
                    value=0;
                }


                Bundle bundle = new Bundle();
                bundle.putInt("img",img);
                bundle.putString("uri",uri);
                Message message = mainHandler.obtainMessage();
                message.setData(bundle);

                mainHandler.sendMessage(message);

                try{
                    Thread.sleep(4000);
                }catch (Exception e){ }
            }
        }
    }
}