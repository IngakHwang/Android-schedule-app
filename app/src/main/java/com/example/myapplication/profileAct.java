package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class profileAct extends AppCompatActivity {

    TextView profile_ID, profile_mail, profile_text;
    ImageView profile_image;

    Uri selectedimageuri;
    String img_path;

    static final int GET_IMAGE = 2;
    static final int GET_PERMISSION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("내 프로필");

        String profileInfo, profileID = null, profileMail = null;
        SharedPreferences autologinshard = getSharedPreferences("member",MODE_PRIVATE);
        profileInfo = autologinshard.getString(mainAct.ID,null);


        try {
            JSONObject jsonObject = new JSONObject(profileInfo);
            profileID = jsonObject.optString("ID",null);
            profileMail = jsonObject.optString("Email",null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("프로필 아이디",profileID);
        Log.i("프로필 이메일",profileMail);


        profile_ID =(TextView) findViewById(R.id.profile_ID);
        profile_mail = (TextView) findViewById(R.id.profile_mail);
        profile_text = (TextView) findViewById(R.id.profile_text);
        profile_image = (ImageView) findViewById(R.id.profile_image);

        profile_ID.setText(profileID);
        profile_mail.setText(profileMail);
        profile_text.setText(profileID+ " 님 반갑습니다.");

        SharedPreferences profileimgshared = getSharedPreferences(mainAct.ID+" profile", MODE_PRIVATE);
        String imgpath = profileimgshared.getString(mainAct.ID,"");
        if(!imgpath.equals("")){
            Glide.with(this).load(imgpath).into(profile_image);
        }
        else if(imgpath.equals("")){
            profile_image.setImageResource(R.drawable.ic_baseline_person_24);
        }

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    // GRANTED = 권한 부여, DENIED = 권한부여X (권한부여가 안되어 있으면 하기 if문 실행해서 권한요청)
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions,GET_PERMISSION);
                    }
                    //
                    else{
                        // 권한부여가 완료되었다면
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent,GET_IMAGE);
                        // pickImageFromGallery();
                    }
                }
                else{
                    // 빌드 버전이 23미만
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent,GET_IMAGE);
                    //pickImageFromGallery();
                }
            }
        });

        profile_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder msgBuilder = new AlertDialog.Builder(profileAct.this)
                        .setTitle("사진 제거")
                        .setMessage("프로필 사진을 제거하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                profile_image.setImageResource(R.drawable.ic_baseline_person_24);

                                SharedPreferences profileShared = getSharedPreferences(mainAct.ID+" profile", MODE_PRIVATE);
                                SharedPreferences.Editor editor = profileShared.edit();
                                editor.putString(mainAct.ID,"");
                                editor.apply();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog msgDig = msgBuilder.create();
                msgDig.show();
                return true;
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case GET_PERMISSION:{
                // 권한부여되어있음
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent,GET_IMAGE);
                }
                // 권한부여 안되었으면
                else{
                    Toast.makeText(profileAct.this,"권한 설정해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode ==GET_IMAGE) {
            selectedimageuri = data.getData();

            //profile_image.setImageURI(selectedimageuri);

            img_path = getRealPathFromURI(selectedimageuri);

            Glide.with(this).load(img_path).into(profile_image);

            Log.i("정보태그",img_path);

            SharedPreferences profileShared = getSharedPreferences(mainAct.ID+" profile", MODE_PRIVATE);
            SharedPreferences.Editor editor = profileShared.edit();
            editor.putString(mainAct.ID,img_path);
            editor.apply();

        }
    }

    public String getRealPathFromURI(Uri uri){
        String[] proj = { MediaStore.Images.Media.DATA };

        CursorLoader loader = new CursorLoader(this,uri,proj,null,null,null);
        Cursor cursor = loader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;

    }

}