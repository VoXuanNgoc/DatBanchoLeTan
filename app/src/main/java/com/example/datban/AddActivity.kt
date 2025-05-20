package com.example.datban

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.datban.model.Reservation
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddActivity : AppCompatActivity() {

    private lateinit var textSelectedTable: TextView
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: TextView
    private lateinit var editTextTime2: TextView
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var paxEditText: EditText
    private lateinit var btnDatban: Button
    private val calendar = Calendar.getInstance()
    private var selectedHourArrival = -1
    private var selectedMinuteArrival = -1

    private lateinit var database: DatabaseReference
    private var selectedTable: String = "" // Biến toàn cục lưu bàn đã chọn

    private var isEditMode = false
    private var reservationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add)

        // Khởi tạo Firebase Database
        database = FirebaseDatabase.getInstance().getReference("reservations")

        // Khởi tạo các view
        editTextDate = findViewById(R.id.editTextDate)
        textSelectedTable = findViewById(R.id.textSelectedTable)
        nameEditText = findViewById(R.id.name2)
        phoneEditText = findViewById(R.id.editTextPhone)
        paxEditText = findViewById(R.id.pax)
        btnDatban = findViewById(R.id.btnDatban)

        // Lấy TextView từ CardView
        editTextTime = findViewById<androidx.cardview.widget.CardView>(R.id.editTextTime).getChildAt(0) as TextView
        editTextTime2 = findViewById<androidx.cardview.widget.CardView>(R.id.editTextTime2).getChildAt(0) as TextView

        setupDatePicker()
        setupTimePickers()

        val btnChonBan = findViewById<Button>(R.id.button5)
        btnChonBan.setOnClickListener {
            val intent = Intent(this, SelectTableActivity::class.java)
            intent.putExtra("selected_date", editTextDate.text.toString()) // Truyền ngày vào Intent
            startActivityForResult(intent, 1001)
        }

        // Xử lý sự kiện khi nhấn nút Đặt Bàn
        btnDatban.setOnClickListener {
            saveReservation()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Nhận dữ liệu từ Intent
        handleIntentData()

        // Thay đổi tiêu đề và text nút nếu là chế độ chỉnh sửa
        if (isEditMode) {
            btnDatban.text = "Cập nhật đặt bàn"
            supportActionBar?.title = "Chỉnh sửa đặt bàn"
        }

    }

