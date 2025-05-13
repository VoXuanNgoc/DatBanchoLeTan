package com.example.datban

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class StatusSpinnerAdapter(
    context: Context,
    private val resource: Int,
    private val statusList: List<String>
) : ArrayAdapter<String>(context, resource, statusList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, convertView, parent)
    }

    private fun createCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        val statusTextView = view.findViewById<TextView>(android.R.id.text1)
        statusTextView.text = statusList[position]
        return view
    }
}