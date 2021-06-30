package com.example.foruma.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.foruma.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

  private lateinit var binding : ActivityLoginBinding

  private lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    auth = Firebase.auth

    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.btnLogin.setOnClickListener {
      login()
    }

    binding.tvRegister.setOnClickListener {
      finish()
    }
  }

  private fun login () {
    binding.progressBar.visibility = View.VISIBLE
    binding.btnLogin.isEnabled = false
    val email = binding.etEmail.text.toString()
    val password = binding.etPassword.text.toString()

    if(email.isEmpty() || password.isEmpty()) {
      Toast.makeText(this, "Email or password must not be empty", Toast.LENGTH_SHORT).show()
      return
    }

    auth.signInWithEmailAndPassword(email, password)
      .addOnCompleteListener { task ->
        binding.progressBar.visibility = View.INVISIBLE
        binding.btnLogin.isEnabled = true
        if (!task.isSuccessful) return@addOnCompleteListener

        Toast.makeText(this, "Successfully logged in!", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LatestMessageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
      }
      .addOnFailureListener {
        Toast.makeText(this, "Failed to login: ${it.message}", Toast.LENGTH_SHORT).show()
      }
  }
}