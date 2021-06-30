package com.example.foruma.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foruma.R
import com.example.foruma.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

  private lateinit var binding : ActivityProfileBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile)
  }
}