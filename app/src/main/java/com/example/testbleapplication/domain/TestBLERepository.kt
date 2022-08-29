package com.example.testbleapplication.domain

import android.bluetooth.le.ScanResult
import kotlinx.coroutines.flow.MutableStateFlow

interface TestBLERepository {
    val bleData : MutableStateFlow<ScanResult?>
}