package com.example.openwrtmanager.com.example.openwrtmanager.ui.information.adapter

import android.annotation.SuppressLint
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.openwrtmanager.R
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.model.InfoResponseModelItem
import com.example.openwrtmanager.com.example.openwrtmanager.utils.Utils
import com.example.openwrtmanager.databinding.ListItemSystemBinding

class SystemInfoHolder(private val binding: ListItemSystemBinding) : RecyclerView.ViewHolder(
    binding.root
) {
    private val TAG = SystemInfoHolder::class.qualifiedName

    private val linearLayout: LinearLayout = itemView.findViewById(R.id.item_system_linear_layout)

    @SuppressLint("SetTextI18n")
    fun bind(systemInfo: InfoResponseModelItem, systemBoard: InfoResponseModelItem) {
        // Data
        val infoData = (systemInfo.result.get(1) as Map<*, *>)
        val uptime = (infoData["uptime"] as Double).toLong();
        val load = infoData["load"] as List<*>;
        val memoryTotal: Double = (infoData["memory"] as Map<*, *>)["total"] as Double;
        val memoryUsed: Double = memoryTotal -
                (infoData["memory"] as Map<*, *>)["free"] as Double -
                (infoData["memory"] as Map<*, *>)["cached"] as Double -
                (infoData["memory"] as Map<*, *>)["buffered"] as Double;
        val boardData = systemBoard.result.get(1) as Map<*, *>;
        val hostName = boardData["hostname"];
        val releaseData = boardData["release"] as Map<*, *>;
        // UI
        binding.hostname.text = hostName.toString()
        binding.uptime.text = Utils.formatSeconds(uptime)
        binding.load.text = load.joinToString(" , ") { "%.2f".format(it as Double / 65536) }
        binding.memoryUsage.text =
            "%.2f".format(memoryUsed / 1024 / 1024) + "Mb/" + "%.2f".format(memoryTotal / 1024 / 1024) + "Mb"
        binding.release.text = releaseData["description"].toString()
        binding.model.text = boardData["model"].toString()
        binding.kernel.text = boardData["kernel"].toString()
        binding.system.text = boardData["system"].toString()
        binding.target.text = releaseData["target"].toString()
    }

}