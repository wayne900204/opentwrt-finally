package com.example.openwrtmanager.com.example.openwrtmanager.ui.information.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.model.InfoResponseModelItem
import com.example.openwrtmanager.com.example.openwrtmanager.utils.Utils
import com.example.openwrtmanager.databinding.ListItemIpStatusBinding
import com.example.openwrtmanager.databinding.ViewstyleIpStatusBinding


class IpNetworkStatusHolder(private val binding: ListItemIpStatusBinding) :
    RecyclerView.ViewHolder(
        binding.root
    ) {
    private val TAG = SystemInfoHolder::class.qualifiedName

    @SuppressLint("SetTextI18n")
    fun bind(networkInterface: InfoResponseModelItem) {
        val _interfaces = mutableListOf<String>()
        val view = mutableListOf<View>()

        val interfaces = (networkInterface.result.get(1) as Map<*, *>)["interface"] as List<*>;
        for (iff in interfaces) {
            val iff = iff as Map<*, *>
            if (iff["up"] as Boolean && iff["proto"] != "none" && iff["device"] != "lo") {
                var dataMap = mutableMapOf<String, String>();
                var interfaceee = iff["interface"] as String;
                _interfaces.add(interfaceee);
                dataMap.putAll(mapOf("Interface" to "\$interface (${iff["device"]})"))
                var ipAddress = iff["ipv4-address"] as List<*>;
                for (ip in ipAddress) {
                    val ip = ip as Map<*, *>
                    dataMap.putAll(mapOf("Ip Address" to "${ip["address"]}/${ip["mask"]}"));
                }
                dataMap.putAll(mapOf("Up" to Utils.formatSeconds((iff["uptime"] as Double).toLong())))
                var dnsServer = iff["dns-server"] as List<*>;
                if (dnsServer.size > 0) {
                    dataMap.putAll(mapOf("Dns Server" to dnsServer.joinToString("\n")))
                }
                for(k in dataMap.keys){
                    val childView = ViewstyleIpStatusBinding.inflate(LayoutInflater.from(binding.root.context))
                    childView.name.setText(k)
                    childView.body.setText(dataMap[k])
                    view.add(childView.root.rootView)
                }
            }
        }

        binding.statusBody.removeAllViews()
        for (i in view) {
            binding.statusBody.addView(i)
        }
    }
}