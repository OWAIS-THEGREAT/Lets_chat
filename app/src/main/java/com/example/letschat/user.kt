package com.example.letschat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_user.*

class user : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        auth = FirebaseAuth.getInstance()

        verifyUserLoggedIn()
//        val googlesignin = GoogleApiClient.Builder(applicationContext).addApi(Auth.GOOGLE_SIGN_IN_API).build()
//        logout.setOnClickListener {
//            Firebase.auth.signOut()
////            if(googlesignin.isConnected){
////
////            }
//            //auth.signOut()
//            startActivity(Intent(this,MainActivity::class.java))
//            finish()
//        }
        imageView3.setOnClickListener {
            val intent = Intent(this,new_messages::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun verifyUserLoggedIn() {
        val uid = auth.uid
        if(uid==null){
            val intent = Intent(this,register::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}