// AdminAddMenuActivity.kt
package com.example.datban.admin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.datban.R
import com.example.datban.databinding.ActivityAdminAddMenuBinding
import com.example.datban.model.FoodItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminAddMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAddMenuBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAddMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().getReference("menuItems")

        binding.addButton.setOnClickListener {
            addMenuItem()
        }
    }

    private fun addMenuItem() {
        // Kiểm tra đăng nhập admin
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        // Kiểm tra loại món
        val type = when (binding.typeRadioGroup.checkedRadioButtonId) {
            R.id.buffetRadio -> "buffet"
            R.id.aLaCarteRadio -> "a_la_carte"
            else -> {
                Toast.makeText(this, "Vui lòng chọn loại món", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Lấy và validate dữ liệu
        val name = binding.nameEditText.text.toString().trim()
        val priceText = binding.priceEditText.text.toString().trim()
        val imageUrl = binding.imageUrlEditText.text.toString().trim()
        val quantityText = binding.quantityEditText.text.toString().trim()

        if (name.isEmpty()) {
            binding.nameEditText.error = "Tên món không được trống"
            return
        }

        val price = try {
            priceText.toDouble().takeIf { it > 0 } ?: throw NumberFormatException()
        } catch (e: NumberFormatException) {
            binding.priceEditText.error = "Giá phải là số dương"
            return
        }

        if (imageUrl.isEmpty()) {
            binding.imageUrlEditText.error = "URL hình ảnh không được trống"
            return
        }

        val quantity = try {
            quantityText.toInt().takeIf { it >= 0 } ?: throw NumberFormatException()
        } catch (e: NumberFormatException) {
            binding.quantityEditText.error = "Số lượng phải ≥ 0"
            return
        }

        // Tạo món ăn
        val menuItem = FoodItem(
            name = name,
            price = price,
            imageUrl = imageUrl,
            quantity = quantity,
            type = type
        )

        // Thêm vào Firebase
        FirebaseDatabase.getInstance().getReference("menu")
            .push()
            .setValue(menuItem)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Thêm món thành công!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.e("FIREBASE_ERROR", "Lỗi: ${task.exception?.message}")
                    Toast.makeText(
                        this,
                        "Lỗi: ${task.exception?.message ?: "Không xác định"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}