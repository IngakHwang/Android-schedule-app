package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityProfileBinding
import org.json.JSONException
import org.json.JSONObject



class ProfileActivity : AppCompatActivity() {
    lateinit var binding : ActivityProfileBinding

    val GET_PERMISSION = 3
    val GET_IMAGE = 2
    lateinit var selectedImageUri : Uri
    lateinit var imgPath : String

    val LOG = "Kotlin - Profile"

    @SuppressLint("SetTextI18n", "ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "내 프로필"

        var profileID = ""
        var profileMail = ""

        val profileInfo = getSharedPreferences("member", MODE_PRIVATE).getString(MainActivity.ID, null)

        try{
            val jsonObject = JSONObject(profileInfo)
            profileID = jsonObject.optString("ID", null)
            profileMail = jsonObject.optString("Email", null)
        }catch(e: JSONException){e.printStackTrace()}

        binding.profileID.text = profileID
        binding.profileMail.text = profileMail
        binding.profileText.text = "$profileID 님 반갑습니다."

        val imgAddress = getSharedPreferences("${MainActivity.ID} profile", MODE_PRIVATE).getString(MainActivity.ID,"")
        if(imgAddress.equals("")){
            binding.profileImage.setImageResource(R.drawable.ic_baseline_person_24)
        } else{
            Glide.with(this).load(imgAddress).into(binding.profileImage)
        }

        binding.profileImage.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission,GET_PERMISSION)
                }
                else{
                    startActivityForResult(Intent(Intent.ACTION_PICK).setType("image/*"),GET_IMAGE)
                }
            } else{
                startActivityForResult(Intent(Intent.ACTION_PICK).setType("image/*"),GET_IMAGE)
            }
        }

        binding.profileImage.setOnLongClickListener {
            AlertDialog.Builder(this).setTitle("사진 제거").setMessage("프로필 사진을 제거하시겠습니까?").setPositiveButton("YES") { dialog, which ->
                binding.profileImage.setImageResource(R.drawable.ic_baseline_person_24)
                getSharedPreferences("${MainActivity.ID} profile", MODE_PRIVATE).edit()
                    .putString(MainActivity.ID, "").apply()
            }.setNegativeButton("NO"){diglog, which -> }.create().show()

            return@setOnLongClickListener true
        }



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            GET_PERMISSION -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startActivityForResult(Intent(Intent.ACTION_PICK).setType("image/*"),GET_IMAGE)
                }
                else{
                    Toast.makeText(this,"권한 설정해주세요.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK && requestCode == GET_IMAGE){
            Log.d(LOG, "요청번호 - $requestCode")
            Log.d(LOG, "결과번호 - $resultCode")

            selectedImageUri = data?.data!!
            Log.d(LOG, "URI - $selectedImageUri")

            imgPath = getRealPathFromURI(selectedImageUri)!!
            Log.d(LOG, imgPath)

            Glide.with(this)
                .load(imgPath)
                .into(binding.profileImage)

            getSharedPreferences("${MainActivity.ID} profile", MODE_PRIVATE).edit().putString(MainActivity.ID, imgPath).apply()
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun getRealPathFromURI(uri : Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)    //-data

        val cursor = CursorLoader(this,uri,proj,null,null,null).loadInBackground()

        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()

        Log.d(LOG, "$columnIndex")

        val result = columnIndex?.let { cursor.getString(it) }
        cursor?.close()

        return result
    }
}