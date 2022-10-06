package com.example.openwrtmanager.com.example.openwrtmanager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.database.DeviceItem
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.database.DeviceItemDao
//import com.example.openwrtmanager.com.example.openwrtmanager.ui.slideshow.database.IdentityItem
//import com.example.openwrtmanager.com.example.openwrtmanager.ui.slideshow.database.IdentityItemDao
import com.example.openwrtmanager.com.example.openwrtmanager.utils.Converters

@Database(
    version = 1,
    entities = [
//        IdentityItem::class,
        DeviceItem::class
    ],
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private const val DATABASE_NAME = "OpenWrt"

        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
        }
    }

//    abstract fun identityItemDao(): IdentityItemDao
    abstract fun deviceItemDao(): DeviceItemDao
}