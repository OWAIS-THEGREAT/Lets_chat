package com.example.letschat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forget_password.*

class forget_password : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        login_forget.setOnClickListener {
            val email_fog = email_forget.text.toString().trim{it <=' '}
            if(email_fog.isEmpty()){
                Toast.makeText(this,"enter the email",Toast.LENGTH_LONG).show()
            }
            else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email_fog).addOnCompleteListener {
                    task->
                    if(task.isSuccessful){
                        Toast.makeText(this,"email send success",Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }
        }
    }
}