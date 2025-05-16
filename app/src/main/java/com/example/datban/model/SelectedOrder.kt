package com.example.datban.model
import com.example.datban.model.SelectedFoodItem

data class SelectedOrder(
    val customerName: String = "",
    val pax: Int = 1,
    val items: List<SelectedFoodItem> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)
