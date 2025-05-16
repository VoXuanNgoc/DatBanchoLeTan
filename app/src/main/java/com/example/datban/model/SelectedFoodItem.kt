// SelectedFoodItem.kt
package com.example.datban.model

data class SelectedFoodItem(
    val id: String = "",
    val foodName: String = "",
    val quantity: Int = 1,
    val price: Double = 0.0,
    val pax: Int = 1
)