//    private fun saveReservation() {
//        val name = nameEditText.text.toString().trim()
//        val phone = phoneEditText.text.toString().trim()
//        val pax = try {
//            paxEditText.text.toString().trim().toInt()
//        } catch (e: Exception) {
//            Toast.makeText(this, "Số lượng khách phải là số", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val date = editTextDate.text.toString().trim()
//        val timeArrival = editTextTime.text.toString().trim()
//        val timeDeparture = editTextTime2.text.toString().trim()
//        val table = selectedTable
//
//        // Validate dữ liệu
//        if (name.isEmpty() || phone.isEmpty() || pax <= 0 || date.isEmpty() ||
//            timeArrival.isEmpty() || timeDeparture.isEmpty() || table.isEmpty()) {
//            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin hợp lệ", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val id = reservationId ?: database.push().key ?: UUID.randomUUID().toString()
//        val reservation = Reservation(
//            id = id,
//            name = name,
//            phone = phone,
//            pax = pax,
//            date = date,
//            timeArrival = timeArrival,
//            timeDeparture = timeDeparture,
//            table = table,
//            status = if (isEditMode) intent.getStringExtra("status") ?: "Chưa đến" else "Chưa đến"
//        )
//
//        database.child(id).setValue(reservation)
//            .addOnSuccessListener {
//                val message = if (isEditMode) "Cập nhật đặt bàn thành công!" else "Đặt bàn thành công!"
//                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//                finish()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
private fun saveReservation() {
    val name = nameEditText.text.toString().trim()
    val phone = phoneEditText.text.toString().trim()
    val paxText = paxEditText.text.toString().trim()
    val date = editTextDate.text.toString().trim()
    val timeArrival = editTextTime.text.toString().trim()
    val timeDeparture = editTextTime2.text.toString().trim()
    val table = selectedTable

    // Validate từng trường với thông báo cụ thể
    when {
        name.isEmpty() -> {
            nameEditText.error = "Vui lòng nhập tên khách hàng"
            nameEditText.requestFocus()
            return
        }
        phone.isEmpty() -> {
            phoneEditText.error = "Vui lòng nhập số điện thoại"
            phoneEditText.requestFocus()
            return
        }
        !phone.matches(Regex("^[0-9]{10,11}\$")) -> {
            phoneEditText.error = "Số điện thoại phải có 10-11 chữ số"
            phoneEditText.requestFocus()
            return
        }
        paxText.isEmpty() -> {
            paxEditText.error = "Vui lòng nhập số lượng khách"
            paxEditText.requestFocus()
            return
        }
        paxText.toIntOrNull() == null -> {
            paxEditText.error = "Số lượng khách phải là số"
            paxEditText.requestFocus()
            return
        }
        paxText.toInt() <= 0 -> {
            paxEditText.error = "Số lượng khách phải lớn hơn 0"
            paxEditText.requestFocus()
            return
        }
        date.isEmpty() -> {
            editTextDate.error = "Vui lòng chọn ngày đặt bàn"
            editTextDate.requestFocus()
            return
        }
        isDateInPast(date) -> {
            editTextDate.error = "Không thể đặt bàn trong quá khứ"
            editTextDate.requestFocus()
            return
        }
        timeArrival.isEmpty() -> {
            Toast.makeText(this, "Vui lòng chọn giờ đến", Toast.LENGTH_SHORT).show()
            editTextTime.performClick()
            return
        }
        timeDeparture.isEmpty() -> {
            Toast.makeText(this, "Vui lòng chọn giờ kết thúc", Toast.LENGTH_SHORT).show()
            editTextTime2.performClick()
            return
        }
        !isTimeValid(timeArrival, timeDeparture) -> {
            Toast.makeText(this, "Giờ kết thúc phải sau giờ bắt đầu ít nhất 1 tiếng", Toast.LENGTH_LONG).show()
            editTextTime2.performClick()
            return
        }
        table.isEmpty() -> {
            Toast.makeText(this, "Vui lòng chọn bàn trước khi đặt", Toast.LENGTH_SHORT).show()
            return
        }
    }

    val pax = paxText.toInt()
    val id = reservationId ?: database.push().key ?: UUID.randomUUID().toString()

    val reservation = Reservation(
        id = id,
        name = name,
        phone = phone,
        pax = pax,
        date = date,
        timeArrival = timeArrival,
        timeDeparture = timeDeparture,
        table = table,
        status = if (isEditMode) intent.getStringExtra("status") ?: "Chưa đến" else "Chưa đến"
    )

    // Hiển thị progress dialog trong khi lưu
    val progressDialog = ProgressDialog(this).apply {
        setMessage(if (isEditMode) "Đang cập nhật đặt bàn..." else "Đang đặt bàn...")
        setCancelable(false)
        show()
    }

    database.child(id).setValue(reservation)
        .addOnSuccessListener {
            progressDialog.dismiss()
            val message = if (isEditMode) "Cập nhật đặt bàn thành công!" else "Đặt bàn thành công!"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            // Hiển thị thông báo chi tiết
            AlertDialog.Builder(this)
                .setTitle("Thông tin đặt bàn")
                .setMessage("""
                    Tên: $name
                    SĐT: $phone
                    Số khách: $pax
                    Ngày: $date
                    Giờ đến: $timeArrival
                    Giờ kết thúc: $timeDeparture
                    Bàn: $table
                """.trimIndent())
                .setPositiveButton("OK") { _, _ -> finish() }
                .show()
        }
        .addOnFailureListener { e ->
            progressDialog.dismiss()
            AlertDialog.Builder(this)
                .setTitle("Lỗi")
                .setMessage("Không thể lưu đặt bàn: ${e.localizedMessage}")
                .setPositiveButton("OK", null)
                .show()
            Log.e("AddActivity", "Error saving reservation", e)
        }
}

    // Thêm hàm kiểm tra ngày trong quá khứ
    private fun isDateInPast(selectedDate: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = sdf.parse(selectedDate)
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
            date.before(today)
        } catch (e: Exception) {
            false
        }
    }

    // Thêm hàm kiểm tra thời gian hợp lệ
    private fun isTimeValid(timeArrival: String, timeDeparture: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val arrival = sdf.parse(timeArrival)
            val departure = sdf.parse(timeDeparture)
            val diffInMillis = departure.time - arrival.time
            val diffInHours = diffInMillis / (60 * 60 * 1000)
            diffInHours >= 1
        } catch (e: Exception) {
            false
        }
    }



