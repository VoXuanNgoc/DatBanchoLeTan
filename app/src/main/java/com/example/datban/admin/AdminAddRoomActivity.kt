//package com.example.datban.admin
//
//import android.os.Bundle
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Spinner
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.datban.R
//import com.example.datban.model.TableModel
//import com.google.firebase.Firebase
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.database
//
//class AdminAddRoomActivity : AppCompatActivity() {
//
//    private lateinit var edtKhu: EditText
//    private lateinit var spinnerSoBan: Spinner
//    private lateinit var edtSoGhe: EditText
//    private lateinit var btnThemBan: Button
//
//    private lateinit var database: DatabaseReference
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        database = Firebase.database.reference
//
//        // Gán view đúng theo id trong file XML mới
//        edtKhu = findViewById(R.id.addKhu)
//        spinnerSoBan = findViewById(R.id.soban)
//        edtSoGhe = findViewById(R.id.soGhe)
//        btnThemBan = findViewById(R.id.themban)
//        // Đổ danh sách số bàn vào spinner
//        val danhSachBan = (1..20).map { "Bàn $it" }
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, danhSachBan)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerSoBan.adapter = adapter
//        btnThemBan.setOnClickListener {
//            val khu = edtKhu.text.toString().trim()
//            val soBan = spinnerSoBan.selectedItem.toString()
//            val soGheStr = edtSoGhe.text.toString().trim()
//
//            if (khu.isEmpty() || soGheStr.isEmpty()) {
//                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            val soGhe = soGheStr.toIntOrNull()
//            if (soGhe == null || soGhe <= 0) {
//                Toast.makeText(this, "Số ghế không hợp lệ", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            val id = database.child("tables").push().key ?: return@setOnClickListener
//            val table = TableModel(id, khu, soBan, soGhe)
//
//            database.child("tables").child(id).setValue(table)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "Thêm bàn thành công", Toast.LENGTH_SHORT).show()
//                    edtKhu.text.clear()
//                    edtSoGhe.text.clear()
//                }
//                .addOnFailureListener {
//                    Toast.makeText(this, "Lỗi khi thêm bàn", Toast.LENGTH_SHORT).show()
//                }
//        }
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_admin_add_room)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//}
package com.example.datban.admin

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.datban.R
import com.example.datban.model.TableModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminAddRoomActivity : AppCompatActivity() {

    private lateinit var edtKhu: EditText
    private lateinit var spinnerSoBan: Spinner
    private lateinit var edtSoGhe: EditText
    private lateinit var btnThemBan: Button

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_room)

        // Ánh xạ view
        edtKhu = findViewById(R.id.addKhu)
        spinnerSoBan = findViewById(R.id.soban)
        edtSoGhe = findViewById(R.id.soGhe)
        btnThemBan = findViewById(R.id.themban)

        // Khởi tạo Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("tables")

        // Khởi tạo danh sách số bàn cho spinner
        val soBanList = (1..20).map { "Bàn $it" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, soBanList)
        spinnerSoBan.adapter = adapter

        btnThemBan.setOnClickListener {
            addTable()
        }
    }

    private fun addTable() {
        val khu = edtKhu.text.toString().trim()
        val soBan = spinnerSoBan.selectedItem.toString()
        val soGheText = edtSoGhe.text.toString().trim()

        if (khu.isEmpty() || soGheText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val soGhe = try {
            soGheText.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Số ghế không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        val id = dbRef.push().key ?: return
        val table = TableModel(id, khu, soBan, soGhe)

        dbRef.child(id).setValue(table)
            .addOnSuccessListener {
                Toast.makeText(this, "Đã thêm bàn", Toast.LENGTH_SHORT).show()
                edtKhu.text.clear()
                edtSoGhe.text.clear()
                spinnerSoBan.setSelection(0)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi thêm bàn", Toast.LENGTH_SHORT).show()
            }
    }
}
