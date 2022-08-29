package com.example.testbleapplication.ui.detail

import android.bluetooth.BluetoothGattCharacteristic
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testbleapplication.ble.printProperties
import com.example.testbleapplication.databinding.ItemCharacteristicBinding

class CharacteristicAdapter(
    private val items: List<BluetoothGattCharacteristic>,
    private val onClickListener: ((characteristic: BluetoothGattCharacteristic) -> Unit)
) : RecyclerView.Adapter<CharacteristicAdapter.VHCharacteristic>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHCharacteristic {
        val binding =
            ItemCharacteristicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VHCharacteristic(
            binding = binding,
            onClickListener = onClickListener
        )
    }

    override fun onBindViewHolder(holder: VHCharacteristic, position: Int) {
        holder.bind(data = items[position])
    }

    inner class VHCharacteristic(
        private val binding: ItemCharacteristicBinding,
        private val onClickListener: ((device: BluetoothGattCharacteristic) -> Unit)
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BluetoothGattCharacteristic) {
            binding.apply {
                titleUuid.text = data.uuid.toString()
                tvProperties.text = data.printProperties()
                root.setOnClickListener {
                    onClickListener.invoke(data)
                }
            }
        }
    }

    override fun getItemCount() = items.size
}