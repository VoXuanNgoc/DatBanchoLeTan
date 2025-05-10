//package com.example.datban
//
//import android.app.DatePickerDialog
//import android.app.TimePickerDialog
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Locale
//
//class AddActivity : AppCompatActivity() {
//
//    private lateinit var textSelectedTable: TextView
//
//    //
//    private lateinit var editTextDate: EditText
//    private lateinit var editTextTime: TextView
//    private lateinit var editTextTime2: TextView
//    private val calendar = Calendar.getInstance()
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_add)
//
//        editTextDate = findViewById(R.id.editTextDate)
//        editTextTime = findViewById(R.id.editTextTime).findViewById(TextView::class.java)
//        editTextTime2 = findViewById(R.id.editTextTime2).findViewById(TextView::class.java)
//
//
//        setupDatePicker()
//        setupTimePickers()
//
//        // Đặt sau setContentView
//        textSelectedTable = findViewById(R.id.textSelectedTable)
//
//        val btnChonBan = findViewById<Button>(R.id.button5)
//        btnChonBan.setOnClickListener {
//            val intent = Intent(this, SelectTableActivity::class.java)
//            startActivityForResult(intent, 1001)
//        }
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//    private fun setupDatePicker() {
//        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
//            calendar.set(Calendar.YEAR, year)
//            calendar.set(Calendar.MONTH, month)
//            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//            updateDateInView()
//        }
//
//        editTextDate.setOnClickListener {
//            DatePickerDialog(
//                this,
//                dateSetListener,
//                calendar.get(Calendar.YEAR),
//                calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH)
//            ).show()
//        }
//    }
//
//    private fun setupTimePickers() {
//        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
//            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
//            calendar.set(Calendar.MINUTE, minute)
//            updateTimeInView()
//        }
//
//        editTextTime.setOnClickListener {
//            TimePickerDialog(
//                this,
//                { _, hourOfDay, minute ->
//                    val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
//                    editTextTime.text = formattedTime
//                },
//                calendar.get(Calendar.HOUR_OF_DAY),
//                calendar.get(Calendar.MINUTE),
//                true
//            ).show()
//        }
//
//        editTextTime2.setOnClickListener {
//            TimePickerDialog(
//                this,
//                { _, hourOfDay, minute ->
//                    val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
//                    editTextTime2.text = formattedTime
//                },
//                calendar.get(Calendar.HOUR_OF_DAY),
//                calendar.get(Calendar.MINUTE),
//                true
//            ).show()
//        }
//    }
//
//    private fun updateDateInView() {
//        val myFormat = "dd/MM/yyyy" // Định dạng ngày tháng
//        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
//        editTextDate.setText(sdf.format(calendar.time))
//    }
//
//    private fun updateTimeInView() {
//        val myFormat = "HH:mm" // Định dạng 24 giờ
//        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
//        editTextTime.setText(sdf.format(calendar.time))
//    }
//
//    private fun updateTimeInView2() {
//        val myFormat = "HH:mm" // Định dạng 24 giờ
//        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
//        editTextTime2.setText(sdf.format(calendar.time))
//    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 1001 && resultCode == RESULT_OK) {
//            val selectedTable = data?.getStringExtra("selected_table")
//            textSelectedTable.text = selectedTable ?: "Chưa chọn bàn"
//        }
//    }
//}
package com.example.datban

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddActivity : AppCompatActivity() {

    private lateinit var textSelectedTable: TextView
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: TextView  // Đã thay đổi từ EditText sang TextView
    private lateinit var editTextTime2: TextView // Đã thay đổi từ EditText sang TextView
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add)

        // Khởi tạo các view
        editTextDate = findViewById(R.id.editTextDate)
        textSelectedTable = findViewById(R.id.textSelectedTable)

        // Lấy TextView từ CardView (không cần findViewById(TextView::class.java))
        editTextTime = findViewById<androidx.cardview.widget.CardView>(R.id.editTextTime).getChildAt(0) as TextView
        editTextTime2 = findViewById<androidx.cardview.widget.CardView>(R.id.editTextTime2).getChildAt(0) as TextView

        setupDatePicker()
        setupTimePickers()

        val btnChonBan = findViewById<Button>(R.id.button5)
        btnChonBan.setOnClickListener {
            val intent = Intent(this, SelectTableActivity::class.java)
            startActivityForResult(intent, 1001)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        editTextDate.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupTimePickers() {
        editTextTime.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                    editTextTime.text = formattedTime
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        editTextTime2.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                    editTextTime2.text = formattedTime
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        editTextDate.setText(sdf.format(calendar.time))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val selectedTable = data?.getStringExtra("selected_table")
            textSelectedTable.text = selectedTable ?: "Chưa chọn bàn"
        }
    }
}