package com.example.openwrtmanager.com.example.openwrtmanager.ui.information.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.adapter.TempData.Companion._ipNetworkTrafficData
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.model.InfoResponseModelItem
import com.example.openwrtmanager.com.example.openwrtmanager.utils.Utils
import com.example.openwrtmanager.databinding.ListItemNetworkTrafficBinding
import com.example.openwrtmanager.databinding.ViewstyleNetworkInterfaceBinding
import kotlin.math.roundToInt

class TempData {
    companion object {
        var _ipNetworkTrafficData = mutableMapOf<String, MutableMap<String, Any>>()
        var _wifiDeviceData = mutableMapOf<String, MutableMap<String, Any>>()
    }
}

class IpNetworkTrafficHolder(private val binding: ListItemNetworkTrafficBinding) :
    RecyclerView.ViewHolder(
        binding.root
    ) {
    private val TAG = SystemInfoHolder::class.qualifiedName


    @SuppressLint("SetTextI18n")
    fun bind(networkDevices: InfoResponseModelItem, networkInterface: InfoResponseModelItem) {
        val _interfaces = mutableListOf<String>()
        val view = mutableListOf<View>()
        // Data
        val trafficInterfaces = (networkDevices.result.get(1) as Map<*, *>)
        var ifCounter = 1;
        val interfaces =
            (networkInterface.result.get(1) as Map<*, *>)["interface"] as List<*>;
        var tempcount = 0;
        for (name in trafficInterfaces.keys) {
            var iff = trafficInterfaces[name] as Map<*, *>;

            var tempBoo: Boolean = ((iff["flags"] as Map<*, *>)["loopback"]) as Boolean
            var tempBo: Boolean = (iff["up"] as Boolean)
            if (tempBo && !tempBoo) {
                tempcount++;
                _interfaces.add(name as String)
                val outgoing =
                    (iff["stats"] as Map<*, *>)["tx_bytes"].toString().toDouble().toInt();
                val incoming =
                    (iff["stats"] as Map<*, *>)["rx_bytes"].toString().toDouble().toInt();
                if (_ipNetworkTrafficData[name] != null) {
                    val incomingDiff = incoming - _ipNetworkTrafficData[name]!!["in"] as Int;
                    val outgoingDiff = outgoing - _ipNetworkTrafficData[name]!!["out"] as Int;
                    val currentTimeStamp = System.currentTimeMillis()
                    val timeDiff: Double =
                        ((currentTimeStamp - _ipNetworkTrafficData[name]!!["timeStamp"] as Long) / 1000.0)

                    _ipNetworkTrafficData[name]!!["timeStamp"] = currentTimeStamp;
                    _ipNetworkTrafficData[name]!!["inSpeed"] = Utils.formatBytes(
                        (incomingDiff.toDouble() / timeDiff).roundToInt().toByte(), decimals = 1
                    )
                    _ipNetworkTrafficData[name]!!["outSpeed"] =
                        Utils.formatBytes(
                            (outgoingDiff.toDouble() / timeDiff).roundToInt().toByte(), decimals = 1
                        )
                }

                var incomingSpeed = " " + Utils.NoSpeedCalculationText + " Kb/s"
                var outgoingSpeed = " " + Utils.NoSpeedCalculationText + " Kb/s"

                if (_ipNetworkTrafficData[name] != null && _ipNetworkTrafficData[name]!!["inSpeed"] != null) {
                    incomingSpeed = "${_ipNetworkTrafficData[name]!!["inSpeed"]}/s";
                    outgoingSpeed = "${_ipNetworkTrafficData[name]!!["outSpeed"]}/s";
                }
                var interfaceeee = interfaces.find {
                    (it as Map<*, *>)["l3_device"] == iff["name"]
                }
                if (interfaceeee == null) {
                    interfaceeee =
                        interfaces.first { iff["master"] == (it as Map<*, *>)["l3_device"] }
                }
                var uptime = ""
                var interfaceAddress = "";
                if (interfaceeee != null) {
                    if ((interfaceeee as Map<*, *>)["uptime"] != null) uptime =
                        Utils.formatSeconds((interfaceeee["uptime"] as Double).toLong());
                    if (interfaceeee["ipv4-address"] != null && (interfaceeee["ipv4-address"] as List<*>).size > 0)
                        interfaceAddress =
                            "${((interfaceeee["ipv4-address"] as List<*>)[0] as Map<*, *>)["address"] ?: "hi"}";
                }
                val childView = ViewstyleNetworkInterfaceBinding.inflate(LayoutInflater.from(binding.root.context))
                childView.interfaceName.setText("${iff["name"]}")
                childView.interfaceIp.setText(interfaceAddress)
                childView.interfaceTime.setText("${uptime}")
                childView.incoming.setText(Utils.formatBytes(incoming.toByte(), 1))
                childView.incomingSpeed.setText(incomingSpeed)
                childView.outcoming.setText(Utils.formatBytes(outgoing.toByte(), 1))
                childView.outcomingSpeed.setText(outgoingSpeed)
                view.add(childView.root.rootView)

                if (_ipNetworkTrafficData[name] == null) {
                    _ipNetworkTrafficData[name] = mutableMapOf<String, Any>();
                }
                _ipNetworkTrafficData[name]!!["out"] = outgoing;
                _ipNetworkTrafficData[name]!!["in"] = incoming;
                if (_ipNetworkTrafficData[name]!!["timeStamp"] == null)
                    _ipNetworkTrafficData[name]!!["timeStamp"] = System.currentTimeMillis();
            }
        }
        binding.ipNetworkTraffic.removeAllViews()
        for (i in view) {
            binding.ipNetworkTraffic.addView(i)
        }

    }

}