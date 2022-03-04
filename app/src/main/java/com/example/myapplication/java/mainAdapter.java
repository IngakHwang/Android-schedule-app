package com.example.myapplication.java;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Calendar;

public class mainAdapter extends RecyclerView.Adapter<mainAdapter.ViewHolder> {

    String LOG = "mainAdapter";

    private ArrayList<mainListdata> listdata;
    Context context;

    public mainAdapter(ArrayList<mainListdata> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.additemcard,parent,false);
        ViewHolder holder = new ViewHolder(views);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.additemcard_title.setText(listdata.get(position).getAdditemcard_title());
        holder.additemcard_memo.setText(listdata.get(position).getAdditemcard_memo());
        holder.additemcard_location.setText(listdata.get(position).getAdditemcard_location());
        holder.additemcard_date.setText(listdata.get(position).getAdditemcard_date());
        holder.additemcard_time.setText(listdata.get(position).getAdditemcard_time());
        holder.additemcard_important.setText(listdata.get(position).getAdditemcard_important());

        Log.i(LOG,"바인드뷰시작");
        Log.i("제목",listdata.get(position).getAdditemcard_title());
        Log.i("메모",listdata.get(position).getAdditemcard_memo());
        Log.i("장소",listdata.get(position).getAdditemcard_location());

        holder.itemView.setTag(position);

        Log.i(LOG,"바인드뷰끝");


    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public boolean moveItem(int fromPosition, int toPosition){
        mainListdata movedata = listdata.get(fromPosition);
        listdata.remove(fromPosition);
        listdata.add(toPosition,movedata);
        notifyItemMoved(fromPosition,toPosition);
        return true;
    }

    public void removeItem(int position){

        listdata.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView additemcard_title,additemcard_memo,additemcard_location,additemcard_date,additemcard_time,additemcard_important;
        String setDaytime;
        String setMytime;
        String timedateset = "날짜/시간 입력";
        String important;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.additemcard_title = (TextView) itemView.findViewById(R.id.additemcard_title);
            this.additemcard_memo = (TextView) itemView.findViewById(R.id.additemcard_memo);
            this.additemcard_location = (TextView) itemView.findViewById(R.id.additemcard_location);
            this.additemcard_date = (TextView) itemView.findViewById(R.id.additemcard_date);
            this.additemcard_time = (TextView) itemView.findViewById(R.id.additemcard_time);
            this.additemcard_important = (TextView) itemView.findViewById(R.id.additemcard_important);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();

                    Log.i(LOG,"포지션번호"+pos);

                    if(pos != RecyclerView.NO_POSITION){
                        // 다이얼로그로 수정
                        Log.i(LOG,"아이템클릭");
                        final Dialog dialog = new Dialog(context);
                        //다이얼로그 뷰아이디 전달
                        dialog.setContentView(R.layout.editlistdialog);
                        Button editbtn = (Button) dialog.findViewById(R.id.editlistdialog_editbtn);
                        Button cancelbtn = (Button) dialog.findViewById(R.id.editlistdialog_cancelbtn);
                        EditText editlistdialog_title = (EditText) dialog.findViewById(R.id.editlistdialog_title);
                        EditText editlistdialog_location = (EditText) dialog.findViewById(R.id.editlistdialog_loaction);
                        TextView editlistdialog_date = (TextView) dialog.findViewById(R.id.editlistdialog_date);
                        EditText editlistdialog_memo = (EditText) dialog.findViewById(R.id.editlistdialog_memo);
                        Switch editlistdialog_switch = (Switch) dialog.findViewById(R.id.editlistdialog_switch);

                        if(additemcard_important.getText().toString().equals("중요")){
                            editlistdialog_switch.setChecked(true);
                            important="중요";
                        }
                        else {
                            editlistdialog_switch.setChecked(false);
                        }

                        //다이얼로그 스위치 클릭
                        editlistdialog_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked){
                                    Toast.makeText(context,"중요한 일 등록",Toast.LENGTH_SHORT).show();
                                    important="중요";
                                } else{
                                    Toast.makeText(context,"중요한 일 해제",Toast.LENGTH_SHORT).show();
                                    important="";
                                }
                            }
                        });
                        setDaytime = additemcard_date.getText().toString();
                        setMytime = additemcard_time.getText().toString();

                        // String datetime = (additemcard_date.getText().toString() +" | "+ additemcard_time.getText().toString());

                        //다이얼로그 내 글자와 아이템의 글자 매칭
                        editlistdialog_title.setText(additemcard_title.getText().toString());
                        editlistdialog_location.setText(additemcard_location.getText().toString());
                        editlistdialog_date.setText(setDaytime + " | "+ setMytime);
                        editlistdialog_memo.setText(additemcard_memo.getText().toString());



                        if(additemcard_date.getText().toString().equals("")){
                            editlistdialog_date.setText(timedateset);
                        }


                        editlistdialog_date.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Calendar c = Calendar.getInstance();
                                int mYear = c.get(Calendar.YEAR);
                                int mMonth = c.get(Calendar.MONTH);
                                int mDay = c.get(Calendar.DAY_OF_MONTH);
                                int mHour = c.get(Calendar.HOUR_OF_DAY);
                                int mMinute = c.get(Calendar.MINUTE);

                                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        String myHour = Integer.toString(hourOfDay);
                                        String myMinute = Integer.toString(minute);
                                        setMytime = (myHour + ":" + myMinute);
                                        editlistdialog_date.setText(setDaytime +" | " + setMytime);
                                    }
                                },mHour,mMinute,true);
                                timePickerDialog.show();

                                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        String myYear = Integer.toString(year);
                                        String myMonth = Integer.toString(month+1);
                                        String myDay = Integer.toString(dayOfMonth);
                                        String myTime = (myYear + "/" + myMonth + "/" + myDay);
                                        setDaytime = myTime;

                                    }
                                },mYear,mMonth,mDay);
                                datePickerDialog.show();
                            }
                        });

                        editbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String etitle = editlistdialog_title.getText().toString();
                                String elocation = editlistdialog_location.getText().toString();
                                String edate = editlistdialog_date.getText().toString();
                                String ememo = editlistdialog_memo.getText().toString();

                                additemcard_title.setText(etitle);
                                additemcard_location.setText(elocation);
                                //additemcard_date.setText(edate);
                                additemcard_memo.setText(ememo);
                                additemcard_important.setText(important);

                                if(edate.equals(timedateset)){
                                    additemcard_date.setText("");
                                    edate="";
                                }

                                Log.i(LOG,"파일수정");

                                mainListdata dict = new mainListdata(etitle,ememo,elocation,setDaytime,setMytime,important);

                                listdata.set(getAdapterPosition(),dict);

                                notifyItemChanged(pos);

                                dialog.dismiss();
                            }
                        });
                        cancelbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }


                }
            });

        }
    }
}
