package com.example.letschat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_splash_screen.*

class Splash_Screen : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val bottomAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom)
        splashtext.startAnimation(bottomAnimation)
        auth = FirebaseAuth.getInstance()
        Handler().postDelayed({

//            if(auth!!.currentUser == null){
                startActivity(Intent(this,MainActivity::class.java))
                finish()
//            }

        },2000)

    }
}