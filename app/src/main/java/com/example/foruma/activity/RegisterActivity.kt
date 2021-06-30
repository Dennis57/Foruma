package com.example.foruma.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foruma.databinding.ActivityRegisterBinding
import com.example.foruma.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RegisterActivity : AppCompatActivity() {

  private lateinit var binding: ActivityRegisterBinding

  private lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    auth = Firebase.auth

    binding = ActivityRegisterBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.btnSelectPhoto.setOnClickListener {
      val intent = Intent(Intent.ACTION_PICK)
      intent.type = "image/*"
      startActivityForResult(intent, 0)
    }

    binding.btnRegister.setOnClickListener {
      register()
    }

    binding.tvLogin.setOnClickListener {
      val intent = Intent(this@RegisterActivity,  LoginActivity::class.java)
      startActivity(intent)
    }
  }

  private var selectedPhotoUri : Uri? = null

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
      selectedPhotoUri = data.data

      val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

      binding.civSelectPhoto.setImageBitmap(bitmap)
      binding.btnSelectPhoto.alpha = 0f
    }
  }

  private fun register() {
    binding.progressBar.visibility = View.VISIBLE
    binding.btnRegister.isEnabled = false
    val email = binding.etEmail.text.toString()
    val password = binding.etPassword.text.toString()
    val username = binding.etName.text.toString()

    if(username.isEmpty() || email.isEmpty() || password.isEmpty() || selectedPhotoUri == null) {
      Toast.makeText(this, "Form must not be empty", Toast.LENGTH_SHORT).show()
      binding.progressBar.visibility = View.INVISIBLE
      binding.btnRegister.isEnabled = true
      return
    }

    auth.createUserWithEmailAndPassword(email, password)
      .addOnCompleteListener(this) { task ->
        if (!task.isSuccessful) return@addOnCompleteListener
        uploadImageToFirebaseStorage()
      }
      .addOnFailureListener {
        Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
        binding.progressBar.visibility = View.INVISIBLE
        binding.btnRegister.isEnabled = true
      }
  }

  private fun uploadImageToFirebaseStorage () {
    val fileName = UUID.randomUUID().toString()
    val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")

    ref.putFile(selectedPhotoUri!!)
      .addOnSuccessListener {
        ref.downloadUrl.addOnSuccessListener { uri ->
          saveUserToDatabase(uri.toString())
        }
      }
  }

  private fun saveUserToDatabase (profileImageUrl: String) {
    val uid = FirebaseAuth.getInstance().uid ?: ""
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

    val username = binding.etName.text.toString()

    val user = User(uid, username, profileImageUrl)

    ref.setValue(user)
      .addOnSuccessListener {
        Toast.makeText(this, "Successfully registered!", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LatestMessageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
      }
      .addOnFailureListener {
        Toast.makeText(this, "Failed to register: $it", Toast.LENGTH_SHORT).show()
      }
      .addOnCompleteListener {
        binding.progressBar.visibility = View.INVISIBLE
        binding.btnRegister.isEnabled = true
      }
  }
}