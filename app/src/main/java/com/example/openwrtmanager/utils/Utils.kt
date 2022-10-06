package com.example.openwrtmanager.com.example.openwrtmanager.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.fragment.app.Fragment
import java.util.concurrent.TimeUnit
import kotlin.math.truncate


class Utils {

    //    static const bool ReleaseMode = bool.fromEnvironment('dart.vm.product', defaultValue: false);
    companion object {
        var NoSpeedCalculationText = "-----"
        fun formatSeconds(seconds: Long): String {
            val days = TimeUnit.SECONDS.toDays(seconds).toInt()
            val hours: Int =
                (TimeUnit.SECONDS.toHours(seconds) - TimeUnit.SECONDS.toDays(seconds) * 24).toInt()
            val minutes: Int =
                (TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.SECONDS.toHours(seconds) * 60).toInt()
            val seconds: Int =
                (TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toMinutes(seconds) * 60).toInt()

            val tokens: MutableList<String> = mutableListOf();
            if (days != 0) {
                tokens.add("${days}d");
            }
            if (tokens.isNotEmpty() || hours != 0) {
                tokens.add("${hours}h");
            }
            if (tokens.isNotEmpty() || minutes != 0) {
                tokens.add("${minutes}m");
            }
            tokens.add("${seconds}s");
            return tokens.joinToString(" ")
        }
        fun coverUrl(port: String, address: String, isUseHttpsConnection: Boolean): String {
            var postt = port
            if (port == "" || port.toInt() <= 0) {
                postt = "80"
            }
            if (isUseHttpsConnection) {
                return "https://$address:$postt/"
            } else {
                return "http://$address:$postt/"
            }
        }
        fun formatBytes(bytes: Byte, decimals: Int =0): String {
            if (bytes <= 0) return "0 B";
            val suffixes = listOf("B", "Kb", "Mb", "Gb", "Tb", "Pb")
            val i = Math.floor(Math.log(bytes.toDouble()) / Math.log(1_024.0));
            val number = (bytes / Math.pow(1024.0, i));

            val a = if (truncate(number) == number) 0 else decimals
            return String.format("%.${a}f", number) + ' ' +
                    suffixes[i.toInt()];
        }
    }
}
fun Fragment.isNetworkAvailable(): Boolean {
    val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE)
    return if (connectivityManager is ConnectivityManager) {
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        networkInfo?.isConnected ?: false
    } else false
}
