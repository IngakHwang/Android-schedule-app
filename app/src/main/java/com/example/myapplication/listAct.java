package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class listAct extends AppCompatActivity {

    private ArrayList<listdata> data;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Button list_addreminder;
    static final int GET_INTENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setTitle("나의 목록");

       /* recyclerView = (RecyclerView) findViewById(R.id.list_recView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        data = new ArrayList<>();*/
        list_addreminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(listAct.this,addreminderAct.class);

                startActivityForResult(intent,GET_INTENT);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GET_INTENT && resultCode == RESULT_OK && data != null){
            String title = data.getStringExtra("title");
            String memo = data.getStringExtra("memo");
            String url = data.getStringExtra("url");
            Uri image = data.getData();

            /*listdata adddata = new listdata(image,title,memo,url);
            data.add(adddata);
            customAdapter.notifyDataSetChanged();*/

        }
    }
}