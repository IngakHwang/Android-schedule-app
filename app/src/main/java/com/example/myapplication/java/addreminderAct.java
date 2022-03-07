package com.example.myapplication.java;


import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.R;

import java.util.Calendar;


public class addreminderAct extends AppCompatActivity{

    static final int GET_IMAGE = 2;
    static final int GET_PERMISSION = 3;

    EditText addreminder_title,addreminder_memo,addreminder_location;
    TextView addreminder_date;
    Button addreminder_checkbtn, addreminder_cancelbtn;
    Switch addreminder_switch;
    String setDaytime;
    String setMytime;
    String addreminder_important;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addreminder);
        setTitle("일정추가");

        addreminder_title = (EditText) findViewById(R.id.addreminder_title);
        addreminder_memo = (EditText) findViewById(R.id.addreminder_memo);
        addreminder_location = (EditText) findViewById(R.id.addreminder_location);
        addreminder_date = (TextView) findViewById(R.id.addreminder_date);

        addreminder_checkbtn = (Button) findViewById(R.id.addreminder_checkbtn);
        addreminder_cancelbtn = (Button) findViewById(R.id.addreminder_cancelbtn);

        addreminder_switch = (Switch) findViewById(R.id.addreminder_switch);
        addreminder_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(),"중요한 일 등록",Toast.LENGTH_SHORT).show();
                    addreminder_important="중요";
                } else{
                    Toast.makeText(getApplicationContext(),"중요한 일 해제",Toast.LENGTH_SHORT).show();
                    addreminder_important="";
                }
            }
        });


        addreminder_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(addreminderAct.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String myHour = Integer.toString(hourOfDay);
                        String myMinute = Integer.toString(minute);
                        setMytime = (myHour + ":" + myMinute);
                        addreminder_date.setText(setDaytime +" | " + setMytime);
                    }
                },mHour,mMinute,true);
                timePickerDialog.show();

                DatePickerDialog datePickerDialog = new DatePickerDialog(addreminderAct.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String mmmonth = Integer.toString(month);
                        String myYear = Integer.toString(year);
                        String myMonth = Integer.toString(month+1);
                        String myDay = Integer.toString(dayOfMonth);
                        String myTime = (myYear + "/" + myMonth + "/" + myDay);
                        setDaytime = myTime;

                        // addreminder_date.setText(year + "/" + (month+1) + "/"+dayOfMonth);
                    }
                },mYear,mMonth,mDay);
                datePickerDialog.show();

                }
        });


        addreminder_checkbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent();
                intent.putExtra("title",addreminder_title.getText().toString());
                intent.putExtra("memo",addreminder_memo.getText().toString());
                intent.putExtra("location",addreminder_location.getText().toString());
                intent.putExtra("date",setDaytime);
                intent.putExtra("time",setMytime);
                intent.putExtra("important",addreminder_important);

                setResult(RESULT_OK,intent);

                finish();

            }
        });

        addreminder_cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }




}