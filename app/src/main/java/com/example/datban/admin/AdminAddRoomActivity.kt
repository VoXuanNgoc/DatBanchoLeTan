//
//package com.example.datban.admin
//
//import android.os.Bundle
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import com.example.datban.R
//import com.example.datban.model.TableModel
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//
//class AdminAddRoomActivity : AppCompatActivity() {
//
//    private lateinit var edtKhu: EditText
//    private lateinit var spinnerSoBan: Spinner
//    private lateinit var edtSoGhe: EditText
//    private lateinit var btnThemBan: Button
//
//    private lateinit var dbRef: DatabaseReference
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_admin_add_room)
//
//        // Ánh xạ view
//        edtKhu = findViewById(R.id.addKhu)
//        spinnerSoBan = findViewById(R.id.soban)
//        edtSoGhe = findViewById(R.id.soGhe)
//        btnThemBan = findViewById(R.id.themban)
//
//        // Khởi tạo Firebase
//        dbRef = FirebaseDatabase.getInstance().getReference("tables")
//
//        // Khởi tạo danh sách số bàn cho spinner
//        val soBanList = (1..20).map { "Bàn $it" }
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, soBanList)
//        spinnerSoBan.adapter = adapter
//
//        btnThemBan.setOnClickListener {
//            addTable()
//        }
//    }
//
//    private fun addTable() {
//        val khu = edtKhu.text.toString().trim()
//        val soBan = spinnerSoBan.selectedItem.toString()
//        val soGheText = edtSoGhe.text.toString().trim()
//
//        if (khu.isEmpty() || soGheText.isEmpty()) {
//            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val soGhe = try {
//            soGheText.toInt()
//        } catch (e: NumberFormatException) {
//            Toast.makeText(this, "Số ghế không hợp lệ", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val id = dbRef.push().key ?: return
//        val table = TableModel(id, khu, soBan, soGhe)
//
//        dbRef.child(id).setValue(table)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Đã thêm bàn", Toast.LENGTH_SHORT).show()
//                edtKhu.text.clear()
//                edtSoGhe.text.clear()
//                spinnerSoBan.setSelection(0)
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "Lỗi khi thêm bàn", Toast.LENGTH_SHORT).show()
//            }
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

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
            checkAndAddTable()
        }
    }

    private fun checkAndAddTable() {
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

        dbRef.orderByChild("khu").equalTo(khu).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isDuplicate = false

                for (child in snapshot.children) {
                    val table = child.getValue(TableModel::class.java)
                    if (table != null && table.soBan == soBan) {
                        isDuplicate = true
                        break
                    }
                }

                if (isDuplicate) {
                    Toast.makeText(this@AdminAddRoomActivity, "Bàn này đã tồn tại", Toast.LENGTH_SHORT).show()
                } else {
                    addTable(khu, soBan, soGhe)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminAddRoomActivity, "Lỗi khi kiểm tra bàn", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addTable(khu: String, soBan: String, soGhe: Int) {
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

