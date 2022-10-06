package com.example.openwrtmanager.com.example.openwrtmanager.ui.information.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.model.InfoResponseModelItem
import com.example.openwrtmanager.com.example.openwrtmanager.utils.Utils
import com.example.openwrtmanager.databinding.ListItemWifiStatusBinding
import com.example.openwrtmanager.databinding.ViewstyleNetworkInterfaceBinding
import com.example.openwrtmanager.databinding.ViewstyleWifiTrafficBinding
import kotlin.math.roundToInt

data class HostHintData(
    var host: String,
    var ipv4: String
)

class WifiStatusHolder(private val binding: ListItemWifiStatusBinding) :
    RecyclerView.ViewHolder(
        binding.root
    ) {
    private val TAG = WifiStatusHolder::class.qualifiedName
    fun getIpAddressStringFromList(lst: List<*>?): String? {

        Log.d(TAG, "lst: " + lst)
        if (lst?.get(0) != null) {
            return lst.get(0).toString()
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    fun bind(
        hosthint: InfoResponseModelItem,
        wirelessDeviceData: InfoResponseModelItem,
        assoclisttt: InfoResponseModelItem
    ) {
        val view = mutableListOf<View>()
        var ifnameToApData = mutableMapOf<String, Map<String, Any?>>();
        val hostHintData = (hosthint.result.get(1) as Map<*, *>)
        val assoclist = (assoclisttt.result.get(1) as Map<*, *>)
        var macToHostsMap = mutableMapOf<String, HostHintData>();
        var wifiInterfaces = mutableListOf<String>()
        var wifiData = mutableListOf<Map<String, String>>();

        for (mac in (hostHintData).keys) {
            try {
                var maccc = hostHintData[mac] as Map<*, *>
                if (maccc != null && maccc["name"] != null) {
                    if (getIpAddressStringFromList(maccc["ipaddrs"] as List<*>?) != null) {
                        macToHostsMap[mac as String] = HostHintData(
                            maccc["name"].toString(),
                            getIpAddressStringFromList(maccc["ipaddrs"] as List<String>)!!
                        )
                    }
                }
            } catch (exception: Exception) {
                Log.d(TAG, "exception: " + exception)
            }
        }
        val wirelessDeviceDataaaaaa = (wirelessDeviceData.result[1]) as Map<*, *>
        for (radio in wirelessDeviceDataaaaaa.keys) {
            var interfacess =
                (wirelessDeviceDataaaaaa[radio] as Map<*, *>)["interfaces"] as MutableList<*>;
            var interfaces = interfacess.map { it -> it }
            for (interfaceee in interfaces) {
                var hihi = interfaceee as Map<*, *>
                if (hihi["ifname"] != null) {
                    ifnameToApData[hihi["ifname"] as String] = mutableMapOf(
                        "ssid" to (hihi["iwinfo"] as Map<*, *>)["ssid"],
                        "noise" to (hihi["iwinfo"] as Map<*, *>)["noise"],
                        "signal" to (hihi["iwinfo"] as Map<*, *>)["signal"],
                        "channel" to (hihi["iwinfo"] as Map<*, *>)["channel"],
                        "bitrate" to (hihi["iwinfo"] as Map<*, *>)["bitrate"],
                        "frequency" to (hihi["iwinfo"] as Map<*, *>)["frequency"],
                        "encryption" to (hihi["config"] as Map<*, *>)["encryption"],
                        "mode" to (hihi["config"] as Map<*, *>)["mode"],
                    )
                }
            }
        }
//            try{
//                var wifiDeviceCounter = 0;
//                for ( interfacee in assoclist) {
////                    var wifiInterface = widget.device.wifiDevices[wifiDeviceCounter];
//                    wifiDeviceCounter++;
////                    wifiInterfaces.add();
        var results = assoclist["results"] as List<Map<String, String>>;
        for (cli in results) {
            var i: MutableMap<String, String> = (cli as Map<String, String>).toMutableMap()
            var hostHint = macToHostsMap[i["mac"]];
            if (hostHint != null) i["hostname"] = hostHint.host;
//                        i["ip"] = "";
            if (hostHint != null) {
                i["ip"] = hostHint.ipv4;
            }
            i["ifname"] = "wlan1"
//                        if (DataCache.macAddressMap.containsKey(i["mac"])) {
//                            var d = DataCache.macAddressMap[i["mac"]];
//                            i["ip"] = d.ipAddress;
//                        } else if (hostHint != null) i["ip"] = hostHint.ipV4;
            wifiData.add(i);
        }
        var currentInterface = "";
        var ifCounter = 1;
        var apWithDevicesList = mutableListOf<String>()
        var firstClientInAP = true;
        Log.d(TAG, "wifiData: " + wifiData.size)
        for (cli in wifiData) {
            if (cli["ifname"] != currentInterface) {
                var apData = ifnameToApData[cli["ifname"]];
                if (apData != null) {
                    currentInterface = cli["ifname"].toString();
                    apWithDevicesList.add(currentInterface);
                    firstClientInAP = true;
                    // UI
                    val childView =
                        ViewstyleNetworkInterfaceBinding.inflate(LayoutInflater.from(binding.root.context))
                    childView.interfaceName.setText(cli["hostname"])

                    var freq = "";
                    if (apData["frequency"] != null) freq = " ${(apData["frequency"] as Double) / 1000} Ghz ";
                    var channel = "";
                    if (apData["channel"] != null) channel = " (${apData["channel"]})";
                    var f = "${apData["ssid"]}$freq$channel (${apData["mode"]}/${apData["encryption"]})"
                    binding.title.text = f
                }
            }
            ////////// TIME ////////
            val outgoing =
                (cli["rx"] as Map<*, *>)["bytes"].toString().toDouble().toInt();
            val incoming =
                (cli["rx"] as Map<*, *>)["bytes"].toString().toDouble().toInt();
            var name = cli["mac"].toString() + "_" + cli["ifname"].toString();
            if (TempData._wifiDeviceData[name] != null) {
                val incomingDiff = incoming - TempData._wifiDeviceData[name]!!["in"] as Int;
                val outgoingDiff = outgoing - TempData._wifiDeviceData[name]!!["out"] as Int;
                val currentTimeStamp = System.currentTimeMillis()
                val timeDiff: Double =
                    ((currentTimeStamp - TempData._wifiDeviceData[name]!!["timeStamp"] as Long) / 1000.0)

                TempData._wifiDeviceData[name]!!["timeStamp"] = currentTimeStamp;
                TempData._wifiDeviceData[name]!!["inSpeed"] = Utils.formatBytes(
                    (incomingDiff.toDouble() / timeDiff).roundToInt().toByte(), decimals = 1
                )
                TempData._wifiDeviceData[name]!!["outSpeed"] = Utils.formatBytes(
                    (outgoingDiff.toDouble() / timeDiff).roundToInt().toByte(), decimals = 1
                )
            }

            var incomingSpeed = " " + Utils.NoSpeedCalculationText + " Kb/s"
            var outgoingSpeed = " " + Utils.NoSpeedCalculationText + " Kb/s"

            if (TempData._wifiDeviceData[name] != null && TempData._wifiDeviceData[name]!!["inSpeed"] != null) {
                incomingSpeed = "${TempData._wifiDeviceData[name]!!["inSpeed"]}/s";
                outgoingSpeed = "${TempData._wifiDeviceData[name]!!["outSpeed"]}/s";
            }
            // 畫面
            val childView =
                ViewstyleWifiTrafficBinding.inflate(LayoutInflater.from(binding.root.context))
            childView.wifiDbm.setText((cli["signal"] as Double).toString()+" dBm")
            childView.wifiHostname.setText(cli["hostname"])
            childView.wifiMac.setText(cli["mac"])
            childView.wifiConnectedTime.setText("connected_time\n"+Utils.formatSeconds((cli["connected_time"] as Double).toLong()))
            val rx_rate =
                (cli["rx"] as Map<*, *>)["rate"].toString().toDouble();
            val tx_rate =
                (cli["tx"] as Map<*, *>)["rate"].toString().toDouble();
            childView.wifiRxRate.setText("${rx_rate / 1000}/${tx_rate / 1000} Mbit/s")
            childView.textView2.setText(cli["ip"])
            childView.incoming.setText(Utils.formatBytes(incoming.toByte(), 1))
            childView.incomingSpeed.setText(incomingSpeed)
            childView.outcoming.setText(Utils.formatBytes(outgoing.toByte(), 1))
            childView.outcomingSpeed.setText(outgoingSpeed)
            view.add(childView.root.rootView)
            // 時間
            if (TempData._wifiDeviceData[name] == null) {
                TempData._wifiDeviceData[name] = mutableMapOf<String, Any>();
            }
            TempData._wifiDeviceData[name]!!["out"] = outgoing;
            TempData._wifiDeviceData[name]!!["in"] = incoming;
            if (TempData._wifiDeviceData[name]!!["timeStamp"] == null) {
                TempData._wifiDeviceData[name]!!["timeStamp"] = System.currentTimeMillis();
            }
        }
        binding.wifiDeviceTraffic.removeAllViews()
        for (i in view) {
            Log.d(TAG, "viewsize: " + view.size);
            binding.wifiDeviceTraffic.addView(i)
        }
    }
}