package com.example.foruma.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foruma.Encryption
import com.example.foruma.databinding.ItemLatestMessageBinding
import com.example.foruma.model.ChatItem
import com.example.foruma.model.NewMessage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LatestMessageAdapter(val onClickListener: (user: NewMessage) -> Unit) :
  ListAdapter<ChatItem, LatestMessageAdapter.LatestMessageViewHolder>(
    DiffCallback
  ) {

  inner class LatestMessageViewHolder(private var binding: ItemLatestMessageBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(chatItem: ChatItem) {
      binding.tvMessage.text = Encryption.decrypt(chatItem.message)

      val friendId: String = if (chatItem.fromId == Firebase.auth.uid) {
        chatItem.toId
      } else {
        chatItem.fromId
      }

      val ref = Firebase.database.getReference("/users/$friendId")
      ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
          val uid = snapshot.child("uid").value.toString()
          val username = snapshot.child("username").value.toString()
          val profileImageUrl = snapshot.child("profileImageUrl").value.toString()

          binding.tvUsername.text = username
          Glide.with(binding.root).load(profileImageUrl).into(binding.civPhoto)

          binding.root.setOnClickListener {
            onClickListener(NewMessage(uid, username, profileImageUrl))
          }
        }

        override fun onCancelled(error: DatabaseError) {
          return
        }

      })
    }
  }

  companion object DiffCallback : DiffUtil.ItemCallback<ChatItem>() {
    override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
      return oldItem == newItem
    }
  }

  override fun onCreateViewHolder(
    parent: ViewGroup, viewType: Int
  ): LatestMessageAdapter.LatestMessageViewHolder {
    return LatestMessageViewHolder(
      ItemLatestMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  override fun onBindViewHolder(
    holder: LatestMessageAdapter.LatestMessageViewHolder, position: Int
  ) {
    val chatItem: ChatItem = getItem(position)
    holder.bind(chatItem)
  }

}