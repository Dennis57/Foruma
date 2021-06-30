package com.example.foruma.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foruma.databinding.ActivityNewMessageBinding
import com.example.foruma.model.NewMessage
import com.example.foruma.adapter.NewMessageAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class NewMessageActivity : AppCompatActivity() {

  private lateinit var binding: ActivityNewMessageBinding

  private lateinit var adapter: NewMessageAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityNewMessageBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.ibBack.setOnClickListener {
      this@NewMessageActivity.onBackPressed()
    }

    binding.rvNewMessage.layoutManager = LinearLayoutManager(this)

    adapter = NewMessageAdapter {
      val intent = Intent(this@NewMessageActivity, ChatLogActivity::class.java)
      intent.putExtra("user", it)
      startActivity(intent)

      finish()
    }

    binding.rvNewMessage.adapter = adapter

    fetchUser()
  }

  private fun fetchUser() {
    val ref = FirebaseDatabase.getInstance().getReference("/users")
    Log.d("NewMessage", ref.toString())
    val newMessages: MutableList<NewMessage> = mutableListOf()
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        snapshot.children.forEach { dataSnapshot ->
          val uid = dataSnapshot.child("uid").value.toString()
          val username = dataSnapshot.child("username").value.toString()
          val profileImageUrl = dataSnapshot.child("profileImageUrl").value.toString()
          if (uid != Firebase.auth.uid) {
            newMessages.add(NewMessage(uid, username, profileImageUrl))
          }
        }
        adapter.submitList(newMessages)
      }

      override fun onCancelled(error: DatabaseError) {

      }
    })
  }
}