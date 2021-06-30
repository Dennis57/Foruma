package com.example.foruma.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewMessage(
  val uid: String,
  val name: String,
  val photo: String
) : Parcelable