//    private fun isDateInPast(selectedDate: String): Boolean {
//        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//        val date = sdf.parse(selectedDate)
//        val today = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 0)
//            set(Calendar.MINUTE, 0)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//        }.time
//
//        return date?.before(today) ?: true
//    }

//    private fun isTimeValid(timeArrival: String, timeDeparture: String): Boolean {
//        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
//        val arrival = sdf.parse(timeArrival)
//        val departure = sdf.parse(timeDeparture)
//
//        // Kiểm tra giờ kết thúc phải sau giờ bắt đầu ít nhất 1 giờ
//        val diffInMillis = departure.time - arrival.time
//        val diffInHours = diffInMillis / (60 * 60 * 1000)
//
//        return diffInHours >= 1
//    }

    private fun setupDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }

            // Kiểm tra ngày không được trong quá khứ
            if (selectedCalendar.before(Calendar.getInstance())) {
                Toast.makeText(this, "Không thể chọn ngày trong quá khứ", Toast.LENGTH_SHORT).show()
                return@OnDateSetListener
            }

            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        editTextDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            // Set min date là ngày hiện tại
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }
    }

    private fun setupTimePickers() {
        editTextTime.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    selectedHourArrival = hourOfDay
                    selectedMinuteArrival = minute
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                    editTextTime.text = formattedTime

                    // Reset giờ kết thúc khi thay đổi giờ bắt đầu
                    editTextTime2.text = ""
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        editTextTime2.setOnClickListener {
            if (editTextTime.text.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn giờ bắt đầu trước", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    // Kiểm tra giờ kết thúc phải sau giờ bắt đầu
                    if (hourOfDay < selectedHourArrival ||
                        (hourOfDay == selectedHourArrival && minute <= selectedMinuteArrival)) {
                        Toast.makeText(this, "Giờ kết thúc phải sau giờ bắt đầu", Toast.LENGTH_SHORT).show()
                        return@TimePickerDialog
                    }

                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                    editTextTime2.text = formattedTime
                },
                // Set giờ mặc định là giờ bắt đầu + 1
                if (selectedHourArrival == -1) calendar.get(Calendar.HOUR_OF_DAY) else selectedHourArrival,
                if (selectedMinuteArrival == -1) calendar.get(Calendar.MINUTE) else selectedMinuteArrival,
                true
            ).show()
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        editTextDate.setText(sdf.format(calendar.time))
    }

    //    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 1001 && resultCode == RESULT_OK) {
//            val selectedTable = data?.getStringExtra("selected_table")
//            textSelectedTable.text = selectedTable ?: "Chưa chọn bàn"
//        }
//    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val selectedTable = data?.getStringExtra("selected_table")
            val selectedArea = data?.getStringExtra("selected_area")
            textSelectedTable.text = "Khu: $selectedArea, Bàn: $selectedTable"
            this.selectedTable = "$selectedArea - $selectedTable" // Lưu cả khu và bàn vào biến toàn cục
        }
    }

    private fun handleIntentData() {
        isEditMode = intent.getBooleanExtra("is_edit_mode", false)
        if (isEditMode) {
            reservationId = intent.getStringExtra("reservation_id")
            nameEditText.setText(intent.getStringExtra("name"))
            phoneEditText.setText(intent.getStringExtra("phone"))
            paxEditText.setText(intent.getIntExtra("pax", 0).toString())
            editTextDate.setText(intent.getStringExtra("date"))
            editTextTime.text = intent.getStringExtra("timeArrival")
            editTextTime2.text = intent.getStringExtra("timeDeparture")

            val table = intent.getStringExtra("table")
            if (!table.isNullOrEmpty()) {
                textSelectedTable.text = table
                selectedTable = table
            }

            // Lưu giờ đến để validate
            val timeArrival = intent.getStringExtra("timeArrival") ?: ""
            if (timeArrival.isNotEmpty()) {
                val parts = timeArrival.split(":")
                if (parts.size == 2) {
                    selectedHourArrival = parts[0].toInt()
                    selectedMinuteArrival = parts[1].toInt()
                }
            }
        }
    }

}