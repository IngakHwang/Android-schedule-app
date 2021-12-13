package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kakao.sdk.user.UserApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class mainAct extends AppCompatActivity /*implements mainAdapter.OnItemClickListener*/{

    static String ID;

    Button main_todaybtn, main_importantbtn,main_addlist, main_timerbtn, main_weather;
    TextView main_emetytextView;
    String LOG = "MainAct";

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    static ArrayList<mainListdata> listdata;
    private mainAdapter mainAdapter;
    static final int GET_INTENT = 1;

    static ArrayList<String> weatheritem = new ArrayList<>();
    static int weather =0;

    static ArrayList<String> dustitem = new ArrayList<>();
    static int dust = 0;

    static ArrayList<String> coviditem = new ArrayList<>();
    static int covid = 0;

    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    public static double currentLatitude;
    public static double currentLongitude;
    public static String sido;
    public static String gugun;
    public static int wedo;
    public static int gangdo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("메인화면");

        if(checkLocationServicesStatus()){
            checkRunTimePermission();
        }else{
            showDialogForLocationServiceSetting();
        }

        gpsTracker = new GpsTracker(mainAct.this);

        currentLatitude = gpsTracker.getLatitude();
        currentLongitude = gpsTracker.getLongitude();

        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> gList = null;
        try{
            gList = geocoder.getFromLocation(currentLatitude, currentLongitude,8);
        }catch (IOException e){
            e.printStackTrace();
        }
        if(gList != null) {
            if (gList.size() == 0) {

            } else {
                Address address = gList.get(0);
                sido = address.getAdminArea();
                gugun = address.getSubLocality();

                Log.i("위도",""+currentLatitude);
                Log.i("경도",""+currentLongitude);

                wedo = (int) Math.floor(currentLatitude);
                gangdo = (int) Math.floor(currentLongitude);
                Log.i("위도내림",""+wedo);
                Log.i("경도내림",""+gangdo);

                Log.i("시", "" + sido);
                Log.i("구", "" + gugun);
            }
        }

        Intent intent = getIntent();
        String userID = intent.getStringExtra("inputID");
        ID = userID;
        Log.i(LOG,ID+"로그");
        weatheritem.clear();
        dustitem.clear();
        coviditem.clear();

        weather=0; dust=0; covid=0;


        String weatherurl = weatherurl();
        Log.i("날씨API",""+weatherurl);
        WeatherAPI weatherAPI = new WeatherAPI(weatherurl);
        weatherAPI.execute();

        String dusturl = dusturl();
        Log.i("미세먼지API",""+dusturl);
        DustAPI dustAPI = new DustAPI(dusturl);
        dustAPI.execute();

        String covidurl = covidurl();
        Log.i("코로나API",""+covidurl);
        CovidAPI covidAPI = new CovidAPI(covidurl);
        covidAPI.execute();


        loadData();

        recyclerView = (RecyclerView) findViewById(R.id.main_recView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        mainAdapter = new mainAdapter(listdata, this);

        recyclerView.setAdapter(mainAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.START|ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return mainAdapter.moveItem(viewHolder.getAdapterPosition(),target.getAdapterPosition());

            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                mainAdapter.removeItem(viewHolder.getAdapterPosition());
                String postion = Integer.toString(viewHolder.getAdapterPosition());

                Log.i(LOG,"아이템 삭제 "+postion+"번");

            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
                    viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                }
            }
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);


        main_todaybtn=(Button) findViewById(R.id.main_todaybtn);
        main_importantbtn=(Button) findViewById(R.id.main_importantbtn);
        main_addlist=(Button) findViewById(R.id.main_addlist);
        main_emetytextView=(TextView) findViewById(R.id.main_emetytextView);
        main_timerbtn = (Button) findViewById(R.id.main_timerbtn);
        main_weather = (Button) findViewById(R.id.main_weather);

        main_todaybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainAct.this,todayAct.class);
                startActivity(intent);
            }
        });

        main_importantbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainAct.this, importantAct.class);
                startActivity(intent);
            }
        });

        main_addlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainAct.this,addreminderAct.class);
                startActivityForResult(intent,GET_INTENT);
            }
        });

        main_timerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainAct.this,timerAct.class);
                startActivity(intent);
            }
        });

        main_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainAct.this,weatherAct.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        saveData();
        Log.i("onPause","파일저장");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("onPause","리줌");
    }

    public void loadData(){
        SharedPreferences mainShared = getSharedPreferences(ID+" reminder", MODE_PRIVATE);
        Gson gson = new Gson();
        String load = mainShared.getString(ID,null);
        Type type = new TypeToken<ArrayList<mainListdata>>(){}.getType();
        listdata = gson.fromJson(load, type);
        Log.i(LOG,"쉐어드에서 로드완료");

        if(listdata == null){
            listdata = new ArrayList<>();
            Log.i(LOG,"쉐어드에서 로드아무것도 없음");
        }
    }

    public void saveData(){
        SharedPreferences mainShared = getSharedPreferences(ID+" reminder", MODE_PRIVATE);
        SharedPreferences.Editor editor = mainShared.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listdata);
        editor.putString(ID,json);
        editor.apply();
        Log.i(LOG,"쉐어드 저장");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GET_INTENT && resultCode == RESULT_OK && data != null){
            String title = data.getStringExtra("title");
            String memo = data.getStringExtra("memo");
            String location = data.getStringExtra("location");
            String date = data.getStringExtra("date");
            String time = data.getStringExtra("time");
            String important = data.getStringExtra("important");

            mainListdata adddata = new mainListdata(title,memo,location,date,time,important);
            listdata.add(adddata);

            mainAdapter.notifyDataSetChanged();

            Log.i(LOG,"리사이클러뷰 아이템 추가");

        }
        switch(requestCode){
            case GPS_ENABLE_REQUEST_CODE:
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }

    }
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(mainAct.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(mainAct.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(mainAct.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(mainAct.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(mainAct.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(mainAct.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mainAct.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item1:
                Intent intent2 = new Intent(mainAct.this,profileAct.class);
                startActivity(intent2);
                return true;
            case R.id.menu_item2:
                UserApiClient.getInstance().logout(error ->{
                    if(error != null){
                        Log.i("카카오","로그아웃 실패",error);
                    }
                    else{
                        Log.i("카카오", "로그아웃 성공");
                    }
                    return null;
                });
                SharedPreferences autologinshard = getSharedPreferences("AutoLogin",MODE_PRIVATE);
                SharedPreferences.Editor editorAL = autologinshard.edit();
                editorAL.clear();
                editorAL.commit();
                Toast.makeText(getApplicationContext(),"로그아웃",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mainAct.this,loginAct.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getAppKeyHash(){
        try{
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures){

                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String (Base64.encode(md.digest(),0));
                Log.e("Hash key", something);

            }
        }catch (Exception e){
            Log.e("name not found",e.toString());
        }
        // 6:14
    }

    public String weatherurl(){
        String serviceKey ="%2F3mLJj4iwDg2oJCwR9ybHw1VUyOn5RoS849b2NBoODNMPVe7gGnNNCkgw3p%2FWrAsR4h1MHQab0eWs2YjozcVaQ%3D%3D";
        String numOfRows = "10"; //100
        String pageNo = "1";

        SimpleDateFormat real_date = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat real_time = new SimpleDateFormat("hh");

        Date time = new Date();

        String base_date = real_date.format(time);
        String test_time = real_time.format(time);
        String base_time ="";

        String weathertime = test_time+"30";

        int timenow = Integer.parseInt(test_time);
        if(timenow>=2 && timenow<5){
            base_time = "0200";
        }
        else if(timenow>=5 && timenow<8){
            base_time = "0500";
        }
        else if(timenow>=8 && timenow<11){
            base_time = "0800";
        }
        else if(timenow>=11 && timenow<14){
            base_time = "1100";
        }
        else if(timenow>=14 && timenow<17){
            base_time = "1400";
        }
        else if(timenow>=17 && timenow<20){
            base_time = "1700";
        }
        else if(timenow>=20 && timenow<23){
            base_time = "2000";
        }
        else if((timenow>=23 && timenow<=24) || (timenow>=0 && timenow<2)){
            base_time = "2300";
        }

        Log.i("현재시간",""+timenow);

        // 2 5 8 11 14 17 20 23

        String nx = Integer.toString(wedo); //37
        String ny = Integer.toString(gangdo); //127

        String weatherurl = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?serviceKey="+serviceKey+"&pageNo="+pageNo+"&numOfRows="+numOfRows+"&dataType=XML"+"&base_date="+base_date
                +"&base_time="+base_time+"&nx="+nx+"&ny="+ny;

        String weatherurl2 = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtFcst?serviceKey="+serviceKey+"&pageNo=1&numOfRows=100&dataType=XML&base_date="+
                base_date+"&base_time="+weathertime+"&nx="+nx+"&ny="+ny;

        return weatherurl2;
    }

    public String dusturl(){
        String serviceKey ="%2F3mLJj4iwDg2oJCwR9ybHw1VUyOn5RoS849b2NBoODNMPVe7gGnNNCkgw3p%2FWrAsR4h1MHQab0eWs2YjozcVaQ%3D%3D";

        String dusturl = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?serviceKey="+serviceKey+"&returnType=xml&numOfRows=10&pageNo=1&stationName="+mainAct.gugun+"&dataTerm=DAILY&ver=1.0";

        return dusturl;
    }

    public String covidurl(){
        String serviceKey ="%2F3mLJj4iwDg2oJCwR9ybHw1VUyOn5RoS849b2NBoODNMPVe7gGnNNCkgw3p%2FWrAsR4h1MHQab0eWs2YjozcVaQ%3D%3D";
        // 20210608&endCreateDt=20210609

        SimpleDateFormat daytime = new SimpleDateFormat("yyyyMMdd");
        Date time = new Date();
        String today_date = daytime.format(time);

        GregorianCalendar yd = new GregorianCalendar();
        yd.add(Calendar.DATE, -1);
        String yesterday_date = daytime.format(yd.getTime());


        String covidurl="http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson?serviceKey="+serviceKey+"&pageNo=1&numOfRows=10&startCreateDt="+yesterday_date+"&endCreateDt="+today_date;
        return covidurl;
    }

}