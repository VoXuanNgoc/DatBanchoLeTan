//
//package com.example.datban
//
//import android.graphics.Typeface
//import android.os.Bundle
//import android.text.Spannable
//import android.text.SpannableStringBuilder
//import android.text.style.StyleSpan
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import com.example.datban.model.Reservation
//import com.example.datban.model.TableModel
//import com.google.firebase.database.*
//import java.text.SimpleDateFormat
//import java.util.*
//
//class LienheFragment : Fragment() {
//
//    private lateinit var tvTotalStats: TextView
//    private lateinit var tvTodayStats: TextView
//    private lateinit var tvZoneStats: TextView
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_lienhe, container, false)
//
//        tvTotalStats = view.findViewById(R.id.tvTotalStats)
//        tvTodayStats = view.findViewById(R.id.tvTodayStats)
//        tvZoneStats = view.findViewById(R.id.tvZoneStats)
//
//        loadTableStatistics()
//
//        return view
//    }
//
//    private fun loadTableStatistics() {
//        val database = FirebaseDatabase.getInstance()
//        val tablesRef = database.getReference("tables")
//        val reservationsRef = database.getReference("reservations")
//
//        tablesRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(tablesSnapshot: DataSnapshot) {
//                val allTables = mutableListOf<TableModel>()
//                val zones = mutableSetOf<String>()
//                var totalChairs = 0
//
//                for (tableSnapshot in tablesSnapshot.children) {
//                    val table = tableSnapshot.getValue(TableModel::class.java)
//                    table?.let {
//                        allTables.add(it)
//                        zones.add(it.khu)
//                        totalChairs += it.soGhe
//                    }
//                }
//
//                val totalStats = """
//                    📋 Tổng quan:
//                    - Khu vực: ${zones.size} khu
//                    - Bàn: ${allTables.size} bàn
//                    - Số ghế: $totalChairs ghế
//                """.trimIndent()
//                tvTotalStats.text = totalStats
//
//                val todayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//                val today = todayFormat.format(Date())
//
//                reservationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(reservationsSnapshot: DataSnapshot) {
//                        val reservedTablesToday = mutableSetOf<String>()
//
//                        for (reservationSnapshot in reservationsSnapshot.children) {
//                            val reservation = reservationSnapshot.getValue(Reservation::class.java)
//                            reservation?.let {
//                                if (it.date.trim() == today) {
//                                    reservedTablesToday.add(it.table)
//                                }
//                            }
//                        }
//
//                        val todayStats = """
//                            📅 Hôm nay ($today):
//                            - Tổng bàn: ${allTables.size} bàn
//                            - Bàn đã đặt: ${reservedTablesToday.size} bàn
//                        """.trimIndent()
//                        tvTodayStats.text = todayStats
//
//                        val zoneInfoMap = mutableMapOf<String, ZoneInfo>()
//                        zones.forEach { zone ->
//                            zoneInfoMap[zone] = ZoneInfo(0, 0, 0)
//                        }
//
//                        allTables.forEach { table ->
//                            zoneInfoMap[table.khu]?.apply {
//                                totalTables++
//                                totalChairs += table.soGhe
//                            }
//                        }
//
//                        reservedTablesToday.forEach { reserved ->
//                            val zone = reserved.substringBefore(" -") // "Khu A"
//                            zoneInfoMap[zone]?.apply {
//                                reservedTables++
//                            }
//                        }
//
//                        val spannableZoneStats = SpannableStringBuilder()
//                        spannableZoneStats.append("📊 Thống kê theo khu vực:\n\n")
//
//                        zoneInfoMap.forEach { (zone, info) ->
//                            val zoneTitle = "- $zone"
//                            val zoneDetail = ": ${info.totalTables} bàn (${info.reservedTables} đã đặt), ${info.totalChairs} ghế\n"
//                            val start = spannableZoneStats.length
//
//                            spannableZoneStats.append(zoneTitle)
//                            spannableZoneStats.append(zoneDetail)
//
//                            spannableZoneStats.setSpan(
//                                StyleSpan(Typeface.BOLD),
//                                start,
//                                start + zoneTitle.length,
//                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                            )
//                        }
//
//                        tvZoneStats.setText(spannableZoneStats, TextView.BufferType.SPANNABLE)
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        tvTodayStats.text = "Lỗi tải đặt bàn: ${error.message}"
//                    }
//                })
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                tvTotalStats.text = "Lỗi tải danh sách bàn: ${error.message}"
//            }
//        })
//    }
//
//    private data class ZoneInfo(
//        var totalTables: Int,
//        var reservedTables: Int,
//        var totalChairs: Int
//    )
//}
package com.example.datban

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.datban.model.Reservation
import com.example.datban.model.TableModel
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class LienheFragment : Fragment() {

    private lateinit var tvTotalStats: TextView
    private lateinit var tvTodayStats: TextView
    private lateinit var tvZoneStats: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lienhe, container, false)
        tvTotalStats = view.findViewById(R.id.tvTotalStats)
        tvTodayStats = view.findViewById(R.id.tvTodayStats)
        tvZoneStats = view.findViewById(R.id.tvZoneStats)
        loadTableStatistics()
        return view
    }

    private fun loadTableStatistics() {
        val database = FirebaseDatabase.getInstance()
        val tablesRef = database.getReference("tables")
        val reservationsRef = database.getReference("reservations")

        tablesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(tablesSnapshot: DataSnapshot) {
                val allTables = mutableListOf<TableModel>()
                val zones = mutableSetOf<String>()
                var totalChairs = 0

                for (tableSnapshot in tablesSnapshot.children) {
                    val table = tableSnapshot.getValue(TableModel::class.java)
                    table?.let {
                        allTables.add(it)
                        zones.add(it.khu)
                        totalChairs += it.soGhe
                    }
                }

                val spannableTotal = SpannableStringBuilder()
                spannableTotal.append("📋 Tổng quan:\n")
                spannableTotal.append("- Khu vực: ${zones.size} khu\n")
                spannableTotal.append("- Bàn: ${allTables.size} bàn\n")

                val chairStart = spannableTotal.length
                spannableTotal.append("- Số ghế: $totalChairs ghế\n")
                spannableTotal.setSpan(ForegroundColorSpan(Color.parseColor("#1976D2")), chairStart, spannableTotal.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                tvTotalStats.setText(spannableTotal, TextView.BufferType.SPANNABLE)

                val todayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val today = todayFormat.format(Date())

                reservationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(reservationsSnapshot: DataSnapshot) {
                        val reservedTablesToday = mutableSetOf<String>()

                        for (reservationSnapshot in reservationsSnapshot.children) {
                            val reservation = reservationSnapshot.getValue(Reservation::class.java)
                            reservation?.let {
                                if (it.date.trim() == today) {
                                    reservedTablesToday.add(it.table)
                                }
                            }
                        }

                        val spannableToday = SpannableStringBuilder()
                        spannableToday.append("📅 Hôm nay ($today):\n")
                        spannableToday.append("- Tổng bàn: ${allTables.size} bàn\n")

                        val redStart = spannableToday.length
                        spannableToday.append("- Bàn đã đặt: ${reservedTablesToday.size} bàn\n")
                        spannableToday.setSpan(ForegroundColorSpan(Color.RED), redStart, spannableToday.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        tvTodayStats.setText(spannableToday, TextView.BufferType.SPANNABLE)

                        val zoneInfoMap = mutableMapOf<String, ZoneInfo>()
                        zones.forEach { zone -> zoneInfoMap[zone] = ZoneInfo(0, 0, 0) }

                        allTables.forEach { table ->
                            zoneInfoMap[table.khu]?.apply {
                                totalTables++
                                totalChairs += table.soGhe
                            }
                        }

                        reservedTablesToday.forEach { reserved ->
                            val zone = reserved.substringBefore(" -")
                            zoneInfoMap[zone]?.reservedTables = zoneInfoMap[zone]?.reservedTables?.plus(1) ?: 0
                        }

                        val spannableZoneStats = SpannableStringBuilder()
                        spannableZoneStats.append("📊 Thống kê theo khu vực:\n\n")

                        zoneInfoMap.forEach { (zone, info) ->
                            val start = spannableZoneStats.length
                            spannableZoneStats.append("📍 $zone\n")

                            spannableZoneStats.setSpan(StyleSpan(Typeface.BOLD), start, spannableZoneStats.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                            spannableZoneStats.append("🍽️ Bàn: ${info.totalTables} ")

                            val reservedStart = spannableZoneStats.length
                            spannableZoneStats.append("(${info.reservedTables} đã đặt)\n")
                            spannableZoneStats.setSpan(ForegroundColorSpan(Color.RED), reservedStart, spannableZoneStats.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                            val chairStart = spannableZoneStats.length
                            spannableZoneStats.append("🪑 Ghế: ${info.totalChairs}\n\n")
                            spannableZoneStats.setSpan(ForegroundColorSpan(Color.parseColor("#388E3C")), chairStart, spannableZoneStats.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }

                        tvZoneStats.setText(spannableZoneStats, TextView.BufferType.SPANNABLE)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        tvTodayStats.text = "Lỗi tải đặt bàn: ${error.message}"
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                tvTotalStats.text = "Lỗi tải danh sách bàn: ${error.message}"
            }
        })
    }

    private data class ZoneInfo(
        var totalTables: Int,
        var reservedTables: Int,
        var totalChairs: Int
    )
}
