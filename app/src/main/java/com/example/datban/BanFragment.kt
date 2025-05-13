package com.example.datban

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.datban.databinding.FragmentBanBinding
import com.example.datban.model.Reservation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BanFragment : Fragment() {
    private var _binding: FragmentBanBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: ReservationAdapter
    private var currentFilter: String = "Tất cả"
    private val todayDate: String by lazy {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance()

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ReservationAdapter(emptyList()) { reservation, newStatus ->
            updateReservationStatus(reservation.id ?: "", newStatus)
        }
        binding.recyclerView.adapter = adapter

        // Setup filter buttons
        setupFilterButtons()

        // Load initial data
        loadReservations()
    }

    private fun setupFilterButtons() {
        val buttons = listOf(
            binding.btnTatCa,
            binding.btnChuaDen,
            binding.btnDaNhan,
            binding.btnHoanThanh,
            binding.btnDaHuy
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                currentFilter = button.text.toString()
                updateButtonStyles(buttons, button)
                loadReservations()
            }
        }

        // Set initial active button
        updateButtonStyles(buttons, binding.btnTatCa)
    }

    private fun updateButtonStyles(buttons: List<Button>, activeButton: Button) {
        buttons.forEach { button ->
            if (button == activeButton) {
                button.setBackgroundResource(R.drawable.active_filter_button)
                button.setTextColor(resources.getColor(R.color.white, null))
            } else {
                button.setBackgroundResource(R.drawable.inactive_filter_button)
                button.setTextColor(resources.getColor(R.color.black, null))
            }
        }
    }

    private fun loadReservations() {
        val reservationsRef = database.getReference("reservations")

        reservationsRef.orderByChild("date").equalTo(todayDate)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reservations = mutableListOf<Reservation>()

                    for (child in snapshot.children) {
                        val reservation = child.getValue(Reservation::class.java)
                        reservation?.let {
                            // Apply filter
                            when (currentFilter) {
                                "Tất cả" -> reservations.add(it.copy(id = child.key))
                                "Chưa đến" -> if (it.status == "Chưa đến") reservations.add(it.copy(id = child.key))
                                "Đã nhận bàn" -> if (it.status == "Đã nhận bàn") reservations.add(it.copy(id = child.key))
                                "Hoàn thành" -> if (it.status == "Hoàn thành") reservations.add(it.copy(id = child.key))
                                "Đã hủy" -> if (it.status == "Đã hủy") reservations.add(it.copy(id = child.key))
                            }
                        }
                    }

                    // Sort by arrival time
                    val sortedReservations = reservations.sortedBy { it.timeArrival }
                    adapter.updateData(sortedReservations)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun updateReservationStatus(reservationId: String, newStatus: String) {
        database.getReference("reservations").child(reservationId)
            .child("status").setValue(newStatus)
            .addOnSuccessListener {
                // Status updated successfully
            }
            .addOnFailureListener {
                // Handle error
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}