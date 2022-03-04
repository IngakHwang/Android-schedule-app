package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myapplication.databinding.ActivityAddreminderBinding

import java.util.*


class AddReminderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddreminderBinding

    val LOG = "Kotlin - AddReminder"

    var addImportant: String = ""
    var setDayTime: String = ""
    var setMyTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(LOG, "AddReminder")

        binding = ActivityAddreminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "일정추가"

        binding.addreminderSwitch.setOnCheckedChangeListener{buttonView, isChecked ->
            addImportant =
            when {
                isChecked -> {
                    Toast.makeText(this,"중요한 일 등록",Toast.LENGTH_SHORT).show()
                    "중요"
                }
                else -> {
                    Toast.makeText(this,"중요한 일 해제",Toast.LENGTH_SHORT).show()
                    ""
                }
            }
        }

        binding.addreminderDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            TimePickerDialog(this, {
                    view, hourOfDay, mins ->

                setMyTime = ("${hourOfDay} : ${mins}")
                binding.addreminderDate.setText("${setDayTime} | ${setMyTime}")
            },hour,minute,true).show()

            DatePickerDialog(this,{
                view, years, months, dayOfMonth ->
                setDayTime = ("${years} / ${months+1} / ${dayOfMonth}")
            },year,month,day).show()
        }

        binding.addreminderCheckbtn.setOnClickListener {
            Intent().run {
                putExtra("title",binding.addreminderTitle.text.toString())
                putExtra("memo",binding.addreminderMemo.text.toString())
                putExtra("location",binding.addreminderLocation.text.toString())
                putExtra("date",setDayTime)
                putExtra("time",setMyTime)
                putExtra("important",addImportant)

                setResult(RESULT_OK,this)

                finish()
            }
        }

        binding.addreminderCancelbtn.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

    }
}