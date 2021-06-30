package com.example.foruma.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foruma.Encryption
import com.example.foruma.adapter.ChatLogAdapter
import com.example.foruma.databinding.ActivityChatLogBinding
import com.example.foruma.model.ChatItem
import com.example.foruma.model.NewMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class ChatLogActivity : AppCompatActivity() {

  private lateinit var binding: ActivityChatLogBinding

  private lateinit var auth: FirebaseAuth

  private lateinit var adapter: ChatLogAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    auth = Firebase.auth

    binding = ActivityChatLogBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.ibBack.setOnClickListener {
      this@ChatLogActivity.onBackPressed()
    }

    binding.btnSend.setOnClickListener {
      sendMessage()
    }

    val newMessage = intent.getParcelableExtra<NewMessage>("user")

    binding.rvChatLog.layoutManager = LinearLayoutManager(this)
    if (newMessage != null) {
      adapter = ChatLogAdapter(newMessage)
    }
    binding.rvChatLog.adapter = adapter

    binding.tvName.text = newMessage?.name

    listenMessage()
  }

  private fun sendMessage() {
    val newMessage = intent.getParcelableExtra<NewMessage>("user")

    if (binding.etMessage.text.toString().isEmpty()) return

    val message = Encryption.encrypt(binding.etMessage.text.toString())
    val fromId = auth.uid
    val toId = newMessage?.uid

    if (fromId == null || toId == null) return

    val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
    val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
    val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
    val latestMessageToRef = FirebaseDatabase.getInstance().getReference("latest-messages/$toId/$fromId")
    val chatItem = ChatItem(ref.key!!, message, fromId, toId, System.currentTimeMillis() / 1000)

    ref.setValue(chatItem).addOnCompleteListener {
      binding.etMessage.text.clear()
      binding.rvChatLog.scrollToPosition(adapter.itemCount - 1)
    }

    toRef.setValue(chatItem)
    latestMessageRef.setValue(chatItem)
    latestMessageToRef.setValue(chatItem)
  }

  private fun listenMessage() {
    val newMessage = intent.getParcelableExtra<NewMessage>("user")

    val fromId = auth.uid
    val toId = newMessage?.uid
    val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
    val chatItems: MutableList<ChatItem> = mutableListOf()

    ref.addChildEventListener(object : ChildEventListener {
      override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        val chatItem = snapshot.getValue(ChatItem::class.java) ?: return
        chatItems.add(chatItem)
        adapter.submitList(chatItems)
      }

      override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        TODO("Not yet implemented")
      }

      override fun onChildRemoved(snapshot: DataSnapshot) {
        TODO("Not yet implemented")
      }

      override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        TODO("Not yet implemented")
      }

      override fun onCancelled(error: DatabaseError) {
        return
      }

    })
  }
}