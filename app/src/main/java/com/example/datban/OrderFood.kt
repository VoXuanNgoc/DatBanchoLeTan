//package com.example.datban
//
//import android.os.Bundle
//import android.widget.Button
//import android.widget.ListView
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import com.example.datban.model.FoodItem
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//
//class OrderFood : AppCompatActivity() {
//    private lateinit var listView: ListView
//    private lateinit var btnBuffet: Button
//    private lateinit var btnAla: Button
//    private lateinit var btnOk: Button
//    private lateinit var nameTextView: TextView
//    private lateinit var paxTextView: TextView
//
//    private lateinit var foodItems: MutableList<FoodItem>
//    private lateinit var adapter: FoodItemAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_order_food) // Thay bằng layout của bạn
//
//        // Ánh xạ view
//        listView = findViewById(R.id.listView)
//        btnBuffet = findViewById(R.id.btnbuffet)
//        btnAla = findViewById(R.id.btnala)
//        btnOk = findViewById(R.id.btnOk)
//        nameTextView = findViewById(R.id.namekhach)
//        paxTextView = findViewById(R.id.paxkhach)
//
//        // Nhận dữ liệu từ Intent
//        val name = intent.getStringExtra("name") ?: ""
//        val pax = intent.getStringExtra("pax") ?: ""
//
//        nameTextView.text = "Tên khách: $name"
//        paxTextView.text = "Số người: $pax"
//
//        // Khởi tạo danh sách và adapter
//        foodItems = mutableListOf()
//        adapter = FoodItemAdapter(this, foodItems)
//        listView.adapter = adapter
//
//        // Load dữ liệu từ Firebase
//        loadFoodItems()
//
//        // Xử lý sự kiện button
//        btnBuffet.setOnClickListener {
//            filterFoodItems("buffet")
//        }
//
//        btnAla.setOnClickListener {
//            filterFoodItems("a_la_carte")
//        }
//
//        btnOk.setOnClickListener {
//            // Xử lý khi nhấn nút OK
//            finish()
//        }
//    }
//
//    private fun loadFoodItems() {
//        val database = FirebaseDatabase.getInstance()
//        val foodRef = database.getReference("menu") // Thay bằng reference thực tế của bạn
//
//        foodRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                foodItems.clear()
//                for (data in snapshot.children) {
//                    val foodItem = data.getValue(FoodItem::class.java)
//                    foodItem?.let {
//                        // Gán ID từ Firebase
//                        it.id = data.key ?: ""
//                        foodItems.add(it)
//                    }
//                }
//                adapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Xử lý lỗi
//            }
//        })
//    }
//
//    private fun filterFoodItems(type: String) {
//        val filteredList = if (type == "buffet" || type == "a_la_carte") {
//            foodItems.filter { it.type == type }
//        } else {
//            foodItems
//        }
//        adapter.updateList(filteredList)
//    }
//}
package com.example.datban

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.datban.model.FoodItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

class OrderFood : AppCompatActivity() {

    private lateinit var adapter: FoodItemAdapter
    private lateinit var foodItems: MutableList<FoodItem>
    private val TAG = "OrderFood"
    private var currentType: String = "buffet"
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_food)

        // Ánh xạ view
        val listView = findViewById<ListView>(R.id.listView)
        val btnBuffet = findViewById<Button>(R.id.btnbuffet)
        val btnAla = findViewById<Button>(R.id.btnala)
        val btnOk = findViewById<Button>(R.id.btnOk)
        val nameTextView = findViewById<TextView>(R.id.namekhach)
        val paxTextView = findViewById<TextView>(R.id.paxkhach)

        // Nhận dữ liệu từ Intent
        val name = intent.getStringExtra("name") ?: "Khách"
        val pax = intent.getIntExtra("pax", 1)

        nameTextView.text = "Tên khách: $name"
        paxTextView.text = "Số người: $pax"

        // Khởi tạo adapter
        foodItems = mutableListOf()
        adapter = FoodItemAdapter(this, foodItems)
        listView.adapter = adapter

        // Tải dữ liệu món ăn
        loadFoodItems()

        // Xử lý sự kiện
        btnBuffet.setOnClickListener {
            currentType = "buffet"
            filterFoodItems(currentType)
        }

        btnAla.setOnClickListener {
            currentType = "a_la_carte"
            filterFoodItems(currentType)
        }

        btnOk.setOnClickListener {
            saveSelectedItems(pax, name)
        }
    }

    private fun loadFoodItems() {
        val database = FirebaseDatabase.getInstance()
        val foodRef = database.getReference("menu")

        showProgressDialog("Đang tải danh sách món ăn...")

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                foodItems.clear()
                for (data in snapshot.children) {
                    try {
                        val foodItem = data.getValue(FoodItem::class.java)
                        foodItem?.let {
                            it.id = data.key ?: ""
                            foodItems.add(it)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Lỗi parse món ăn: ${data.key}", e)
                    }
                }
                filterFoodItems(currentType)
                dismissProgressDialog()
                Log.d(TAG, "Đã tải ${foodItems.size} món ăn")
            }

            override fun onCancelled(error: DatabaseError) {
                dismissProgressDialog()
                Toast.makeText(this@OrderFood, "Lỗi tải món ăn: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Lỗi tải món ăn", error.toException())
            }
        })
    }

    private fun filterFoodItems(type: String) {
        val filteredList = foodItems.filter { it.type == type }
        adapter.updateList(filteredList)
        Log.d(TAG, "Đã lọc ${filteredList.size} món loại $type")
    }

    private fun saveSelectedItems(pax: Int, name: String) {
        val selectedItems = adapter.getSelectedItems()
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 món", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_LONG).show()
            return
        }

        val progressDialog = showProgressDialog("Đang lưu đơn hàng...")

        val database = FirebaseDatabase.getInstance()
        val ordersRef = database.getReference("selectedOrders")

        // Chuẩn bị dữ liệu
        val orderId = ordersRef.push().key
        val orderItems = selectedItems.map {
            mapOf(
                "id" to it.id,
                "name" to it.name,
                "price" to it.price,
                "quantity" to it.quantity
            )
        }

        val orderData = mapOf(
            "customerName" to name,
            "pax" to pax,
            "items" to orderItems,
            "timestamp" to ServerValue.TIMESTAMP,
            "status" to "pending"
        )

        orderId?.let { id ->
            ordersRef.child(id).setValue(orderData)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Đặt món thành công!", Toast.LENGTH_SHORT).show()
                    adapter.clearSelection()
                    Log.d(TAG, "Đã lưu đơn hàng ID: $id")
                    finish()
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this, "Lỗi: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Lỗi lưu đơn hàng", e)
                }
        } ?: run {
            progressDialog.dismiss()
            Toast.makeText(this, "Lỗi tạo ID đơn hàng", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun showProgressDialog(message: String): ProgressDialog {
        progressDialog = ProgressDialog(this).apply {
            setMessage(message)
            setCancelable(false)
            show()
        }
        return progressDialog!!
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

}


