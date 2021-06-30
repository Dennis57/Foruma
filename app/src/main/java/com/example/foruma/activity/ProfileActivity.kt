package com.example.foruma.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.foruma.databinding.ActivityProfileBinding
import com.example.foruma.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

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

    binding.btnSelectPhoto.setOnClickListener {
      val intent = Intent(Intent.ACTION_PICK)
      intent.type = "image/*"
      startActivityForResult(intent, 0)
    }

    binding.btnUpdateProfile.setOnClickListener {
      updateProfile()
    }
  }

  private var selectedPhotoUri : Uri? = null

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
      selectedPhotoUri = data.data

      val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

      binding.civSelectPhoto.setImageBitmap(bitmap)
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
        selectedPhotoUri = profileImageUrl.toUri()

        binding.etName.setText(username)
        Glide.with(binding.root).load(profileImageUrl).into(binding.civSelectPhoto)
        binding.btnSelectPhoto.alpha = 0f
      }

      override fun onCancelled(error: DatabaseError) {
        return
      }
    })
  }

  private fun updateProfile() {
    if (binding.etName.text.toString().isEmpty()) {
      Toast.makeText(this, "The form must not be empty", Toast.LENGTH_SHORT).show()
      return
    }
    uploadImageToFirebaseStorage()
  }

  private fun uploadImageToFirebaseStorage () {
    binding.progressBar.visibility = View.VISIBLE
    binding.btnUpdateProfile.isEnabled = false

    val fileName = UUID.randomUUID().toString()
    val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")

    ref.putFile(selectedPhotoUri!!)
      .addOnSuccessListener {
        ref.downloadUrl.addOnSuccessListener { uri ->
          saveUserToDatabase(uri.toString())
        }
      }
      .addOnFailureListener {
        saveUserToDatabase(selectedPhotoUri.toString())
      }
  }

  private fun saveUserToDatabase (profileImageUrl: String) {
    val uid = FirebaseAuth.getInstance().uid ?: ""
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

    val username = binding.etName.text.toString()

    val user = User(uid, username, profileImageUrl)

    ref.setValue(user)
      .addOnSuccessListener {
        Toast.makeText(this, "Successfully updated!", Toast.LENGTH_SHORT).show()
      }
      .addOnFailureListener {
        Toast.makeText(this, "Failed to update: $it", Toast.LENGTH_SHORT).show()
      }
      .addOnCompleteListener {
        binding.progressBar.visibility = View.INVISIBLE
        binding.btnUpdateProfile.isEnabled = true
      }
  }
}