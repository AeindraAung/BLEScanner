package com.example.testbleapplication.ui.main

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testbleapplication.R
import com.example.testbleapplication.databinding.ItemScannerBinding
import timber.log.Timber

class ScannerAdapter(
    private val onClickListener: ((device: ScanResult) -> Unit)
) : ListAdapter<ScanResult, ScannerAdapter.ScannerVHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScannerVHolder {
        val binding =
            ItemScannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScannerVHolder(
            binding = binding,
            onClickListener = onClickListener
        )
    }

    override fun onBindViewHolder(holder: ScannerVHolder, position: Int) {
        holder.bind(data = getItem(position))
    }

    inner class ScannerVHolder(
        private val binding: ItemScannerBinding,
        private val onClickListener: ((device: ScanResult) -> Unit)
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("MissingPermission")
        fun bind(data: ScanResult) {
            binding.apply {
                deviceName.text =
                    data.device.name ?: root.context.getString(R.string.label_unknown_source)
                address.text = data.device.address
                Timber.d("uuids: ${data.device.uuids.size}")
            }
            binding.root.setOnClickListener {
                onClickListener.invoke(data)
            }
        }
    }
}

val diffCallback = object : DiffUtil.ItemCallback<ScanResult>() {
    override fun areItemsTheSame(oldItem: ScanResult, newItem: ScanResult) =
        oldItem.device.hashCode() == newItem.device.hashCode()

    override fun areContentsTheSame(oldItem: ScanResult, newItem: ScanResult) =
        oldItem.device == newItem.device
}