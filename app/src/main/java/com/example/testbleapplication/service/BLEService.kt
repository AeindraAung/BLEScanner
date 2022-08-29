package com.example.testbleapplication.service

import android.app.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import com.example.testbleapplication.R
import com.example.testbleapplication.data.BLEDataSource
import com.example.testbleapplication.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class BLEService : Service() {
    @Inject
    lateinit var manager: BLEDeviceManager

    @Inject
    lateinit var dataSource: BLEDataSource

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        manager.getBLEdevices(scanCallback = scanCallback)
        //initForeGroundService()
    }

    private fun initForeGroundService() {
        startForeground()
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Timber.d("BLEService_result : $result")
            dataSource.updateResult(result)
            manager.stopScan()
        }

        override fun onScanFailed(errorCode: Int) {
            Timber.d("BLEService_error : $errorCode")
        }
    }

    private fun startForeground() {
        val channelId = createNotificationChannel("my_service", "My Background Service")

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
}