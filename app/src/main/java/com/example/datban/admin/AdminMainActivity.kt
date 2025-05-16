package com.example.datban.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.datban.R
import com.example.datban.SignActivity
import com.example.datban.databinding.ActivityAdminMainBinding
import com.example.datban.databinding.ActivityLoginBinding

class AdminMainActivity : AppCompatActivity() {

    private val binding: ActivityAdminMainBinding by lazy {
        ActivityAdminMainBinding.inflate(layoutInflater)
    }

    // Update this in AdminMainActivity.kt
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding.addTable.setOnClickListener {
            val intent = Intent(this, AdminAddRoomActivity::class.java)
            startActivity(intent)
        }

        binding.addMenu.setOnClickListener {
            val intent = Intent(this, AdminAddMenuActivity::class.java)
            startActivity(intent)
        }

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}