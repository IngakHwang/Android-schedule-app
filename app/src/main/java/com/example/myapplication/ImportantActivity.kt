package com.example.myapplication

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
import com.example.myapplication.databinding.ActivityImportantBinding
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import java.lang.Exception

class ImportantActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImportantBinding
    private val mainHander = MainHandler()

    val LOG = "Kotlin - ImportantActivity"

    var importantList = mutableListOf<MainData>()
    var notImportantList = mutableListOf<MainData>()
    var adapter = SubAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImportantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "중요"

        val loadData = getSharedPreferences("${MainActivity.ID} reminder", MODE_PRIVATE).getString(MainActivity.ID,null)

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

                if(important.equals("중요")){
                    importantList.add(MainData(title,memo,location, date, time, important))
                } else {
                    notImportantList.add(MainData(title, memo, location, date, time, important))
                }
                i++
            }
        }catch (e: JSONException){
            e.printStackTrace()
        }
        when{
            importantList.size <= 0 -> {
                binding.importantNoitem.text = "중요한 일이 없습니다."
            }
            importantList.size > 1 -> {
                binding.importantNoitem.text = ""
            }
        }

        adapter.listData = importantList
        binding.importantRecView.adapter = adapter
        binding.importantRecView.layoutManager = LinearLayoutManager(this)
        binding.importantRecView.setHasFixedSize(true)

        BackgroundThread().start()
        MainHandler()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action ->{
                Toast.makeText(this,"저장 중", Toast.LENGTH_SHORT).show()

                if(importantList.size>=1){
                    var i =0
                    while(i < importantList.size){
                        notImportantList.add(importantList.get(i))
                        i++
                    }
                }
                val gson = Gson().toJson(notImportantList)
                getSharedPreferences("${MainActivity.ID} reminder", MODE_PRIVATE).edit().putString(MainActivity.ID, gson).apply()

                startActivity(Intent(this,SaveActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class MainHandler : Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val img = msg.data.getInt("img")
            val uri = msg.data.getString("uri")
            binding.importantImg.setImageResource(img)
            binding.importantImg.setOnClickListener {
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