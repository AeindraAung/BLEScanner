package com.example.testbleapplication.domain

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.testbleapplication.data.BLEDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class TestBLERepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val dataSource: BLEDataSource
) : TestBLERepository {

    override val bleData: MutableStateFlow<ScanResult?>
        get() = dataSource.bleData

}