package com.example.openwrtmanager.com.example.openwrtmanager.ui.device.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = DeviceItem.TABLE_NAME
)
data class DeviceItem(
    @ColumnInfo(name = COLUMN_DISPLAY_NAME) var displayName: String,
    @ColumnInfo(name = COLUMN_ADDRESS) var address: String,
    @ColumnInfo(name = COLUMN_PORT) var port: String,
    @ColumnInfo(name = COLUMN_USERNAME) var username: String,
    @ColumnInfo(name = COLUMN_PASSWORD) var password: String,
    @ColumnInfo(name = COLUMN_USE_HTTPS_CONNECTION) var useHttpsConnection: Boolean,
    @ColumnInfo(name = COLUMN_IGNORE_BAD_CERTIFICATE) var ignoreBadCertificate: Boolean,
    @ColumnInfo(name = COLUMN_CREATED_AT) var createdAt: Date
) {
    companion object {
        const val TABLE_NAME = "device_items"

        const val COLUMN_ID = "id"
        const val COLUMN_DISPLAY_NAME = "display_name"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_PORT = "port"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_USE_HTTPS_CONNECTION = "use_https_connection"
        const val COLUMN_IGNORE_BAD_CERTIFICATE = "ignore_bad_certificate"
        const val COLUMN_CREATED_AT = "created_at"
    }

    // 必須為 var 才會有 setter
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    var id: Int = 0
}
