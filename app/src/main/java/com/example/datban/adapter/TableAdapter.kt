package com.example.datban

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.datban.model.TableModel

class TableAdapter(context: Context, private val tables: List<TableModel>)
    : ArrayAdapter<TableModel>(context, 0, tables) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(
                R.layout.item_table,
                parent,
                false
            )
        }

        val currentTable = tables[position]

        val tvArea = itemView!!.findViewById<TextView>(R.id.tvArea)
        val tvTableNumber = itemView.findViewById<TextView>(R.id.tvTableNumber)
        val tvSeats = itemView.findViewById<TextView>(R.id.tvSeats)

        tvArea.text = currentTable.khu
        tvTableNumber.text = "${currentTable.soBan}"
        tvSeats.text = "${currentTable.soGhe} ghế"

        return itemView
    }
}