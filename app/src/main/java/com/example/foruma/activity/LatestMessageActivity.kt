package com.example.foruma.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foruma.adapter.LatestMessageAdapter
import com.example.foruma.databinding.ActivityLatestMessageBinding
import com.example.foruma.model.ChatItem
import com.example.foruma.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class LatestMessageActivity : AppCompatActivity() {

  private lateinit var binding: ActivityLatestMessageBinding

  private lateinit var auth: FirebaseAuth

  private lateinit var adapter: LatestMessageAdapter

  private val latestMessagesMap = HashMap<String, ChatItem>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityLatestMessageBinding.inflate(layoutInflater)
    setContentView(binding.root)

    auth = Firebase.auth

    binding.rvLatestMessage.layoutManager = LinearLayoutManager(this)
    adapter = LatestMessageAdapter {
      val intent = Intent(this@LatestMessageActivity, ChatLogActivity::class.java)
      intent.putExtra("user", it)
      startActivity(intent)
    }
    binding.rvLatestMessage.adapter = adapter

    verifyUserIsLoggedIn()

    fetchCurrentUser()

    listenLatestMessages()

    binding.ibSignOut.setOnClickListener {
      auth.signOut()
      val intent = Intent(this, RegisterActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
      startActivity(intent)
    }

    binding.ibNewMessage.setOnClickListener {
      val intent = Intent(this, NewMessageActivity::class.java)
      startActivity(intent)
    }
  }

  private fun fetchCurrentUser() {
    val uid = auth.uid
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
    ref.addListenerForSingleValueEvent(object: ValueEventListener{
      override fun onDataChange(snapshot: DataSnapshot) {
        val id = snapshot.child("uid").value.toString()
        val username = snapshot.child("username").value.toString()
        val profileImageUrl = snapshot.child("profileImageUrl").value.toString()
        currentUser = User(id, username, profileImageUrl)
      }

      override fun onCancelled(error: DatabaseError) {
        return
      }
    })
  }

  private fun verifyUserIsLoggedIn() {
    val uid = auth.uid
    if (uid == null) {
      val intent = Intent(this, RegisterActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
      startActivity(intent)
    }
  }

  private fun refreshRecyclerViewMessages(chatItems: MutableList<ChatItem>) {
    chatItems.clear()
    latestMessagesMap.values.forEach {
      chatItems.add(it)
      adapter.submitList(chatItems.toMutableList())
    }
  }

  private fun listenLatestMessages() {
    val fromId = auth.uid
    val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
    val chatItems: MutableList<ChatItem> = mutableListOf()
    ref.addChildEventListener(object: ChildEventListener {
      override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        binding.tvStartChatting.visibility = View.INVISIBLE
        val chatItem = snapshot.getValue(ChatItem::class.java) ?: return
        latestMessagesMap[snapshot.key!!] = chatItem
        refreshRecyclerViewMessages(chatItems)
      }

      override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        val chatItem = snapshot.getValue(ChatItem::class.java) ?: return
        latestMessagesMap[snapshot.key!!] = chatItem
        refreshRecyclerViewMessages(chatItems)
      }

      override fun onChildRemoved(snapshot: DataSnapshot) {
        return
      }

      override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        return
      }

      override fun onCancelled(error: DatabaseError) {
        return
      }

    })
  }

  companion object {
    var currentUser: User? = null
  }
}