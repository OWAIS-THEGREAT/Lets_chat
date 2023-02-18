package com.example.letschat

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.GoogleAuthUtil.getToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chattinglog.*
import message_adapter
import org.json.JSONObject

class chattinglog : AppCompatActivity() {

        private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox : TextView
    private lateinit var sendButton : Button
    private lateinit var Username : String
    private lateinit var Phone_Number : String
    private lateinit var messageAdapter: message_adapter
//    private lateinit var messageList : ArrayList<message>
    private lateinit var dbref : DatabaseReference


    var recieverroom : String ?= null
    var senderroom : String ?= null

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chattinglog)
        val PERMISSION_CODE = 100
        dbref = FirebaseDatabase.getInstance().getReference()
        val name = intent.getStringExtra("username")
        val recieverUid = intent.getStringExtra("uid")
        val Phone_Number = intent.getStringExtra("phoneNumber")

        senderroom = recieverUid + senderUid
        recieverroom = senderUid + recieverUid
        titlename.text = name.toString()
        chatRecyclerView = findViewById(R.id.text_Recycle)
        messageBox = findViewById(R.id.textmessage)
        sendButton = findViewById(R.id.sendBut)
        val dataList = ArrayList<message>()
//        dataList.asReversed()
        val adapter = message_adapter(this, dataList)
        chatRecyclerView = findViewById(R.id.text_Recycle)
//        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = adapter

        putdata()
        sendButton.setOnClickListener {
            val message = messageBox.text.toString()
            if(message.isNotEmpty()) {
                val messageobject = message(message, senderUid)

                dbref.child("chats").child(senderroom!!).child("message").push()
                    .setValue(messageobject).addOnSuccessListener {
                    dbref.child("chats").child(recieverroom!!).child("message").push()
                        .setValue(messageobject)
                }
                messageBox.setText("")
                getToken(message)
            }
        }

        phone.setOnClickListener {

            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE),PERMISSION_CODE)
            }


//            val database = FirebaseDatabase.getInstance().getReference("user")
//            database.child(recieverUid!!).get().addOnSuccessListener {
//                val Phone_Number = it.child("phoneNumber").toString()

////                Toast.makeText(this,"$Phone_Number",Toast.LENGTH_LONG).show()
//            }

            val dial = "tel:$Phone_Number"
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))




        }


    }




        private fun putdata() {
        val dataList = ArrayList<message>()
        dbref.child("chats").child(senderroom!!).child("message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dataList.clear()
                    for(postSnapshot in snapshot.children){

                        val mesaage = postSnapshot.getValue(message::class.java)
                        dataList.add(mesaage!!)
                        val index = dataList.size
                        chatRecyclerView.smoothScrollToPosition(index)
                    }

                    chatRecyclerView.adapter = message_adapter(this@chattinglog,dataList)
                }

                override fun onCancelled(error: DatabaseError) {

                    Log.d(TAG, "onCancelled: ")
                }

            })

    }


        private fun getToken(message : String){
        val name = intent.getStringExtra("username")
        val recieverUid = intent.getStringExtra("uid")
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val username = FirebaseDatabase.getInstance().getReference("user")
        username.child(uid!!).get().addOnSuccessListener {
             Username = it.child("username").value.toString()
        }
        val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("user").child(recieverUid!!)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val token : String = snapshot.child("token").value.toString()
//                    val name = snapshot.child("name").value.toString()

                    val to  = JSONObject()
                    val data = JSONObject()
                    data.put("hisId",senderUid)
                    data.put("title",Username)
                    data.put("message",message)
                    data.put("chatId",recieverroom)

                    to.put("to",token)
                    to.put("data",data)

                    sendNotification(to)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun sendNotification(to : JSONObject) {
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            AppConstants.NOTIFICATION_URL,
            to,
            Response.Listener { response: JSONObject ->
            },
            Response.ErrorListener {

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()
                map["Authorization"] = "key=" + AppConstants.SERVER_KEY
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(request)
    }




    interface AppConstants {

        companion object {
            const val CHANNEL_ID = "Message Channel"
            const val SERVER_KEY = "AAAAqpN8olk:APA91bH33YAhx_zQReSmD4c0Wl4TEK5PDdeyzLNHXiVkLpECllV7MYTBjHthZkqV-1s_OY-dlVIQC1Reb2CcWd4gYwWbJajGyE1Rjzs0ZCanfYYOkOXBY_PNr0V48p9ZngwtIrzRrjv0"
            const val NOTIFICATION_URL = "https://fcm.googleapis.com/fcm/send"
            const val PATH = "/ProfileImage/image"
            const val UID = "ZyvJrMphIbbZQth0E7n1KmvFLWu1"
            const val STORAGE_PERMISSION = 1000
            const val CONTACT_PERMISSION = 2000
        }
    }
}