package com.example.datban

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.datban.model.Reservation
import com.google.firebase.database.*
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var edtDate: EditText
    private lateinit var btnLoad: Button
    private lateinit var tvTotalBookings: TextView
    private lateinit var tvTotalPax: TextView
    private lateinit var tvCancelledBookings: TextView
    private lateinit var tvCancelledPax: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        edtDate = findViewById(R.id.edtDate)
        btnLoad = findViewById(R.id.btnLoad)
        tvTotalBookings = findViewById(R.id.tvTotalBookings)
        tvTotalPax = findViewById(R.id.tvTotalPax)
        tvCancelledBookings = findViewById(R.id.tvCancelledBookings)
        tvCancelledPax = findViewById(R.id.tvCancelledPax)

        // Chỉ cho chọn ngày, không gõ
        edtDate.inputType = InputType.TYPE_NULL

        database = FirebaseDatabase.getInstance().getReference("reservations")

        // Gắn DatePickerDialog cho edtDate
        edtDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this,
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    val dayStr = if (selectedDayOfMonth < 10) "0$selectedDayOfMonth" else "$selectedDayOfMonth"
                    val monthStr = if (selectedMonth + 1 < 10) "0${selectedMonth + 1}" else "${selectedMonth + 1}"
                    val formattedDate = "$dayStr/$monthStr/$selectedYear"
                    edtDate.setText(formattedDate)
                },
                year, month, day)
            datePickerDialog.show()
        }

        btnLoad.setOnClickListener {
            val selectedDate = edtDate.text.toString()
            if (selectedDate.isNotEmpty()) {
                loadStatistics(selectedDate)
            } else {
                Toast.makeText(this, "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadStatistics(date: String) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalBookings = 0
                var totalPax = 0
                var cancelledBookings = 0
                var cancelledPax = 0

                for (child in snapshot.children) {
                    val reservation = child.getValue(Reservation::class.java)
                    if (reservation != null) {
                        // Check ngày trùng
                        if (reservation.date == date) {
                            if (reservation.status == "Đã hủy") {
                                cancelledBookings++
                                cancelledPax += reservation.pax
                            } else {
                                totalBookings++
                                totalPax += reservation.pax
                            }
                        }
                    }
                }

                tvTotalBookings.text = "Tổng số bàn đặt: $totalBookings"
                tvTotalPax.text = "Tổng số pax: $totalPax"
                tvCancelledBookings.text = "Tổng số bàn hủy: $cancelledBookings"
                tvCancelledPax.text = "Tổng số pax hủy: $cancelledPax"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@StatisticsActivity, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
