package com.example.testbleapplication.ui.main

import android.bluetooth.le.ScanResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.testbleapplication.domain.TestBLERepository
import com.example.testbleapplication.service.BLEDeviceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: TestBLERepository,
    private val bleManager: BLEDeviceManager
) : ViewModel() {

    val bleResults: LiveData<ScanResult?>
        get() = repository.bleData.asLiveData()

    val isScanning = MutableLiveData(false)

    var isPermissionGranted = false

    fun isBleAdapterEnable(): Boolean {
        bleManager.getBleAdapter()?.let {
            return it.isEnabled
        } ?: return false
    }

    fun stopBLEScan() {
        bleManager.stopScan()
    }
}