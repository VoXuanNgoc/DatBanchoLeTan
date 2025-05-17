package com.example.datban.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.datban.R

class MenuImageAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<MenuImageAdapter.MenuImageViewHolder>() {

    inner class MenuImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_image, parent, false)
        return MenuImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuImageViewHolder, position: Int) {
        Glide.with(holder.imageView.context)
            .load(imageUrls[position])
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = imageUrls.size
}
