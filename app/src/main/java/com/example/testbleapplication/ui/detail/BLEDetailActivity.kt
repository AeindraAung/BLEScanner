package com.example.testbleapplication.ui.detail

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testbleapplication.R
import com.example.testbleapplication.ble.ConnectionEventListener
import com.example.testbleapplication.ble.ConnectionManager
import com.example.testbleapplication.ble.toHexString
import com.example.testbleapplication.databinding.ActivityBleDetailBinding
import timber.log.Timber
import java.util.*

class BLEDetailActivity : AppCompatActivity() {
    private lateinit var device: BluetoothDevice

    private val characteristics by lazy {
        ConnectionManager.servicesOnDevice(device)?.flatMap { service ->
            service.characteristics ?: listOf()
        } ?: listOf()
    }

    private val characteristicAdapter: CharacteristicAdapter by lazy {
        CharacteristicAdapter(characteristics) { characteristic ->
            showCharacteristicOptions(characteristic)
        }
    }

    private lateinit var binding: ActivityBleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        ConnectionManager.registerListener(connectionEventListener)
        super.onCreate(savedInstanceState)
        binding = ActivityBleDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initView()
    }

    private fun initView() {
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            ?: error("Missing BluetoothDevice from MainActivity!")

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = getString(R.string.title_ble_detail)
        }

        binding.rvCharacteristics.apply {
            adapter = characteristicAdapter
            layoutManager = LinearLayoutManager(
                this@BLEDetailActivity,
                RecyclerView.VERTICAL,
                false
            )
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        ConnectionManager.unregisterListener(connectionEventListener)
        ConnectionManager.teardownConnection(device)
        super.onDestroy()
    }

    private fun showCharacteristicOptions(characteristic: BluetoothGattCharacteristic) {
        ConnectionManager.readCharacteristic(device, characteristic)
    }

    private val connectionEventListener by lazy {
        ConnectionEventListener().apply {
            onDisconnect = {
                Toast.makeText(
                    this@BLEDetailActivity,
                    "Disconnected from device.",
                    Toast.LENGTH_SHORT
                ).show()
                onBackPressed()
            }

            onCharacteristicRead = { _, characteristic ->
                Timber.d("Read from ${characteristic.uuid}: ${characteristic.value.toHexString()}")
            }

            onCharacteristicChanged = { _, characteristic ->
                Timber.d("Value changed on ${characteristic.uuid}: ${characteristic.value.toHexString()}")
            }
        }
    }
}