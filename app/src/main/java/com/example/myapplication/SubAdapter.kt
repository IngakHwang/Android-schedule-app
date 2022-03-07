package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.AdditemcardBinding

class SubAdapter(var context: Context) : RecyclerView.Adapter<SubAdapter.ViewHolder>() {

    var listData = mutableListOf<MainData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdditemcardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return  ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.additemcardTitle.setText(listData.get(position).title)
        holder.binding.additemcardMemo.setText(listData.get(position).memo)
        holder.binding.additemcardLocation.setText(listData.get(position).location)
        holder.binding.additemcardDate.setText(listData.get(position).date)
        holder.binding.additemcardTime.setText(listData.get(position).time)
        holder.binding.additemcardImportant.setText(listData.get(position).important)

        holder.itemView.setTag(position)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder(val binding: AdditemcardBinding) : RecyclerView.ViewHolder(binding.root){

        init{
            binding.root.setOnClickListener {
                val pos = adapterPosition

                val builder = AlertDialog.Builder(context)

                builder.setMessage("일을 완료하셨나요?")
                builder.setPositiveButton("Yes",DialogInterface.OnClickListener { dialogInterface, i ->
                    listData.removeAt(pos)
                    notifyItemRemoved(pos)
                    notifyDataSetChanged()
                })

                builder.setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i ->

                })

                builder.create().show()
            }
        }
    }



}