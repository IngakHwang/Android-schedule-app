package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class joinAct extends AppCompatActivity {

    final String LOG= "회원가입";

    EditText join_id, join_pw,join_pwcheck,join_mail;
    Button join_idcheckbtn, join_checkbtn, join_cancelbtn;
    boolean checkedID = false;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG,"onCreate");
        setContentView(R.layout.activity_join);
        setTitle("회원가입");

        join_id = findViewById(R.id.join_id);
        join_pw = findViewById(R.id.join_pw);
        join_pwcheck = findViewById(R.id.join_pwcheck);
        join_mail = findViewById(R.id.join_mail);

        join_idcheckbtn = findViewById(R.id.join_idcheckbtn);
        join_checkbtn = findViewById(R.id.join_checkbtn);
        join_cancelbtn = findViewById(R.id.join_cancelbtn);



        join_checkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doJoin();
            }
        });

        join_cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        join_idcheckbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String joinID = join_id.getText().toString().trim();
                SharedPreferences joinShared = getSharedPreferences("member", MODE_PRIVATE);
                String mSavedText = joinShared.getString(joinID,"true");

                if(mSavedText!="true"){
                    AlertDialog.Builder builder = new AlertDialog.Builder(joinAct.this);

                    dialog = builder.setMessage("중복된 아이디입니다.").setPositiveButton("확인",null).create();
                    dialog.show();

                    join_id.requestFocus();

                    checkedID = false;


                }
                else{
                    Toast.makeText(getApplicationContext(),"사용가능한 아이디입니다.",Toast.LENGTH_SHORT).show();

                    checkedID = true;

                }

            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG,"onRestart");

        SharedPreferences joinSPsave = getSharedPreferences("join_save",MODE_PRIVATE);
        String valueID = joinSPsave.getString("valueID","");
        String valuePW = joinSPsave.getString("valuePW","");
        String valuePWcheck = joinSPsave.getString("valuePWcheck","");
        String valuemail = joinSPsave.getString("valuemail","");

        join_id.setText(valueID);
        join_pw.setText(valuePW);
        join_pwcheck.setText(valuePWcheck);
        join_mail.setText(valuemail);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG,"onStop");
        SharedPreferences joinSPsave = getSharedPreferences("join_save",MODE_PRIVATE);
        SharedPreferences.Editor editor = joinSPsave.edit();
        String valueID = join_id.getText().toString();
        String valuePW = join_pw.getText().toString();
        String valuePWcheck = join_pwcheck.getText().toString();
        String valuemail = join_mail.getText().toString();

        editor.putString("valueID", valueID);
        editor.putString("valuePW", valuePW);
        editor.putString("valuePWcheck", valuePWcheck);
        editor.putString("valuemail", valuemail);
        editor.apply();
    }

    public void doJoin(){
        String joinID = join_id.getText().toString().trim();
        String joinPW = join_pw.getText().toString().trim();
        String joinPWcheck = join_pwcheck.getText().toString().trim();
        String joinMail = join_mail.getText().toString().trim();

        if(joinID.length() == 0){
            Toast.makeText(getApplicationContext(),"아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
            join_id.requestFocus();
            return;
        }
        if(checkedID == false){
            Toast.makeText(getApplicationContext(),"아이디 중복체크 해주세요.", Toast.LENGTH_SHORT).show();
            join_id.requestFocus();
            return;
        }

        if(joinPW.length() == 0){
            Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            join_pw.requestFocus();
            return;
        }

        if(joinPW.equals(joinPWcheck) == false){
            Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            join_pwcheck.requestFocus();
            return;
        }

        if(joinMail.length() == 0){
            Toast.makeText(getApplicationContext(),"이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            join_mail.requestFocus();
            return;
        }

        joinmemberInfo member = new joinmemberInfo(joinID,joinPW,joinMail);
        Gson gson = new Gson();
        String changejson = gson.toJson(member);

        SharedPreferences joinShared = getSharedPreferences("member", MODE_PRIVATE);
        SharedPreferences.Editor editor = joinShared.edit();
        editor.putString(joinID,changejson);
        editor.commit();

        Toast.makeText(getApplicationContext(),"회원가입되었습니다.",Toast.LENGTH_SHORT).show();

        finish();
    }


}