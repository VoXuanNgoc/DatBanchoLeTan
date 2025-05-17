package com.example.datban


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.datban.adapter.MenuImageAdapter
import com.example.datban.model.MenuItem
import com.google.firebase.database.*

class MenuFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuImageAdapter
    private lateinit var database: DatabaseReference
    private val imageUrls = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewMenu)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MenuImageAdapter(imageUrls)
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance().getReference("Image")
        loadImagesFromFirebase()

        return view
    }

    private fun loadImagesFromFirebase() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                imageUrls.clear()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(MenuItem::class.java)
                    item?.imageUrl?.let { imageUrls.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu cần
            }
        })
    }
}
