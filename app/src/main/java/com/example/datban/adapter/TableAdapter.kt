package com.example.datban

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.datban.R
import com.example.datban.model.TableModel

class TableAdapter(
    context: Context,
//    private val tables: List<TableModel>,
    private val tables: MutableList<TableModel>,  // ⚠️ Đã sửa List -> MutableList

    private val reservedTables: Set<String>
) : ArrayAdapter<TableModel>(context, 0, tables) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_table,
            parent,
            false
        )

        val currentTable = getItem(position) ?: return itemView
//        val isReserved = reservedTables.contains(currentTable.id)
        val tableKey = "${currentTable.khu} - ${currentTable.soBan}"
        val isReserved = reservedTables.contains(tableKey)

        val tvArea = itemView.findViewById<TextView>(R.id.tvArea)
        val tvTableNumber = itemView.findViewById<TextView>(R.id.tvTableNumber)
        val tvSeats = itemView.findViewById<TextView>(R.id.tvSeats)

        tvArea.text = currentTable.khu
        tvTableNumber.text = currentTable.soBan
        tvSeats.text = "${currentTable.soGhe} ghế"
        val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
        val cardView = itemView.findViewById<CardView>(R.id.cardView)
//        tvStatus.text = if (isReserved) "Đã đặt" else "Còn trống"
//        tvStatus.setTextColor(
//            ContextCompat.getColor(
//                context,
//                if (isReserved) R.color.cam else R.color.xanh
//            )
//        )

        // Đặt màu và trạng thái dựa trên tình trạng đặt bàn
        if (isReserved) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cam))
            tvStatus.text = "Đã đặt"
            tvStatus.setTextColor(ContextCompat.getColor(context, R.color.xanh))
        } else {
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.xanh))
            tvStatus.text = "Còn trống"
            tvStatus.setTextColor(ContextCompat.getColor(context, R.color.cam))
        }

        return itemView



    }
    fun updateData(newTables: List<TableModel>) {
        this.tables.clear()
        this.tables.addAll(newTables)
        notifyDataSetChanged()
    }

}
//package com.example.datban
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.TextView
//import androidx.cardview.widget.CardView
//import androidx.core.content.ContextCompat
//import com.example.datban.R
//import com.example.datban.model.TableModel
//
//class TableAdapter(
//    context: Context,
//    private val tables: List<TableModel>,
//    private val reservedTables: Set<String>
//) : ArrayAdapter<TableModel>(context, 0, tables) {
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val itemView = convertView ?: LayoutInflater.from(context).inflate(
//            R.layout.item_table,
//            parent,
//            false
//        )
//
//        val currentTable = getItem(position) ?: return itemView
//        val isReserved = reservedTables.contains(currentTable.id)
//
//        val tvArea = itemView.findViewById<TextView>(R.id.tvArea)
//        val tvTableNumber = itemView.findViewById<TextView>(R.id.tvTableNumber)
//        val tvSeats = itemView.findViewById<TextView>(R.id.tvSeats)
//        val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
//        val cardView = itemView.findViewById<CardView>(R.id.cardView)
//
//        tvArea.text = currentTable.khu
//        tvTableNumber.text = currentTable.soBan
//        tvSeats.text = "${currentTable.soGhe} ghế"
//
//        // Đặt màu và trạng thái dựa trên tình trạng đặt bàn
//        if (isReserved) {
//            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cam))
//            tvStatus.text = "Đã đặt"
//            tvStatus.setTextColor(ContextCompat.getColor(context, R.color.cam))
//        } else {
//            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.xanh))
//            tvStatus.text = "Còn trống"
//            tvStatus.setTextColor(ContextCompat.getColor(context, R.color.xanh))
//        }
//
//        return itemView
//    }
//}
