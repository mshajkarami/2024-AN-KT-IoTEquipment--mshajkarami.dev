package com.irisaco.iotequipment.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.R

class SkeletonAdapter(private val itemCount: Int) : RecyclerView.Adapter<SkeletonAdapter.SkeletonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkeletonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.skeleton_item_search, parent, false)
        return SkeletonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkeletonViewHolder, position: Int) {}

    override fun getItemCount(): Int = itemCount

    inner class SkeletonViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
