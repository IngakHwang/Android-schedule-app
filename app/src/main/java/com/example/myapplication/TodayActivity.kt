package com.example.myapplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityTodayBinding
import com.example.myapplication.java.AlarmRecevier
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TodayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodayBinding
    private val mainHander = MainHandler()
    val LOG = "Kotlin - TodayActivity"

    var todayList = mutableListOf<MainData>()
    var notTodayList = mutableListOf<MainData>()
    var adapter = SubAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "오늘 할 일"

        val sdfYear = SimpleDateFormat("yyyy",Locale.getDefault())
        val sdfMonth = SimpleDateFormat("M",Locale.getDefault())
        val sdfDay = SimpleDateFormat("d",Locale.getDefault())
        val currentTime = Calendar.getInstance().time

        val todayYear = sdfYear.format(currentTime)
        val todayMonth = sdfMonth.format(currentTime)
        val todayDay = sdfDay.format(currentTime)

        Log.d(LOG, "오늘 날짜 - ${todayYear}/${todayMonth}/${todayDay}")
        Log.d(LOG, MainActivity.ID)

        val loadData = getSharedPreferences(MainActivity.ID+" reminder", MODE_PRIVATE).getString(MainActivity.ID, null)

        try{
            val jsonArray = JSONArray(loadData)

            var i = 0
            Log.d(LOG,"JSON 길이 - ${jsonArray.length()}")
            while(i < jsonArray.length()){

                val jsonObject = jsonArray.getJSONObject(i)
                val title = jsonObject.optString("title","")
                val memo = jsonObject.optString("memo", "")
                val location = jsonObject.optString("location", "")
                val date = jsonObject.optString("date", "")
                val time = jsonObject.optString("time", "")
                val important = jsonObject.optString("important", "")

                if(date.equals("$todayYear / $todayMonth / $todayDay") ||
                        date.equals("${todayYear}/${todayMonth}/${todayDay}")){
                    todayList.add(MainData(title,memo,location, date, time, important))

                    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    val requestID : Int = System.currentTimeMillis().toInt()

                    val receiverIntent = Intent(this, AlarmRecevierKT::class.java)
                    receiverIntent.putExtra("Title",title)
                    receiverIntent.putExtra("Memo",memo)

                    val pendingIntent = PendingIntent.getBroadcast(this,requestID,receiverIntent,0)

                    val dateFormat = SimpleDateFormat("yyyy / M / d HH : mm",Locale.getDefault())
                    val from = "$date $time"
                    Log.d(LOG,"시간 - $from")

                    var dateTime = Date()
                    try{
                        dateTime = dateFormat.parse(from)
                    }catch (e: ParseException){
                        e.printStackTrace()
                        Log.d(LOG, "로그 - 실패")
                    }

                    val calendar = Calendar.getInstance()
                    calendar.time = dateTime

                    Log.d(LOG,"$dateTime")

                    alarmManager.set(AlarmManager.RTC, calendar.timeInMillis,pendingIntent)

                } else {
                    notTodayList.add(MainData(title, memo, location, date, time, important))
                }

                i++
            }

        }catch (e: JSONException){
            e.printStackTrace()
        }

        if(todayList.size <= 0){
            binding.todayNoitem.text = "오늘 할 일이 없습니다."
        } else {
            binding.todayNoitem.text = ""
        }

        adapter.listData = todayList
        binding.todayRecView.adapter = adapter
        binding.todayRecView.layoutManager = LinearLayoutManager(this)
        binding.todayRecView.setHasFixedSize(true)

        BackgroundThread().start()

        MainHandler()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action -> {
                Toast.makeText(this,"저장 중",Toast.LENGTH_SHORT).show()

                if(todayList.size>=1) {
                    var i = 0
                    while (i < todayList.size) {
                        notTodayList.add(todayList.get(i))
                        i++
                    }
                }
                val shard = getSharedPreferences("${MainActivity.ID} reminder", MODE_PRIVATE).edit()
                val gson = Gson().toJson(notTodayList)
                shard.putString(MainActivity.ID, gson)
                shard.apply()

                startActivity(Intent(this, SaveActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class MainHandler : Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val img = msg.data.getInt("img")
            val uri = msg.data.getString("uri")
            binding.todayImg.setImageResource(img)
            binding.todayImg.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(uri)))
            }
        }
    }

    private inner class BackgroundThread : Thread(){
        var value = 0
        var img = 0
        var uri = ""
        override fun run() {
            while(true){
                value++
                when(value){
                    1->{
                        img = R.drawable.google2
                        uri = "https://www.google.com"
                    }
                    2->{
                        img = R.drawable.naver2
                        uri = "https://m.naver.com"
                    }
                    3->{
                        img = R.drawable.kakao2
                        uri = "https://m.daum.net"
                        value=0
                    }
                }
                val bundle = Bundle()
                bundle.putInt("img",img)
                bundle.putString("uri", uri)

                val message = mainHander.obtainMessage()
                message.data=bundle

                mainHander.sendMessage(message)

                try{
                    Thread.sleep(4000)
                }catch (e: Exception){}
            }
        }
    }
}

