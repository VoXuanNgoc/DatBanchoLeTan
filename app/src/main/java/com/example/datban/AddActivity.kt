package com.example.datban

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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

    }

    //    private fun saveReservation() {
//        // Lấy dữ liệu từ các trường nhập liệu
//        val name = nameEditText.text.toString().trim()
//        val phone = phoneEditText.text.toString().trim()
//        val pax = paxEditText.text.toString().trim()
//        val date = editTextDate.text.toString().trim()
//        val timeArrival = editTextTime.text.toString().trim()
//        val timeDeparture = editTextTime2.text.toString().trim()
//        val table = textSelectedTable.text.toString().trim()
//
//        // Kiểm tra các trường bắt buộc
//        if (name.isEmpty() || phone.isEmpty() || pax.isEmpty() || date.isEmpty() ||
//            timeArrival.isEmpty() || timeDeparture.isEmpty() || table == "Chưa chọn bàn") {
//            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Kiểm tra thời gian
//        if (!isTimeValid(timeArrival, timeDeparture)) {
//            Toast.makeText(this, "Giờ kết thúc phải sau giờ bắt đầu ít nhất 1 giờ", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Kiểm tra ngày không được trong quá khứ
//        if (isDateInPast(date)) {
//            Toast.makeText(this, "Không thể đặt bàn trong quá khứ", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Tạo ID ngẫu nhiên cho reservation
//        val id = UUID.randomUUID().toString()
//
//        // Tạo đối tượng Reservation
//        val reservation = Reservation(
//            id = id,
//            name = name,
//            phone = phone,
//            pax = pax.toInt(),
//            date = date,
//            timeArrival = timeArrival,
//            timeDeparture = timeDeparture,
//            table = table,
//            status = "Chưa đến"
//        )
//
//        // Lưu lên Firebase
//        database.child(id).setValue(reservation)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Đặt bàn thành công!", Toast.LENGTH_SHORT).show()
//                finish() // Đóng activity sau khi lưu thành công
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "Đặt bàn thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
    private fun saveReservation() {
        // Lấy dữ liệu từ các trường nhập liệu
        val name = nameEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val pax = paxEditText.text.toString().trim()
        val date = editTextDate.text.toString().trim()
        val timeArrival = editTextTime.text.toString().trim()
        val timeDeparture = editTextTime2.text.toString().trim()

        // Kiểm tra các trường bắt buộc
        if (name.isEmpty() || phone.isEmpty() || pax.isEmpty() || date.isEmpty() ||
            timeArrival.isEmpty() || timeDeparture.isEmpty() || selectedTable.isEmpty() || selectedTable == "Chưa chọn bàn") {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        // Lưu thông tin đặt bàn với bàn đã chọn
        val id = UUID.randomUUID().toString()
        val reservation = Reservation(
            id = id,
            name = name,
            phone = phone,
            pax = pax.toInt(),
            date = date,
            timeArrival = timeArrival,
            timeDeparture = timeDeparture,
            table = selectedTable, // Lưu cả khu và bàn
            status = "Chưa đến"
        )


        database.child(id).setValue(reservation)
            .addOnSuccessListener {
                Toast.makeText(this, "Đặt bàn thành công!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Đặt bàn thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun isDateInPast(selectedDate: String): Boolean {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = sdf.parse(selectedDate)
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return date?.before(today) ?: true
    }

    private fun isTimeValid(timeArrival: String, timeDeparture: String): Boolean {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val arrival = sdf.parse(timeArrival)
        val departure = sdf.parse(timeDeparture)

        // Kiểm tra giờ kết thúc phải sau giờ bắt đầu ít nhất 1 giờ
        val diffInMillis = departure.time - arrival.time
        val diffInHours = diffInMillis / (60 * 60 * 1000)

        return diffInHours >= 1
    }

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



}