package com.example.myapplication

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.AdditemcardBinding
import com.example.myapplication.databinding.EditlistdialogBinding
import java.util.*

class MainAdapter(var context: Context): RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    var mainData = mutableListOf<MainData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdditemcardBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.additemcardTitle.setText(mainData.get(position).title)
        holder.binding.additemcardMemo.setText(mainData.get(position).memo)
        holder.binding.additemcardLocation.setText(mainData.get(position).location)
        holder.binding.additemcardDate.setText(mainData.get(position).date)
        holder.binding.additemcardTime.setText(mainData.get(position).time)
        holder.binding.additemcardImportant.setText(mainData.get(position).important)

        holder.itemView.setTag(position)
    }

    override fun getItemCount(): Int {
        return mainData.size
    }

    fun removeItem(position: Int){
        mainData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun moveItem(fromPosition: Int, toPosition: Int): Boolean{
        val movedata: MainData = mainData.get(fromPosition)
        mainData.removeAt(fromPosition)
        mainData.add(toPosition,movedata)

        notifyItemMoved(fromPosition,toPosition)
        return true
    }

    inner class ViewHolder(val binding: AdditemcardBinding): RecyclerView.ViewHolder(binding.root){
        var setDayTime: String = ""
        var setMyTime: String = ""
        var timeDateSet: String = "날짜/시간 입력"
        var importantThing: String = ""

        init{
                binding.root.setOnClickListener {
                    val pos = adapterPosition

                    if(pos != RecyclerView.NO_POSITION){
                        val dialog = Dialog(context)

                        val dialogBinding = EditlistdialogBinding.inflate(LayoutInflater.from(context))
                        dialog.setContentView(dialogBinding.root)

                        if(binding.additemcardImportant.text.toString().equals("중요")){
                            dialogBinding.editlistdialogSwitch.isChecked = true
                            importantThing="중요"
                        }else{
                            dialogBinding.editlistdialogSwitch.isChecked = false
                        }

                        dialogBinding.editlistdialogSwitch.setOnCheckedChangeListener { compoundButton, b ->
                            importantThing =
                                when{
                                    b ->{
                                        Toast.makeText(context,"중요한일 등록",Toast.LENGTH_SHORT).show()
                                        "중요"
                                    }else -> {
                                        Toast.makeText(context,"중요한일 해제",Toast.LENGTH_SHORT).show()
                                        ""
                                    }
                                }
                        }
                        setDayTime = binding.additemcardDate.text.toString()
                        setMyTime = binding.additemcardTime.text.toString()

                        dialogBinding.editlistdialogTitle.setText(binding.additemcardTitle.text.toString())
                        dialogBinding.editlistdialogLoaction.setText(binding.additemcardLocation.text.toString())
                        dialogBinding.editlistdialogDate.setText("${setDayTime} | ${setMyTime}")
                        dialogBinding.editlistdialogMemo.setText(binding.additemcardMemo.text.toString())

                        if(binding.additemcardDate.text.toString().equals("")) dialogBinding.editlistdialogDate.setText(timeDateSet)

                        dialogBinding.editlistdialogDate.setOnClickListener {
                            val c = Calendar.getInstance()
                            val mYear = c.get(Calendar.YEAR)
                            val mMonth = c.get(Calendar.MONTH)
                            val mDay = c.get(Calendar.DAY_OF_MONTH)
                            val mHour = c.get(Calendar.HOUR_OF_DAY)
                            val mMinute = c.get(Calendar.MINUTE)

                            TimePickerDialog(context, {
                                view, hourOfDay, minute ->
                                val myHour = Integer.toString(hourOfDay)
                                val myMinute = Integer.toString(minute)
                                setMyTime = (myHour + ":" + myMinute)
                                dialogBinding.editlistdialogDate.setText("${setDayTime} | ${setMyTime}")
                            }, mHour, mMinute, true).show()

                            DatePickerDialog(context, {
                                view, year, month, dayOfMonth ->
                                val myTime = "${Integer.toString(year)}/${Integer.toString(month+1)}/${Integer.toString(dayOfMonth)}"
                                setDayTime = myTime
                            },mYear,mMonth,mDay).show()
                        }

                        dialogBinding.editlistdialogEditbtn.setOnClickListener {
                            val editTitle = dialogBinding.editlistdialogTitle.text.toString()
                            val editLocation = dialogBinding.editlistdialogLoaction.text.toString()
                            var editDate = dialogBinding.editlistdialogDate.text.toString()
                            val editMemo = dialogBinding.editlistdialogMemo.text.toString()

                            binding.additemcardTitle.setText(editTitle)
                            binding.additemcardLocation.setText(editLocation)
                            binding.additemcardMemo.setText(editMemo)
                            binding.additemcardImportant.setText(importantThing)

                            if(editDate.equals(timeDateSet)){
                                binding.additemcardDate.setText("")
                                editDate=""
                            }

                            val editDataList = MainData(editTitle,editMemo,editLocation, setDayTime, setMyTime, importantThing)

                            mainData.set(adapterPosition,editDataList)

                            notifyItemChanged(pos)

                            dialog.dismiss()
                        }

                        dialogBinding.editlistdialogCancelbtn.setOnClickListener {
                            dialog.dismiss()
                        }

                        dialog.show()
                    }
                }
        }
    }
}
