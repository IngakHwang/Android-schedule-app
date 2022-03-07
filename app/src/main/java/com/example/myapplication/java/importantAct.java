package com.example.myapplication.java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.myapplication.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class importantAct extends AppCompatActivity {

    ArrayList<mainListdata> importantlist = new ArrayList<>();
    ArrayList<mainListdata> notimportlist = new ArrayList<>();

    private MainHandler mainHandler = new MainHandler();

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private com.example.myapplication.java.subAdapter subAdapter;
    mainListdata edlist, restlist;
    TextView important_noitem;
    ImageView important_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important);
        setTitle("중요");



        SharedPreferences mainShared = getSharedPreferences(mainAct.ID+" reminder", MODE_PRIVATE);
        String load = mainShared.getString(mainAct.ID,null);

        try {
            JSONArray jarray = new JSONArray(load);
            String JSON길이 = Integer.toString(jarray.length());
            for (int i = 0; i < jarray.length(); i++){

                Log.i("importantAct","JSON 길이"+JSON길이);
                JSONObject jsonObject = jarray.getJSONObject(i);
                String title = jsonObject.optString("additemcard_title","");
                String memo = jsonObject.optString("additemcard_memo","");
                String location = jsonObject.optString("additemcard_location","");
                String date = jsonObject.optString("additemcard_date","");
                String time = jsonObject.optString("additemcard_time","");
                String important = jsonObject.optString("additemcard_important","");

                if(important.equals("중요")){
                    edlist = new mainListdata(title,memo,location,date,time,important);
                    String 데이터추가 = Integer.toString(i);

                    Log.i("lmportantAct","추가하기"+ 데이터추가);

                    importantlist.add(edlist);
                }
                else{
                    restlist = new mainListdata(title,memo,location,date,time,important);
                    notimportlist.add(restlist);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        important_noitem = (TextView) findViewById(R.id.important_noitem);
        if(importantlist.size() <=0 ){
            important_noitem.setText("중요 일이 없습니다.");
        } else if(importantlist.size() > 1){
            important_noitem.setText("");
        }

        recyclerView = (RecyclerView) findViewById(R.id.important_recView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        subAdapter = new subAdapter(importantlist, this);

        recyclerView.setAdapter(subAdapter);
        important_img = (ImageView) findViewById(R.id.important_img);
        BackgroundThread thread = new BackgroundThread();
        thread.start();

        mainHandler = new MainHandler();

        // 쉐어드 가져오기 -> Arraylist 분리 -> 중요 포인트 잡기
    }

    private class MainHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int img = bundle.getInt("img");
            String uri = bundle.getString("uri");
            important_img.setImageResource(img);
            important_img.setOnClickListener(new View.OnClickListener() {
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
                if(importantlist.size()==0){
                    SharedPreferences mainShared = getSharedPreferences(mainAct.ID+" reminder", MODE_PRIVATE);
                    SharedPreferences.Editor editor = mainShared.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(notimportlist);
                    editor.putString(mainAct.ID,json);
                    editor.apply();

                    Intent intent = new Intent(importantAct.this, saveAct.class);
                    startActivity(intent);
                }
                else if(importantlist.size()>=1){
                    for (int i = 0; i < importantlist.size(); i++){
                        notimportlist.add(importantlist.get(i));
                    }
                    SharedPreferences mainShared = getSharedPreferences(mainAct.ID+" reminder", MODE_PRIVATE);
                    SharedPreferences.Editor editor = mainShared.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(notimportlist);
                    editor.putString(mainAct.ID,json);
                    editor.apply();

                    Intent intent = new Intent(importantAct.this,saveAct.class);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}