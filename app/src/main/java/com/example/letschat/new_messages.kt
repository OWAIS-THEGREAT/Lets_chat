package com.example.letschat

import android.content.Intent
import android.os.Bundle
import android.widget.Adapter
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Carousel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_new_messages.*
import kotlinx.android.synthetic.main.activity_new_messages.logout
import kotlinx.android.synthetic.main.activity_user.*

class new_messages : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var userarraylist : ArrayList<Select_User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_messages)
        auth = FirebaseAuth.getInstance()

        verifyUserLoggedIn()
        userRecyclerView = findViewById(R.id.recycle)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        userarraylist = arrayListOf<Select_User>()

        val adapter = selectuser_adapter(userarraylist)
        userRecyclerView.adapter = adapter
        getuserdata()
        val googlesignin = GoogleApiClient.Builder(applicationContext).addApi(Auth.GOOGLE_SIGN_IN_API).build()
        logout.setOnClickListener {
            Firebase.auth.signOut()
            if(googlesignin.isConnected){

            }
            //auth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }

        })

    }

    fun searchList(text :String){
        val searchList = ArrayList<Select_User>()
        for(dataclass in userarraylist){
            if(dataclass.username?.lowercase()?.contains(text.lowercase())== true){
                searchList.add(dataclass)
            }
        }
        val adapter = selectuser_adapter(userarraylist)
        userRecyclerView.adapter = adapter

        adapter.searchdatalist(searchList)

    }

    private fun getuserdata() {
        dbref = FirebaseDatabase.getInstance().getReference("user")
        dbref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists())
                    userarraylist.clear()
                    for(userSnapshot in snapshot.children){
                        val user = userSnapshot.getValue(Select_User::class.java)
                        if(auth.currentUser?.uid != user?.uid) {
                            userarraylist.add(user!!)
                        }
                    }
                userRecyclerView.adapter = selectuser_adapter(userarraylist)
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
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