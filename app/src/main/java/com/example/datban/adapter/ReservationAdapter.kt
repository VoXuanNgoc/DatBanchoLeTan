package com.example.datban

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.datban.model.Reservation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class ReservationAdapter(
    private var reservations: List<Reservation>,
    private val onStatusChanged: (Reservation, String) -> Unit
) : RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {

    private val statusOptions = listOf("Chưa đến", "Đã nhận bàn", "Hoàn thành", "Đã hủy")

    inner class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tenbook)
        private val paxTextView: TextView = itemView.findViewById(R.id.paxbook)
        private val timeTextView: TextView = itemView.findViewById(R.id.gioden)
        private val tableTextView: TextView = itemView.findViewById(R.id.banbook)
        private val timeLeftTextView: TextView = itemView.findViewById(R.id.baothoigian)
        private val statusSpinner: Spinner = itemView.findViewById(R.id.statusbook)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)

        fun bind(reservation: Reservation) {
            nameTextView.text = reservation.name
            paxTextView.text = "${reservation.pax} Pax"
            timeTextView.text = reservation.timeArrival
            tableTextView.text = reservation.table

            // Calculate time left (simplified)
            timeLeftTextView.text = when (reservation.status) {
                "Chưa đến" -> "Chưa đến"
                "Đã nhận bàn" -> "Đang phục vụ"
                "Hoàn thành" -> "Hoàn thành"
                "Đã hủy" -> "Đã hủy"
                else -> ""
            }
            timeLeftTextView.text = calculateTimeStatus(reservation.timeArrival)


            // Setup spinner
            val adapter = ArrayAdapter(
                itemView.context,
                android.R.layout.simple_spinner_item,
                statusOptions
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            statusSpinner.adapter = adapter

            // Set current status position
            val statusPosition = statusOptions.indexOf(reservation.status)
            if (statusPosition >= 0) {
                statusSpinner.setSelection(statusPosition)
            }

            // Handle status change
            statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val newStatus = statusOptions[position]
                    if (newStatus != reservation.status) {
                        onStatusChanged(reservation, newStatus)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            btnEdit.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, AddActivity::class.java).apply {
                    putExtra("reservation_id", reservation.id)
                    putExtra("name", reservation.name)
                    putExtra("phone", reservation.phone)
                    putExtra("pax", reservation.pax)
                    putExtra("date", reservation.date)
                    putExtra("timeArrival", reservation.timeArrival)
                    putExtra("timeDeparture", reservation.timeDeparture)
                    putExtra("table", reservation.table)
                    putExtra("status", reservation.status)
                    putExtra("is_edit_mode", true)
                }
                context.startActivity(intent)
            }
        }
        }


    fun updateData(newReservations: List<Reservation>) {
        reservations = newReservations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return ReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        holder.bind(reservations[position])
    }
    private fun calculateTimeStatus(arrivalTime: String): String {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
                timeZone = TimeZone.getDefault()
            }

            val now = Calendar.getInstance()
            val arrival = sdf.parse(arrivalTime) ?: return ""

            // Tính thời gian hiện tại dạng HH:mm
            val currentTime = sdf.format(now.time)
            val current = sdf.parse(currentTime) ?: return ""

            // Tính chênh lệch (milliseconds)
            val diff = arrival.time - current.time

            when {
                // Trường hợp chưa đến giờ
                diff > 3600000 -> {
                    val hours = TimeUnit.MILLISECONDS.toHours(diff)
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
                    when {
                        minutes > 0 -> "Còn $hours tiếng $minutes phút"
                        else -> "Còn $hours tiếng"
                    }
                }
                diff > 0 -> "Còn ${TimeUnit.MILLISECONDS.toMinutes(diff)} phút"

                // Trường hợp đã qua giờ hẹn
                diff > -600000 -> "Vừa đến"
                else -> {
                    val lateMinutes = TimeUnit.MILLISECONDS.toMinutes(-diff)
                    when {
                        lateMinutes >= 1050 -> {  // 1050 phút = 17.5 tiếng
                            val hours = lateMinutes / 60
                            val mins = lateMinutes % 60
                            when {
                                mins > 0 -> "Trễ $hours tiếng $mins phút"
                                else -> "Trễ $hours tiếng"
                            }
                        }
                        lateMinutes >= 60 -> {
                            val hours = lateMinutes / 60
                            val mins = lateMinutes % 60
                            "Trễ $hours tiếng $mins phút"
                        }
                        else -> "Trễ $lateMinutes phút"
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TimeCalc", "Lỗi tính thời gian: ${e.message}")
            ""
        }
    }
    override fun getItemCount(): Int = reservations.size
}