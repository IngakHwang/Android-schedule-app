package com.example.myapplication

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val LOG = "Kotlin - Main"

    companion object{
        var ID = ""
    }

    var dataList = mutableListOf<MainData>()
    var adapter = MainAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(LOG, "Main")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "메인화면"

        if (checkLocationServiceStatus()) checkRunTimePermission() else showDialogForLocationServiceSetting()

        val userID = intent.getStringExtra("inputID")

        Log.d(LOG, "로그인된 아이디 : ${userID}")

        ID = userID!!

        loadData()

        adapter.mainData = dataList
        binding.mainRecView.adapter = adapter
        binding.mainRecView.layoutManager = LinearLayoutManager(this)
        binding.mainRecView.setHasFixedSize(true)

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.START or ItemTouchHelper.END
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return adapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeItem(viewHolder.adapterPosition)
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG)
                    viewHolder?.itemView?.setBackgroundColor(
                        Color.LTGRAY
                    )
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.setBackgroundColor(Color.WHITE)
            }
        }).attachToRecyclerView(binding.mainRecView)

        binding.mainAddlist.setOnClickListener {
            val intentAddList = Intent(this, AddReminderActivity::class.java)
            startActivityForResult(intentAddList, 1)
        }

        binding.mainTodaybtn.setOnClickListener {
            startActivity(Intent(this,TodayActivity::class.java))
        }

        binding.mainImportantbtn.setOnClickListener {
            startActivity(Intent(this,ImportantActivity::class.java))
        }

        binding.mainTimerbtn.setOnClickListener {
            Toast.makeText(this, "TimerAcitivity 준비 중", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val title = data.getStringExtra("title")
            val memo = data.getStringExtra("memo")
            val location = data.getStringExtra("location")
            val date = data.getStringExtra("date")
            val time = data.getStringExtra("time")
            val important = data.getStringExtra("important")

            Log.d(LOG, "$title | $memo | $location | $date | $time | $important")

            dataList.add(MainData(title, memo, location, date, time, important))

            adapter.notifyDataSetChanged()

            Log.d(LOG, "add 성공")
        }
    }

    override fun onPause() {
        super.onPause()

        saveData()
        Log.d(LOG, "저장 성공")
    }

    fun loadData() {
        val loadJson = getSharedPreferences("$ID reminder", MODE_PRIVATE).getString(ID, null)

        when {
            loadJson == null -> {
                Log.d(LOG, "Shared 아무것도 없음")
            }
            else -> {
                dataList =
                    Gson().fromJson(loadJson, object : TypeToken<MutableList<MainData>>() {}.type)
                Log.d(LOG, "Shared Load")
            }
        }
    }

    fun saveData() {
        val gson = Gson().toJson(dataList)
        getSharedPreferences(ID + " reminder", MODE_PRIVATE).edit().run {
            putString(ID, gson)
            apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mymenu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item1 -> {
                Toast.makeText(this, "준비중", Toast.LENGTH_SHORT).show()
                return true
            }

            R.id.menu_item2 -> {
                getSharedPreferences("AutoLogin", MODE_PRIVATE).edit().run {
                    clear()
                    apply()
                }

                Toast.makeText(this, "로그아웃", Toast.LENGTH_SHORT).show()
                val item2Intent = Intent(this, LoginActivity::class.java)
                startActivity(item2Intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun checkLocationServiceStatus(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun checkRunTimePermission() {
        val hasFineLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    REQUIRED_PERMISSIONS[0]
                )
            ) {
                Toast.makeText(this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 100)
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 100)
            }
        }
    }

    fun showDialogForLocationServiceSetting() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.")
        builder.setCancelable(true)
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialogInterface, i ->
            val callGPSSettingIntent =
                Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, 2001)
        })
        builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.cancel()
        })
        builder.create().show()
    }
}