 package com.example.letschat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

 class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var client : GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.apply { systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        auth = Firebase.auth

        login.setOnClickListener {

            if(checking()){
                val user = email.text.toString()
                val password = password.text.toString()
                auth.signInWithEmailAndPassword(user,password).addOnCompleteListener(this){
                        task->
                    if(task.isSuccessful){

                        val verification = auth.currentUser?.isEmailVerified
                        if(verification == true) {
//                            Toast.makeText(this,"verification done",Toast.LENGTH_LONG).show()
                            val intent = Intent(this, new_messages::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    else{
                        Toast.makeText(this,"wrong detail", Toast.LENGTH_LONG).show()
                    }
                }
            }
            else{
                Toast.makeText(this,"enter the detail", Toast.LENGTH_LONG).show()
            }
        }

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        client = GoogleSignIn.getClient(this,options)

        val googlesignin = GoogleApiClient.Builder(applicationContext).addApi(Auth.GOOGLE_SIGN_IN_API).build()
        google.setOnClickListener {
            val intent1 = client.signInIntent
            startActivityForResult(intent1,10001)
        }

        newacc.setOnClickListener {
            val intent = Intent(this,register::class.java)
            startActivity(intent)
            finish()
        }

        forgetpass.setOnClickListener {
            startActivity(Intent(this,forget_password::class.java))
        }
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if(requestCode == 10001){

             val task = GoogleSignIn.getSignedInAccountFromIntent(data)
             val account = task.getResult(ApiException::class.java)
             val credential = GoogleAuthProvider.getCredential(account.idToken,null)
             FirebaseAuth.getInstance().signInWithCredential(credential)
                 .addOnCompleteListener(){ task->
                     if(task.isSuccessful){
                         var intent = Intent(this,user::class.java)
                         startActivity(intent)
                         finish()
                     }
                     else{
                         Toast.makeText(this,"unsuccessfull",Toast.LENGTH_LONG).show()
                     }
                 }
         }
     }

     override fun onStart() {
         super.onStart()
         val verification = auth.currentUser?.isEmailVerified
         // Check if user is signed in (non-null) and update UI accordingly.
         if(FirebaseAuth.getInstance().currentUser !=null && verification == true){
             var intent = Intent(this,new_messages::class.java)
             startActivity(intent)
             finish()
         }
     }

     private fun checking():Boolean{
         if(email.text.trim { it <=' ' }.isNotEmpty() && password.text.trim { it<=' ' }.isNotEmpty()){
             return true
         }
         return false
     }
 }

