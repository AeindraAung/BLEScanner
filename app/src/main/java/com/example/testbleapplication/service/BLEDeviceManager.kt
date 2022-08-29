package com.example.testbleapplication.service

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BLEDeviceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter?
        get() = bluetoothManager.adapter
    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()
    private lateinit var scanCallback: ScanCallback

    fun getBleAdapter(): BluetoothAdapter? {
        return bluetoothAdapter
    }

    fun getBLEdevices(scanCallback: ScanCallback) {
        if (bluetoothAdapter == null || !bluetoothAdapter?.isEnabled!!) {
            return
        }
        this.scanCallback = scanCallback

        val filters: MutableList<ScanFilter> = ArrayList()
        val scanFilter: ScanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(UUID.fromString(SERVICE_STRING)))
            .build()
        filters.add(scanFilter)

        bluetoothAdapter?.bluetoothLeScanner?.startScan(filters, scanSettings, scanCallback)
    }

    fun stopScan() {
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
    }

    companion object {
        const val SERVICE_STRING = "4576d562-7e68-11ec-90d6-0242ac120003"
    }
}