package com.example.foruma.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foruma.databinding.ItemNewMessageBinding
import com.example.foruma.model.NewMessage

class NewMessageAdapter(val onClickListener: (user: NewMessage) -> Unit) :
  ListAdapter<NewMessage, NewMessageAdapter.NewMessageViewHolder>(
    DiffCallback
  ) {

  inner class NewMessageViewHolder(private var binding: ItemNewMessageBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(newMessage: NewMessage) {
      Glide.with(binding.root).load(newMessage.photo).into(binding.civPhoto)
      binding.tvName.text = newMessage.name
      binding.root.setOnClickListener {
        onClickListener(newMessage)
      }
    }
  }

  companion object DiffCallback : DiffUtil.ItemCallback<NewMessage>() {
    override fun areItemsTheSame(oldItem: NewMessage, newItem: NewMessage): Boolean {
      return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: NewMessage, newItem: NewMessage): Boolean {
      return oldItem.name == newItem.name && oldItem.photo == newItem.photo
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewMessageViewHolder {
    return NewMessageViewHolder(
      ItemNewMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  override fun onBindViewHolder(holder: NewMessageViewHolder, position: Int) {
    val newMessage: NewMessage = getItem(position)
    holder.bind(newMessage)
  }
}