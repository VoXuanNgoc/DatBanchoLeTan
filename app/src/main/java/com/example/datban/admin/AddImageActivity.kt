package com.example.datban.admin
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.datban.R
import com.google.firebase.database.FirebaseDatabase

class AddImageActivity : AppCompatActivity() {

    private lateinit var etImageUrl: EditText
    private lateinit var btnAddImage: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)

        etImageUrl = findViewById(R.id.etImageUrl)
        btnAddImage = findViewById(R.id.btnAddImage)

        btnAddImage.setOnClickListener {
            val url = etImageUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                addImageToFirebase(url)
            } else {
                Toast.makeText(this, "Vui lòng nhập URL ảnh", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addImageToFirebase(url: String) {
        val database = FirebaseDatabase.getInstance().getReference("Image")
        // Tạo key mới cho item
        val newKey = database.push().key

        if (newKey == null) {
            Toast.makeText(this, "Lỗi tạo khóa mới", Toast.LENGTH_SHORT).show()
            return
        }

        val menuItem = mapOf("imageUrl" to url)

        database.child(newKey).setValue(menuItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Thêm ảnh thành công!", Toast.LENGTH_SHORT).show()
                etImageUrl.text.clear()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Thêm ảnh thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
