package com.example.foruma.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.foruma.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

  private lateinit var binding : ActivityProfileBinding

  private lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityProfileBinding.inflate(layoutInflater)
    setContentView(binding.root)

    auth = Firebase.auth

    fetchCurrentUser()

    binding.ibBack.setOnClickListener {
      this@ProfileActivity.onBackPressed()
    }

    binding.ibSignOut.setOnClickListener {
      auth.signOut()
      val intent = Intent(this, RegisterActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
      startActivity(intent)
    }
  }

  private fun fetchCurrentUser() {
    val uid = auth.uid
    val email = auth.currentUser?.email

    binding.tvEmail.text = email

    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
    ref.addListenerForSingleValueEvent(object: ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        val username = snapshot.child("username").value.toString()
        val profileImageUrl = snapshot.child("profileImageUrl").value.toString()

        binding.etName.setText(username)
        Glide.with(binding.root).load(profileImageUrl).into(binding.civSelectPhoto)
      }

      override fun onCancelled(error: DatabaseError) {
        return
      }
    })
  }
}