package com.example.testbleapplication.ui.main

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testbleapplication.R
import com.example.testbleapplication.ble.ConnectionEventListener
import com.example.testbleapplication.ble.ConnectionManager
import com.example.testbleapplication.databinding.ActivityMainBinding
import com.example.testbleapplication.service.BLEService
import com.example.testbleapplication.ui.detail.BLEDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private const val BLUETOOTH_PERMISSION_REQUEST_CODE = 9999
        private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1111
    }

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private lateinit var scannerAdapter: ScannerAdapter

    private val connectionEventListener by lazy {
        ConnectionEventListener().apply {
            onConnectionSetupComplete = { gatt ->
                Intent(this@MainActivity, BLEDetailActivity::class.java).also {
                    it.putExtra(BluetoothDevice.EXTRA_DEVICE, gatt.device)
                    startActivity(it)
                }
                ConnectionManager.unregisterListener(this)
            }
            onDisconnect = {
                Toast.makeText(
                    this@MainActivity,
                    "Disconnected or unable to connect to device.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initializeBluetoothOrRequestPermission()
        initViews()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        ConnectionManager.registerListener(connectionEventListener)
        if (!viewModel.isBleAdapterEnable())
            promptEnableBluetooth()
    }

    private fun initializeBluetoothOrRequestPermission() {
        val requiredPermissions = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            listOf(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            listOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        val missingPermissions = requiredPermissions.filter { permission ->
            checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isEmpty()) {
            viewModel.isPermissionGranted = true
        } else {
            requestPermissions(missingPermissions.toTypedArray(), BLUETOOTH_PERMISSION_REQUEST_CODE)
        }
    }

    private fun initViews() {
        scannerAdapter = ScannerAdapter {
            Timber.d("Connecting to ${it}")
            ConnectionManager.connect(it.device, this@MainActivity)
        }
        binding.apply {
            btnScan.setOnClickListener {
                viewModel.isScanning.value = !viewModel.isScanning.value!!
            }

            rvDevices.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = scannerAdapter
            }
        }
    }

    private fun initObservers() {
        viewModel.apply {
            isScanning.observe(this@MainActivity) {
                binding.btnScan.text =
                    if (it) getString(R.string.btn_stop_scan) else getString(R.string.btn_start_scan)
                if (it) {
                    startBleScan()
                }
            }

            bleResults.observe(this@MainActivity) {
                it?.let { data ->
                    scannerAdapter.submitList(listOf(data))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            BLUETOOTH_PERMISSION_REQUEST_CODE -> {
                if (grantResults.none { it != PackageManager.PERMISSION_GRANTED }) {
                    // all permissions are granted
                    viewModel.isPermissionGranted = true
                } else {
                    // some permissions are not granted
                    viewModel.isPermissionGranted = false
                    Toast.makeText(this, "You need some permission to allow", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }

    private fun promptEnableBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
    }

    private fun startBleScan() {
        if (viewModel.isPermissionGranted) {
            startBLEService()
        } else {
            Toast.makeText(this, "You need some permission to allow", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startBLEService() {
        val i = Intent(this, BLEService::class.java)
        this.startService(i)
    }

    private fun stopBleScan() {
        viewModel.stopBLEScan()
    }
}