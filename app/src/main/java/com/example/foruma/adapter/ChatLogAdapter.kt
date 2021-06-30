package com.example.foruma.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foruma.Encryption
import com.example.foruma.activity.LatestMessageActivity
import com.example.foruma.R
import com.example.foruma.databinding.ItemLeftChatLogBinding
import com.example.foruma.databinding.ItemRightChatLogBinding
import com.example.foruma.model.ChatItem
import com.example.foruma.model.NewMessage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChatLogAdapter(val newMessage: NewMessage) : ListAdapter<ChatItem, RecyclerView.ViewHolder>(
  DiffCallback
) {
  inner class LeftChatLogViewHolder(private var leftBinding: ItemLeftChatLogBinding) :
    RecyclerView.ViewHolder(leftBinding.root) {
      fun bind(chatItem: ChatItem) {
        Glide.with(leftBinding.root).load(newMessage.photo).into(leftBinding.civChatLog)
        leftBinding.tvMessage.text = Encryption.decrypt(chatItem.message)
      }

  }

  inner class RightChatLogViewHolder(private var rightBinding: ItemRightChatLogBinding) :
    RecyclerView.ViewHolder(rightBinding.root) {
      fun bind(chatItem: ChatItem) {
        Glide.with(rightBinding.root).load(LatestMessageActivity.currentUser?.profileImageUrl)
          .into(rightBinding.civChatLog)
        rightBinding.tvMessage.text = Encryption.decrypt(chatItem.message)
      }

  }

  companion object DiffCallback : DiffUtil.ItemCallback<ChatItem>() {
    override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
      return oldItem.timeStamp == newItem.timeStamp
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return if (viewType == 1) {
      val view = inflater.inflate(R.layout.item_right_chat_log, parent, false)
      val binding = ItemRightChatLogBinding.bind(view)
      RightChatLogViewHolder(binding)
    } else {
      val view = inflater.inflate(R.layout.item_left_chat_log, parent, false)
      val binding = ItemLeftChatLogBinding.bind(view)
      LeftChatLogViewHolder(binding)
    }
  }

  override fun getItemViewType(position: Int): Int {
    return if(Firebase.auth.uid == getItem(position).fromId) {
      1
    } else {
      2
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val chatItem : ChatItem = getItem(position)
    if(Firebase.auth.uid == chatItem.fromId) {
      (holder as RightChatLogViewHolder).bind(chatItem)
    } else {
      (holder as LeftChatLogViewHolder).bind(chatItem)
    }
  }
}