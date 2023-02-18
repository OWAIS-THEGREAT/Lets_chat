package com.example.letschat

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var token : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth
        button.setOnClickListener {

           perform_register()
        }

        imageView2.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }


        textView3.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    var selectphotouri : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode == Activity.RESULT_OK && data!=null){
            selectphotouri = data.data
//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectphotouri)
//            val bitmapDrawable = BitmapDrawable(bitmap)
            select_pic_circle.setImageURI(selectphotouri)
            imageView2.alpha = 0f
        }
    }

    private fun perform_register() {
        val inputemail = username1.text.toString()
        val inputpassword = Password1.text.toString()


//            // check pass
        if (inputemail.isBlank() || inputpassword.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
        // If all credential are correct
        // We call createUserWithEmailAndPassword
        // using auth object and pass the
        // email and pass in it.
        auth.createUserWithEmailAndPassword(inputemail, inputpassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    auth.currentUser?.sendEmailVerification()
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {

                            return@OnCompleteListener
                        }

                        // Get new FCM registration token
                        token  = task.result
                    })
                        .addOnSuccessListener {

                            uploadImagetoStorage()
                            val intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        }



                    // Sign in success, update UI with the signed-in user's information
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun uploadImagetoStorage() {
        if(selectphotouri==null)return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("images/$filename")
        ref.putFile(selectphotouri!!).addOnSuccessListener {

            ref.downloadUrl.addOnSuccessListener {
                SaveUsertoDatabase(it.toString(),token)
            }

        }
    }

    private fun SaveUsertoDatabase(profileImageUrl : String,token: String) {


        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("user/$uid")
        val user = Users(uid,editTextTextPersonName.text.toString(),profileImageUrl,token,Phone_Number.text.toString())
        ref.setValue(user)
    }
}

class Users(val uid : String , val username : String,val imageUrl : String,val token : String,val phoneNumber : String )