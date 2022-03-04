package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.auth.model.Prompt;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;
import com.kakao.sdk.user.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;




public class loginAct extends AppCompatActivity {

    EditText login_inputID;
    EditText login_inputPW;
    Button login_loginbtn, login_joinbtn;
    View login_kakaobtn;
    String ID,PW,checkID,checkPW;
    String AutoID,AutoPW;
    String kakaoname, kakaopw,kakaomail;

    String TAG = "카카오";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences loginshared = getSharedPreferences("member",MODE_PRIVATE);
        SharedPreferences autologinshard = getSharedPreferences("AutoLogin",MODE_PRIVATE);
        AutoID = autologinshard.getString("ID",null);
        AutoPW = autologinshard.getString("PW",null);
        if(AutoID != null && AutoPW != null){

            Intent intent = new Intent(loginAct.this,mainAct.class);
            intent.putExtra("inputID",AutoID);
            startActivity(intent);
            finish();
        }

        login_inputID =(EditText) findViewById(R.id.login_inputID);
        login_inputPW =(EditText) findViewById(R.id.login_inputPW);
        login_loginbtn =(Button) findViewById(R.id.login_loginbtn);
        login_joinbtn =(Button) findViewById(R.id.login_joinbtn);
        login_kakaobtn = findViewById(R.id.login_kakaobtn);

        login_kakaobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                UserApiClient.getInstance().loginWithKakaoAccount(loginAct.this,((oAuthToken, error) ->{
                    if(error != null){
                        Log.i(TAG,"로그인 실패");
                    } else if (oAuthToken != null) {
                        Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());

                        UserApiClient.getInstance().me((user, meError) -> {
                            if (meError != null) {
                                Log.e(TAG, "사용자 정보 요청 실패", meError);
                            } else {
                                Log.i(TAG, "사용자 정보 요청 성공" +
                                        "\n회원번호: "+user.getId() +
                                        "\n이메일: "+user.getKakaoAccount().getEmail() +
                                        "\n닉네임: "+user.getKakaoAccount().getProfile().getNickname());
                                kakaoname = "Kakao";
                                kakaopw = user.getKakaoAccount().getProfile().getNickname();
                                kakaomail = user.getKakaoAccount().getEmail();

                                joinmemberInfo member = new joinmemberInfo(kakaoname, kakaopw, kakaomail);
                                Gson gson = new Gson();
                                String changejson = gson.toJson(member);

                                SharedPreferences joinshared = getSharedPreferences("member",MODE_PRIVATE);
                                SharedPreferences.Editor editor = joinshared.edit();
                                editor.putString(kakaoname,changejson);
                                editor.commit();

                                SharedPreferences autologinshard = getSharedPreferences("AutoLogin",MODE_PRIVATE);
                                SharedPreferences.Editor editorAL = autologinshard.edit();
                                editorAL.putString("ID",kakaoname);
                                editorAL.putString("PW",kakaopw);
                                editorAL.commit();
                                Toast.makeText(getApplicationContext(),kakaoname+" 님 환영합니다.",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(loginAct.this,mainAct.class);
                                intent.putExtra("inputID",kakaoname);
                                startActivity(intent);

                                finish();
                            }
                            return null;
                        });
                    }

                    return null;
                }));

            }
        });

        login_loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ID = login_inputID.getText().toString().trim();
                PW = login_inputPW.getText().toString();

                SharedPreferences loginshared = getSharedPreferences("member",MODE_PRIVATE);
                String json = loginshared.getString(ID,"Fail");
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String jsonid = jsonObject.getString("ID");
                    String jsonpw = jsonObject.getString("PW");

                    checkID = jsonid;
                    checkPW = jsonpw;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(ID.isEmpty()){
                    Toast.makeText(getApplicationContext(),"아이디를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    login_inputID.requestFocus();
                    return;
                }
                else if(PW.isEmpty()){
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    login_inputPW.requestFocus();
                    return;
                }

                else if(!ID.equals(checkID)){
                    Toast.makeText(getApplicationContext(),"아이디를 확인해주세요.",Toast.LENGTH_SHORT).show();
                    login_inputID.requestFocus();
                    return;
                }
                else if(!PW.equals(checkPW)){
                    Toast.makeText(getApplicationContext(),"비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                    login_inputPW.requestFocus();
                    return;
                }

                else if(checkID.equals(ID) && checkPW.equals(PW)){
                    SharedPreferences autologinshard = getSharedPreferences("AutoLogin",MODE_PRIVATE);
                    SharedPreferences.Editor editorAL = autologinshard.edit();
                    editorAL.putString("ID",checkID);
                    editorAL.putString("PW",checkPW);
                    editorAL.commit();
                    Toast.makeText(getApplicationContext(),checkID+" 님 환영합니다.",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(loginAct.this,mainAct.class);
                    intent.putExtra("inputID",ID);
                    startActivity(intent);
                    finish();
                }
            }
        });

        if(savedInstanceState != null){
            ID = savedInstanceState.getString("ID");
            PW = savedInstanceState.getString("PW");
        }

        /*@Override
        protected void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString("ID", ID);
            outState.putString("PW", PW);
        }*/

        login_joinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginAct.this,joinAct.class);
                startActivity(intent);
            }
        });
    }

    private void updateKakaoLoginUI(){
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {

                Log.i(TAG,"id" + user.getId());
                Log.i(TAG,"email" + user.getKakaoAccount().getEmail());
                Log.i(TAG,"nickname" + user.getKakaoAccount().getProfile().getNickname());
                Log.i(TAG,"gender" + user.getKakaoAccount().getGender());
                Log.i(TAG,"age" + user.getKakaoAccount().getAgeRange());

                return null;
            }
        });


    }

}
