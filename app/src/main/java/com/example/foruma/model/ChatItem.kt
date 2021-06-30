package com.example.foruma.model

data class ChatItem(
  val id: String,
  val message: String,
  val fromId: String,
  val toId: String,
  val timeStamp: Long
) {
  constructor() : this("", "", "", "", -1)
}