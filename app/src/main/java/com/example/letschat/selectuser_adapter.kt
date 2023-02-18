package com.example.letschat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class selectuser_adapter(private var userlist : ArrayList<Select_User>) : RecyclerView.Adapter<selectuser_adapter.Myviewholder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myviewholder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view,parent,false)
        return Myviewholder(itemView)
    }

    override fun onBindViewHolder(holder: Myviewholder, position: Int) {
        val currentitem = userlist[position]
        holder.username.text = currentitem.username
        Picasso.get().load(currentitem.imageUrl).into(holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,chattinglog::class.java)

            intent.putExtra("username",currentitem.username)
            intent.putExtra("uid",currentitem.uid)
//            Toast.makeText(this,"number ${currentitem.Phonenumber}",Toast.LENGTH_LONG).s
            intent.putExtra("phoneNumber",currentitem.phoneNumber)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userlist.size
    }

    fun searchdatalist(searchList :ArrayList<Select_User>){
        userlist = searchList
        notifyDataSetChanged()
    }

    class Myviewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val username : TextView = itemView.findViewById(R.id.nam)
        val image :ImageView = itemView.findViewById(R.id.img)
    }
}