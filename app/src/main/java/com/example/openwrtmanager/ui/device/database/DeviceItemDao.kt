package com.example.openwrtmanager.com.example.openwrtmanager.ui.device.database


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.openwrtmanager.com.example.openwrtmanager.utils.GenericDao

@Dao
interface DeviceItemDao : GenericDao<DeviceItem> {

    @Query("SELECT * FROM ${DeviceItem.TABLE_NAME} ORDER BY ${DeviceItem.COLUMN_CREATED_AT} DESC")
    fun findAll(): LiveData<List<DeviceItem>>

    @Query("DELETE FROM ${DeviceItem.TABLE_NAME} WHERE ${DeviceItem.COLUMN_ID} LIKE :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM ${DeviceItem.TABLE_NAME} WHERE ${DeviceItem.COLUMN_ID} LIKE :id")
    fun getById(id: Int): LiveData<DeviceItem>

    @Query("SELECT * FROM ${DeviceItem.TABLE_NAME} WHERE ${DeviceItem.COLUMN_ID} LIKE :id")
    suspend fun suspendGetDeviceDeviceItemById(id: Int): DeviceItem

    @Query("UPDATE  ${DeviceItem.TABLE_NAME} SET ${DeviceItem.COLUMN_DISPLAY_NAME} = :displayName, ${DeviceItem.COLUMN_ADDRESS} = :address, ${DeviceItem.COLUMN_PORT} = :port,${DeviceItem.COLUMN_USERNAME} = :username, ${DeviceItem.COLUMN_PASSWORD} = :password, ${DeviceItem.COLUMN_USE_HTTPS_CONNECTION} = :useHttpsConnection, ${DeviceItem.COLUMN_IGNORE_BAD_CERTIFICATE} = :ignoreBadCertificate WHERE ${DeviceItem.COLUMN_ID} LIKE :id")
    suspend fun updateById(
        displayName: kotlin.String,
        address: kotlin.String,
        port: kotlin.String,
        username: kotlin.String,
        password: kotlin.String,
        useHttpsConnection: kotlin.Boolean,
        ignoreBadCertificate: kotlin.Boolean,
        id: kotlin.Int
    )
}
