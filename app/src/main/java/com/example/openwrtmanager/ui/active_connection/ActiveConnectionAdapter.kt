package com.example.openwrtmanager.com.example.openwrtmanager.ui.activite_connection

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.adapter.ActiviteConnectionHolder
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.model.InfoResponseModelItem
import com.example.openwrtmanager.databinding.ListItemActiviceConnectionBinding

class ActiviteConnectionAdapter : ListAdapter<MutableList<InfoResponseModelItem>, RecyclerView.ViewHolder>(
    InfoDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("DEBUG", "onCreateViewHolder: "+viewType)
        val itemBinding = ListItemActiviceConnectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActiviteConnectionHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("DEBUG", "currentList123: "+currentList[position][0])
        (holder as ActiviteConnectionHolder).bind(currentList[position][0])
    }
}

private class InfoDiffCallback : DiffUtil.ItemCallback<MutableList<InfoResponseModelItem>>() {
    //    areItemsTheSame() 方法比對是否來自同一個
//    viewType(也就是同一個 ViewHolder )
    override fun areItemsTheSame(
        oldItem: MutableList<InfoResponseModelItem>,
        newItem: MutableList<InfoResponseModelItem>
    ): Boolean {
        return oldItem==newItem
    }

    override fun areContentsTheSame(
        oldItem: MutableList<InfoResponseModelItem>,
        newItem: MutableList<InfoResponseModelItem>
    ): Boolean {
        return oldItem==newItem
    }
}