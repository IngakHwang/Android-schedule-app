package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.databinding.ActivityJoinBinding
import com.google.gson.Gson


class JoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinBinding

    var checkedID : Boolean = false
    val LOG = "Kotlin - Join"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG,"Join")
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "회원가입"

        binding.joinCheckbtn.setOnClickListener{
            doJoin()
        }

        binding.joinCancelbtn.setOnClickListener {
            finish()
        }

        binding.joinIdcheckbtn.setOnClickListener {
            checkID()
        }
    }

    fun doJoin(){
        val joinId = binding.joinId.text.toString().trim()
        val joinPw = binding.joinPw.text.toString().trim()
        val joinPwCheck = binding.joinPwcheck.text.toString().trim()
        val joinMail = binding.joinMail.text.toString().trim()

        when{
            joinId.isEmpty() -> {
                Toast.makeText(applicationContext,"아이디를 입력해주세요.",Toast.LENGTH_SHORT).show()
                binding.joinId.requestFocus()
            }

            !checkedID -> {
                Toast.makeText(applicationContext,"아이디 중복체크를 해주세요.",Toast.LENGTH_SHORT).show()
                binding.joinId.requestFocus()
            }

            joinPw.isEmpty() -> {
                Toast.makeText(applicationContext,"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show()
                binding.joinPw.requestFocus()
            }

            joinPw != joinPwCheck -> {
                Toast.makeText(applicationContext,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show()
                binding.joinPw.requestFocus()
            }

            joinMail.isEmpty() -> {
                Toast.makeText(applicationContext,"이메일를 입력해주세요.",Toast.LENGTH_SHORT).show()
                binding.joinMail.requestFocus()
            }

            else -> {
                val member = Member(joinId, joinPw, joinMail)
                val gson = Gson()
                val changeJSON : String = gson.toJson(member)

                val joinShared = getSharedPreferences("member", MODE_PRIVATE)
                val editer = joinShared.edit()
                editer.putString(joinId, changeJSON)
                editer.commit()

                Toast.makeText(applicationContext,"회원가입되었습니다.",Toast.LENGTH_SHORT).show()

                finish()
            }


        }
    }

    fun checkID(){
        val joinID = binding.joinId.text.toString().trim()
        val joinShared = getSharedPreferences("member", MODE_PRIVATE)
        val savedText = joinShared.getString(joinID, "true")

        checkedID = if(savedText!="true"){
            val alertDialog = AlertDialog.Builder(this)

            alertDialog.setMessage("중복된 아이디입니다.").setPositiveButton("확인",null).create()
            alertDialog.show()

            binding.joinId.requestFocus()

            false
        } else {
            Toast.makeText(applicationContext,"사용가능한 아이디입니다.",Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(LOG,"onStop")
        val joinSaveData = getSharedPreferences("join_save", MODE_PRIVATE)
        val editer = joinSaveData.edit()

        editer.putString("saveId", binding.joinId.text.toString())
        editer.putString("savePw", binding.joinPw.text.toString())
        editer.putString("savePwCheck", binding.joinPwcheck.text.toString())
        editer.putString("saveEmail", binding.joinMail.text.toString())

        editer.apply()
        //commit apply 차이
        //apply 비동기
        //commit 동기

//        getSharedPreferences("join_save", MODE_PRIVATE).edit().run {
//            putString("saveId", join_id.text.toString())
//            putString("savePw", join_pw.text.toString())
//            putString("savePwCheck", join_pwcheck.text.toString())
//            putString("saveEmail", join_mail.text.toString())
//            apply()
//        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(LOG,"onRestart")
//        val joinSaveData = getSharedPreferences("join_save", MODE_PRIVATE)
//
//        join_id.setText(joinSaveData.getString("saveId",""))
//        join_pw.setText(joinSaveData.getString("savePw",""))
//        join_pwcheck.setText(joinSaveData.getString("savePwCheck",""))
//        join_mail.setText(joinSaveData.getString("saveEmail",""))
    }
}