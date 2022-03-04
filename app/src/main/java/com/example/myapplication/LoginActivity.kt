package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.myapplication.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject

// 자동 로그인 기능은 mainActivity 수정 후 구현 할 것
// 카카오 API 기능 삭제

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    val LOG = "Kotlin - Login"

    var id : String = ""
    var pw : String = ""
    var checkID : String = ""
    var checkPW : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG,"Login")
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val autoLoginShared = getSharedPreferences("AutoLogin", MODE_PRIVATE)
        val AutoID = autoLoginShared.getString("ID", null)
        val AutoPW = autoLoginShared.getString("PW", null)
        if(AutoID != null && AutoPW != null){
            val intent = Intent(this,MainActivity::class.java).putExtra("inputID",AutoID)
            startActivity(intent)
            finish()
        }


        binding.loginKakaobtn.setOnClickListener(View.OnClickListener {
            Toast.makeText(this,"카카오 로그인",Toast.LENGTH_SHORT).show()
        })


        binding.loginLoginbtn.setOnClickListener{
            id = binding.loginInputID.text.toString().trim()
            pw = binding.loginInputPW.text.toString()

            val loginShared = getSharedPreferences("member", MODE_PRIVATE)
            val json = loginShared.getString(id,"Fail")

            Log.d(LOG, "json 데이터 : ${json}")

            try{
                val jsonObject = JSONObject(json)
                checkID = jsonObject.getString("ID")
                checkPW = jsonObject.getString("PW")

                Log.d(LOG, "ID : ${checkID}")
                Log.d(LOG, "PW : ${checkPW}")
            } catch (e: JSONException){
                e.printStackTrace()
                Log.d(LOG, "try 실패")
            }

            when{
                id.isEmpty() -> {
                    Toast.makeText(applicationContext,"아이디를 입력해주세요.",Toast.LENGTH_SHORT).show()
                    binding.loginInputID.requestFocus()
                }

                pw.isEmpty() -> {
                    Toast.makeText(applicationContext,"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show()
                    binding.loginInputPW.requestFocus()
                }
                !id.equals(checkID) -> {
                    Toast.makeText(applicationContext,"아이디를 확인해주세요.",Toast.LENGTH_SHORT).show()
                    binding.loginInputID.requestFocus()
                }
                !pw.equals(checkPW) -> {
                    Toast.makeText(applicationContext,"비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show()
                    binding.loginInputPW.requestFocus()
                }
                else -> {
                    val autoLoginShared : SharedPreferences = getSharedPreferences("AutoLogin", MODE_PRIVATE)
                    val editer : SharedPreferences.Editor = autoLoginShared.edit()
                    editer.putString("ID",checkID)
                    editer.putString("PW",checkPW)
                    editer.commit()
                    Toast.makeText(applicationContext,"${checkID} 님 환영합니다.",Toast.LENGTH_SHORT).show()

                    val intent = Intent(this,MainActivity::class.java)
                    intent.putExtra("inputID",checkID)

                    startActivity(intent)
                    finish()
                }

            }
        }

        binding.loginJoinbtn.setOnClickListener{
            val intent = Intent(this,JoinActivity::class.java)
            startActivity(intent)
        }

        if(savedInstanceState != null){
            id = savedInstanceState.getString("ID")!!
            pw = savedInstanceState.getString("PW")!!

        }

    }
}