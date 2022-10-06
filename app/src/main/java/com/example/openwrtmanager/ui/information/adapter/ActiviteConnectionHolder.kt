package com.example.openwrtmanager.com.example.openwrtmanager.ui.information.adapter

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.adapter.ActiviteConnection.Companion.lastTrafficDataTimeStamp
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.model.InfoResponseModelItem
import com.example.openwrtmanager.com.example.openwrtmanager.utils.Utils
import com.example.openwrtmanager.databinding.ListItemActiviceConnectionBinding
import com.example.openwrtmanager.databinding.ViewstyleActiveConnectionsBinding
import kotlin.math.roundToInt

class ActiviteConnection {
    companion object {
        var _trafficData = mutableMapOf<String, MutableMap<String, Any>>()
        var lastTrafficDataTimeStamp = 0L
    }
}

class ActiviteConnectionHolder(private val binding: ListItemActiviceConnectionBinding) :
    RecyclerView.ViewHolder(
        binding.root
    ) {
    private val TAG = ActiviteConnectionHolder::class.qualifiedName

    var _ipLookup = mutableMapOf<String, String>()
    var _trafficMap = mutableMapOf<String, Any>()


    @SuppressLint("SetTextI18n")
    fun bind(activiteConnection: InfoResponseModelItem) {
        val view = mutableListOf<View>()
        val connectionsList = (activiteConnection.result.get(1) as Map<*, *>)["result"] as List<*>;
        // Data
        Log.d("DEBUG", "次數");
        if (connectionsList.size == 0) {

        } else {
//            connectionsList.sortedWith(compareBy { getBytes(((it as Map<*, *>)["bytes"] as Double).toInt()) })
//            connectionsList.sortedBy {  getBytes(((it as Map<*, *>)["bytes"] as Double).toInt())  -  getBytes(((it as Map<*, *>)["bytes"] as Double).toInt())  }
            connectionsList.sortedWith{ a, b -> getBytes(((b as Map<*, *>)["bytes"] as Double).toInt())  -  getBytes(((a as Map<*, *>)["bytes"] as Double).toInt())  }
//            Log.d("DEBUG", " activiteConnection sortedWith: " + activiteConnection);

            var ipToResolve = mutableListOf<String>()
            val currentTimeStamp = System.currentTimeMillis()
            for (con in connectionsList) {
                var con = con as Map<*, *>
                var bytes = con["bytes"].toString().toDouble().roundToInt()
                checkIpForLookup(con["src"]!!, ipToResolve);
                checkIpForLookup(con["dst"]!!, ipToResolve);

                var speedText: String = ""

                var connectionKey = getProtocolText(con, "src", "sport") + "," + getProtocolText(
                    con,
                    "dst",
                    "dport"
                )

                if (_trafficMap.containsKey(connectionKey)) {
                    var oldTrafficData = _trafficMap[connectionKey] as MutableMap<String, Any>;
//                    var oldTrafficData:Map<String,Any> = _trafficMap[connectionKey] as Map<String, Any>;
//                    var oldTrafficData:mui = _trafficMap[connectionKe。]。
                    var newTrafficDataBytes = bytes;

                    //if( is new data ){
                    var timeDiff = (currentTimeStamp - lastTrafficDataTimeStamp) / 1000.0;
                    if (timeDiff > 0) {
                        var byteDiff = newTrafficDataBytes - (oldTrafficData["traffic"] as Int);
                        var speed = (byteDiff / timeDiff).roundToInt();
                        speedText = Utils.formatBytes(speed.toByte(), 2) + "/s";
//                        oldTrafficData["speedText"] = speedText;
                        oldTrafficData.put("speedText", speed)
                        oldTrafficData.put("traffic", newTrafficDataBytes)
//                        oldTrafficData["traffic"] = newTrafficDataBytes;
                    } else {
                        speedText = oldTrafficData["speedText"] as String;
                    }
                    // }
                } else {
                    val a = mutableMapOf<String, Int>("traffic" to bytes)
                    _trafficMap.put(connectionKey, a)
//                    Log.d("DEBUG", " connectionKey: " + connectionKey);
                }
//                Log.d("DEBUG", " activiteConnection _trafficMap: " + _trafficMap);

                /////////////////// UI

//                Log.d("DEBUG", " UI layer3: " + con["layer3"]);
//                Log.d("DEBUG", " UI layer4: " + con["layer4"]);
//                Log.d("DEBUG", " UI speedText: " + if(speedText!="")speedText else (Utils.NoSpeedCalculationText + " Kb/s"));
//                Log.d("DEBUG", " UI bytes: " + Utils.formatBytes(bytes.toByte(), 2));
//                Log.d("DEBUG", " UI ProtocolText src: " + getProtocolText(con, "src", "sport"));
//                Log.d("DEBUG", " UI ProtocolText dst: " + getProtocolText(con, "dst", "dport"));
                val childView = ViewstyleActiveConnectionsBinding.inflate(LayoutInflater.from(binding.root.context))
                childView.layer.setText(con["layer3"].toString()+"/"+con["layer4"].toString())
                childView.speedText.setText(if(speedText!="")speedText else (Utils.NoSpeedCalculationText + " Kb/s"))
                childView.bytes.setText(Utils.formatBytes(bytes.toByte(), 2))
                childView.bytes.setTypeface(null, Typeface.BOLD_ITALIC);
                childView.dstProtocolText.setText(getProtocolText(con, "dst", "dport"))
                childView.srcProtocolText.setText(getProtocolText(con, "src", "sport"))
                view.add(childView.root.rootView)
            }


            //
            lastTrafficDataTimeStamp = currentTimeStamp;

            if (ipToResolve.size > 0) {
                // TODO: 把本 openwrt 的 ip 換成名稱
//                var cli = OpenWRTClient(widget.device, null);
//                cli.getRemoteDns(widget.authenticationStatus, ipToResolve).then((res) {
//                    if (res.status == ReplyStatus.Ok) {
//                        var data = (res.data["result"] as dynamic)[1];
//                        if (data != null && data is Map) {
//                            for (String ip in data.keys) {
//                                _ipLookup[ip] = data[ip];
//                            }
//                        }
//                    }
//                });
            }
        }
        binding.ipNetworkTraffic.removeAllViews()
        for (i in view) {
            binding.ipNetworkTraffic.addView(i)
        }

    }

    private fun getBytes(b: Any): Int {
//        return double.parse(b.toString()).round();
        return b.toString().toDouble().roundToInt();
    }

    private fun checkIpForLookup(ip: Any, ipToResolve: MutableList<String>) {
        if (!_ipLookup.containsKey(ip)) {
            ipToResolve.add(ip as String)
            _ipLookup.put(ip, ip)
        }
    }

    fun getProtocolText(data: Map<*, *>, ipPropName: String?, portPropName: String?): String? {
        val ip = data.get(ipPropName) ?: return ""
        var str = _ipLookup[ip]
        if (data.get(portPropName) != null) str = str.toString() + ":" + data.get(portPropName)
        return str
    }

}