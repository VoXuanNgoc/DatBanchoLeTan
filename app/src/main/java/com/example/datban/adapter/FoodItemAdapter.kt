//
//package com.example.datban
//
//import android.content.Context
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.BaseAdapter
//import android.widget.CheckBox
//import android.widget.ImageView
//import android.widget.TextView
//import com.bumptech.glide.Glide
//import com.example.datban.model.FoodItem
//
//class FoodItemAdapter(
//    private val context: Context,
//    private var allItems: List<FoodItem>
//) : BaseAdapter() {
//
//    private var displayedItems: List<FoodItem> = allItems
//    private val TAG = "FoodItemAdapter"
//
//    fun updateList(newList: List<FoodItem>) {
//        // Giữ lại trạng thái quantity từ danh sách cũ
//        allItems = newList.map { newItem ->
//            allItems.find { it.id == newItem.id }?.let { existingItem ->
//                newItem.copy(quantity = existingItem.quantity)
//            } ?: newItem
//        }
//        displayedItems = allItems
//        notifyDataSetChanged()
//        Log.d(TAG, "Danh sách đã cập nhật - Số lượng: ${allItems.size}")
//    }
//
//    override fun getCount(): Int = displayedItems.size
//
//    override fun getItem(position: Int): FoodItem = displayedItems[position]
//
//    override fun getItemId(position: Int): Long = position.toLong()
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        val view = convertView ?: LayoutInflater.from(context)
//            .inflate(R.layout.item_food, parent, false)
//
//        val foodItem = getItem(position)
//
//        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)
//        val tvName = view.findViewById<TextView>(R.id.tvMenuItemName)
//        val tvPrice = view.findViewById<TextView>(R.id.tvMenuItemPrice)
//        val ivImage = view.findViewById<ImageView>(R.id.ivMenuItem)
//
//        tvName.text = foodItem.name
//        tvPrice.text = "Giá: ${String.format("%,.0f", foodItem.price)} VND"
//
//        // Xử lý trạng thái checkbox
//        checkBox.setOnCheckedChangeListener(null)
//        checkBox.isChecked = foodItem.quantity > 0
//        checkBox.setOnCheckedChangeListener { _, isChecked ->
//            foodItem.quantity = if (isChecked) 1 else 0
//            Log.d(TAG, "Món ${foodItem.name} - Số lượng: ${foodItem.quantity}")
//        }
//
//        // Load ảnh bằng Glide
//        Glide.with(context)
//            .load(foodItem.imageUrl)
//            .placeholder(R.drawable.ic_food_placeholder)
//            .error(R.drawable.ic_placeholder)
//            .into(ivImage)
//
//        return view
//    }
//
//    fun getSelectedItems(): List<FoodItem> {
//        val selected = allItems.filter { it.quantity > 0 }
//        Log.d(TAG, "Số món đã chọn: ${selected.size}")
//        return selected
//    }
//
//    fun clearSelection() {
//        allItems.forEach { it.quantity = 0 }
//        notifyDataSetChanged()
//    }
//}
package com.example.datban

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.datban.model.FoodItem

class FoodItemAdapter(
    private val context: Context,
    private var allItems: List<FoodItem>
) : BaseAdapter() {

    private var displayedItems: List<FoodItem> = allItems
    private val TAG = "FoodItemAdapter"

    fun updateList(newList: List<FoodItem>) {
        // Không giữ lại trạng thái quantity từ danh sách cũ, reset về 0
        allItems = newList.map { it.copy(quantity = 0) }
        displayedItems = allItems
        notifyDataSetChanged()
        Log.d(TAG, "Danh sách đã cập nhật - Số lượng: ${allItems.size}")
    }

    override fun getCount(): Int = displayedItems.size

    override fun getItem(position: Int): FoodItem = displayedItems[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_food, parent, false)

        val foodItem = getItem(position)

        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)
        val tvName = view.findViewById<TextView>(R.id.tvMenuItemName)
        val tvPrice = view.findViewById<TextView>(R.id.tvMenuItemPrice)
        val ivImage = view.findViewById<ImageView>(R.id.ivMenuItem)

        tvName.text = foodItem.name
        tvPrice.text = "Giá: ${String.format("%,.0f", foodItem.price)} VND"

        // Xử lý trạng thái checkbox - luôn bắt đầu với unchecked
        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = false // Đảm bảo checkbox không được tick ban đầu
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            foodItem.quantity = if (isChecked) 1 else 0
            Log.d(TAG, "Món ${foodItem.name} - Số lượng: ${foodItem.quantity}")
        }

        // Load ảnh bằng Glide
        Glide.with(context)
            .load(foodItem.imageUrl)
            .placeholder(R.drawable.ic_food_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(ivImage)

        return view
    }

    fun getSelectedItems(): List<FoodItem> {
        val selected = allItems.filter { it.quantity > 0 }
        Log.d(TAG, "Số món đã chọn: ${selected.size}")
        return selected
    }

    fun clearSelection() {
        allItems.forEach { it.quantity = 0 }
        notifyDataSetChanged()
    }
}