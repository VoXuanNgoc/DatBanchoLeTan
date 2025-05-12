package com.example.datban

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_table)

        listView = findViewById(R.id.listViewTables)
        progressBar = findViewById(R.id.progressBar)
        tables = mutableListOf()

        adapter = TableAdapter(this, tables, reservedTables)
        listView.adapter = adapter

        selectedDate = intent.getStringExtra("selected_date") ?: ""

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
            val table = tables[position]
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
            tables.clear()
            Log.d("SelectTable", "Total tables from Firebase: ${tableSnapshot.childrenCount}")

            for (child in tableSnapshot.children) {
                val table = child.getValue(TableModel::class.java)
                table?.let {
                    Log.d("SelectTable", "Loaded table: ${it.soBan}")
                    tables.add(it.copy(id = child.key ?: ""))
                }
            }
            Log.d("SelectTable", "Tables count: ${tables.size}")
            adapter.notifyDataSetChanged()
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
}