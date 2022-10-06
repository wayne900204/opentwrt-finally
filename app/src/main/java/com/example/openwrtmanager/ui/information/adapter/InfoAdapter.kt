package com.example.openwrtmanager.com.example.openwrtmanager.ui.information

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.adapter.ActiviteConnectionHolder
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.adapter.IpNetworkStatusHolder
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.adapter.IpNetworkTrafficHolder
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.adapter.SystemInfoHolder
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.model.InfoResponseModelItem
import com.example.openwrtmanager.databinding.ListItemActiviceConnectionBinding
import com.example.openwrtmanager.databinding.ListItemIpStatusBinding
import com.example.openwrtmanager.databinding.ListItemNetworkTrafficBinding
import com.example.openwrtmanager.databinding.ListItemSystemBinding


// 1. onBindViewHolder 查看第幾  SAMPLE:2 個 item 要對應哪一個畫面
// 2. 再去查看 getItemViewType 的那個 item 2-> 對應哪一個參數 SAMPLE:2->INT3
// 3. 再去看 onCreateViewHolder 裡面的 viewType 是對應哪一個 viewholder SAMPLE 3-> NetworkTraffic
class InfoAdapter : ListAdapter<MutableList<InfoResponseModelItem>, RecyclerView.ViewHolder>(
    InfoDiffCallback()
) {
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("DEBUG", "onCreateViewHolder: "+viewType)
        return when (viewType) {
            0 -> SystemInfoHolder(ListItemSystemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            1 -> IpNetworkStatusHolder(ListItemIpStatusBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            2 -> IpNetworkTrafficHolder(ListItemNetworkTrafficBinding.inflate(LayoutInflater.from(parent.context),parent,false))
//            3 -> WifiStatusHolder(ListItemWifiStatusBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            3 -> ActiviteConnectionHolder(ListItemActiviceConnectionBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            else -> SystemInfoHolder(ListItemSystemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            0 -> (holder as SystemInfoHolder).bind(currentList[position][0],currentList[position][1])
            1 -> (holder as IpNetworkStatusHolder).bind(currentList[position][0])
            2 -> (holder as IpNetworkTrafficHolder).bind(currentList[position][0],currentList[position][1])
//            3 -> (holder as WifiStatusHolder).bind(currentList[position][0] ,currentList[position][1] )
            3 -> (holder as ActiviteConnectionHolder).bind(currentList[position][0] )
            else->{}
        }
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