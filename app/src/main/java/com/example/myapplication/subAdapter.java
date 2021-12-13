package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class subAdapter extends RecyclerView.Adapter<subAdapter.ViewHolder>{

    String LOG = "subAdapter";

    private ArrayList<mainListdata> listdata;
    Context context;

    public subAdapter(ArrayList<mainListdata> listdata, Context context) {
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

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView additemcard_title,additemcard_memo,additemcard_location,additemcard_date,additemcard_time,additemcard_important;

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
                    Log.i("클릭","클릭");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setMessage("일을 완료하셨나요?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listdata.remove(pos);
                            notifyItemRemoved(pos);
                            notifyDataSetChanged();

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
    }
}
