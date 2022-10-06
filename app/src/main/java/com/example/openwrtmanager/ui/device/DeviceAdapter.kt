package com.example.openwrtmanager.com.example.openwrtmanager.ui.device

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.database.DeviceItem
import com.example.openwrtmanager.databinding.ListItemIdentitiyBinding
import com.example.openwrtmanager.ui.device.DeviceFragmentDirections


class DeviceAdapter : RecyclerView.Adapter<DeviceAdapter.MyViewHolder>() {
    private val TAG = DeviceAdapter::class.qualifiedName
    var feeds: List<DeviceItem> = listOf()
    var row_index = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceAdapter.MyViewHolder {
        return MyViewHolder(
            ListItemIdentitiyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(feeds[position],position)
    }

    override fun getItemCount() = feeds.size

    inner class MyViewHolder(private val binding: ListItemIdentitiyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val pref = binding.root.context.getSharedPreferences("OpenWrt", Context.MODE_PRIVATE)
        var editor = pref.edit()

        fun bindView(item: DeviceItem, position: Int) {
            binding.display.text = item.displayName
            if(feeds.isEmpty()){
                editor.putInt("device_select_item_id",-1).commit()
            }
            if ( feeds.size==1) {
                editor.putInt("device_select_item_id",feeds.first().id).commit()
            }
            binding.itemIdentity.setOnClickListener {

                if (pref.getInt("device_select_item_id", -1) == item.id) {
                    val action = DeviceFragmentDirections.actionDeviceFragmentToAddDeviceFragment(item.id, true)
                    itemView.findNavController().navigate(action)
                }
                editor.putInt("device_select_item_id", item.id).commit()
                notifyDataSetChanged()

            }
            if (pref.getInt("device_select_item_id", -1) == item.id) {
                binding.isUsing.visibility = View.VISIBLE
            } else {
                binding.isUsing.visibility = View.GONE
            }
        }

    }

    fun updateAdapterData(todos: List<DeviceItem>) {
        feeds = todos
        notifyDataSetChanged()
    }

}
