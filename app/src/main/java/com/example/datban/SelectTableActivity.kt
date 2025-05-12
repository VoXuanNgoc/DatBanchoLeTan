package com.example.datban

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.datban.model.TableModel
import com.google.firebase.database.FirebaseDatabase

class SelectTableActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var progressBar: ProgressBar
    private lateinit var tables: MutableList<TableModel>
    private val reservedTables = mutableSetOf<String>()
    private lateinit var selectedDate: String
    private lateinit var adapter: TableAdapter
    private var selectedTable: TableModel? = null // Đã khai báo đúng kiểu
    private var allTables: MutableList<TableModel> = mutableListOf()

    //
    private lateinit var khuButtonLayout: LinearLayout
    private var filteredTables: MutableList<TableModel> = mutableListOf()
    private var selectedKhuButton: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_table)

        listView = findViewById(R.id.listViewTables)
        progressBar = findViewById(R.id.progressBar)
        tables = mutableListOf()

        adapter = TableAdapter(this, tables, reservedTables)
        listView.adapter = adapter

        selectedDate = intent.getStringExtra("selected_date") ?: ""

        khuButtonLayout = findViewById(R.id.layoutKhuButtons)

        loadTableData()

        // Xử lý chọn bàn
//        listView.setOnItemClickListener { _, _, position, _ ->
//            val table = tables[position]
//            if (!reservedTables.contains(table.id)) {
//                selectedTable = table
//                Toast.makeText(this, "Đã chọn bàn ${table.soBan}", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Bàn này đã được đặt!", Toast.LENGTH_SHORT).show()
//            }
//        }
        listView.setOnItemClickListener { _, _, position, _ ->
            val table = filteredTables[position]
            val tableKey = "${table.khu} - ${table.soBan}"

            if (!reservedTables.contains(tableKey)) {
                selectedTable = table
                Toast.makeText(this, "Đã chọn bàn ${table.soBan}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Bàn này đã được đặt!", Toast.LENGTH_SHORT).show()
            }
        }


        // Đảm bảo rằng bạn gửi bàn đã chọn
        findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            if (selectedTable != null) {
                val resultIntent = Intent().apply {
                    putExtra("selected_table", selectedTable!!.soBan) // Bàn đã chọn
                    putExtra("selected_area", selectedTable!!.khu)    // Khu vực của bàn
                }
                setResult(RESULT_OK, resultIntent)
                finish()


            } else {
                Toast.makeText(this, "Vui lòng chọn một bàn!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun loadTableData() {
        showLoading(true)

        val reservationRef = FirebaseDatabase.getInstance().getReference("reservations")
        reservationRef.orderByChild("date").equalTo(selectedDate).get()
            .addOnSuccessListener { reservationSnapshot ->
                reservedTables.clear()
                for (child in reservationSnapshot.children) {
                    val reservedTable = child.child("table").getValue(String::class.java)
                    reservedTable?.let { reservedTables.add(it) }


                }

                loadAllTables()
            }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(this, "Lỗi tải đặt bàn: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadAllTables() {
        val tableRef = FirebaseDatabase.getInstance().getReference("tables")
        tableRef.get().addOnSuccessListener { tableSnapshot ->
 //           tables.clear()
//            allTables.clear() // <- Thêm dòng này

            Log.d("SelectTable", "Total tables from Firebase: ${tableSnapshot.childrenCount}")

//            for (child in tableSnapshot.children) {
//                val table = child.getValue(TableModel::class.java)
//                table?.let {
//                    Log.d("SelectTable", "Loaded table: ${it.soBan}")
//                    tables.add(it.copy(id = child.key ?: ""))
//
//                }
//            }
            tables.clear()
            allTables.clear() // <- Thêm dòng này

            for (child in tableSnapshot.children) {
                val table = child.getValue(TableModel::class.java)
                table?.let {
                    val tableWithId = it.copy(id = child.key ?: "")
                    tables.add(tableWithId)
                    allTables.add(tableWithId) // <- Lưu bản gốc
                }
            }

            Log.d("SelectTable", "Tables count: ${tables.size}")
            adapter.notifyDataSetChanged()
            filteredTables = tables.toMutableList()
            adapter.updateData(filteredTables)
            setupKhuButtons()

            showLoading(false)
        }
            .addOnFailureListener {
                Log.e("SelectTable", "Error loading tables", it)
                showLoading(false)
                Toast.makeText(this, "Lỗi tải danh sách bàn: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        listView.visibility = if (show) View.GONE else View.VISIBLE
    }
//    private fun setupKhuButtons() {
//        khuButtonLayout.removeAllViews()
//
//        val khuSet = allTables.map { it.khu }.distinct().sorted()
//
//// Nút "Tất cả"
//        val allButton = createKhuButton("Tất cả")
//        allButton.setOnClickListener {
//            filteredTables = allTables.toMutableList() // <- Sửa từ tables -> allTables
//            adapter.updateData(filteredTables)
//        }
//        khuButtonLayout.addView(allButton)
//
//        for (khu in khuSet) {
//            val button = createKhuButton(khu)
//            button.setOnClickListener {
//                filteredTables = allTables.filter { it.khu == khu }.toMutableList() // <- sửa
//                adapter.updateData(filteredTables)
//            }
//            khuButtonLayout.addView(button)
//        }
//
//    }
private fun setupKhuButtons() {
    khuButtonLayout.removeAllViews()

    val khuSet = allTables.map { it.khu }.distinct().sorted()

    // Nút "Tất cả"
    val allButton = createKhuButton("Tất cả")
    allButton.setOnClickListener {
        filteredTables = allTables.toMutableList()
        adapter.updateData(filteredTables)
        updateSelectedButton(allButton)
    }
    khuButtonLayout.addView(allButton)

    for (khu in khuSet) {
        val button = createKhuButton(khu)
        button.setOnClickListener {
            filteredTables = allTables.filter { it.khu == khu }.toMutableList()
            adapter.updateData(filteredTables)
            updateSelectedButton(button)
        }
        khuButtonLayout.addView(button)
    }

    // Mặc định chọn "Tất cả"
    updateSelectedButton(allButton)
}
    private fun updateSelectedButton(newSelected: Button) {

        // Reset nút trước (nếu có)
        selectedKhuButton?.let {
            it.setBackgroundResource(R.drawable.bg_khu_button)
           // it.setBackgroundColor(resources.getColor(R.color.cam, null))
            it.setTextColor(resources.getColor(R.color.white, null))
        }

        // Cập nhật nút mới
        newSelected.setBackgroundResource(R.drawable.bg_khu_button2) // Vuông khi chọn

       // newSelected.setBackgroundColor(resources.getColor(R.color.xanh, null))
        newSelected.setTextColor(resources.getColor(R.color.white, null))
        selectedKhuButton = newSelected
    }



    private fun createKhuButton(khu: String): Button {
        val button = Button(this).apply {
            text = khu
            setPadding(40, 10, 40, 10)
            setBackgroundColor(resources.getColor(R.color.cam, null))
            setTextColor(resources.getColor(R.color.white, null))
            textSize = 14f

            // Bo tròn và margin
            background = ContextCompat.getDrawable(context, R.drawable.bg_khu_button)
        }

        // Set margin (LayoutParams)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(16, 8, 16, 8)
        }

        button.layoutParams = params

        return button
    }



}