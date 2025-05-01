package com.example.datban

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.datban.databinding.ActivitySignBinding
import com.example.datban.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
              import com.google.firebase.database.ktx.database

class SignActivity : AppCompatActivity() {
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private val binding: ActivitySignBinding by lazy {
        ActivitySignBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

       // auth = Firebase.auth
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        binding.creatacc.setOnClickListener {
            username = binding.name.text.toString().trim()
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()

            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password)
            }
        }

        binding.alreadyhaveaccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                    saveUserData()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show()
                    Log.d("Account", "create account: Failed", task.exception)
                }
            }
    }

    private fun saveUserData() {
        username = binding.name.text.toString().trim()
        email = binding.email.text.toString().trim()
        password = binding.password.text.toString().trim()
        val user=UserModel(username,email,password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("user").child(userId).setValue(user)
    }
}
