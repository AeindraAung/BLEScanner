package com.example.testbleapplication.data

import android.bluetooth.le.ScanResult
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

interface BLEDataSource {
    val bleData : MutableStateFlow<ScanResult?>

    fun updateResult(data: ScanResult)
}

class BLEDataSourceImpl @Inject constructor() : BLEDataSource {
    private val _data = MutableStateFlow<ScanResult?>(null)
    override val bleData: MutableStateFlow<ScanResult?>
        get() = _data

    override fun updateResult(data: ScanResult) {
        bleData.value = data
    }
}