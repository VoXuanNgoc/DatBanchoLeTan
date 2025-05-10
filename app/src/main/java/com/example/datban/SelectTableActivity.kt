package com.example.datban

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.datban.model.TableModel
import com.google.firebase.database.FirebaseDatabase

class SelectTableActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var tables: MutableList<TableModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_table)

        listView = findViewById(R.id.listViewTables)
        tables = mutableListOf()

        val adapter = TableAdapter(this, tables)
        listView.adapter = adapter

        val dbRef = FirebaseDatabase.getInstance().getReference("tables")
        dbRef.get().addOnSuccessListener { snapshot ->
            snapshot.children.forEach {
                val table = it.getValue(TableModel::class.java)
                if (table != null) {
                    tables.add(table)
                }
            }
            adapter.notifyDataSetChanged()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val table = tables[position]
            val resultIntent = Intent()
            resultIntent.putExtra("selected_table", "${table.khu} -  ${table.soBan}")
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        listView.setOnItemClickListener { _, view, position, _ ->
            view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
                .withEndAction {
                    view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    val table = tables[position]
                    val resultIntent = Intent()
                    resultIntent.putExtra("selected_table", "${table.khu} -  ${table.soBan}")
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }.start()
        }
    }
}