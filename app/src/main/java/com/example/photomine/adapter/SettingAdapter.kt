package com.example.photomine.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.photomine.databinding.ItemSettingBinding
import com.example.photomine.model.ModelSetting
import com.example.photomine.model.SettingType

class SettingAdapter(
    private val settings: List<ModelSetting>,
    private val onClick: (ModelSetting) -> Unit
) : RecyclerView.Adapter<SettingAdapter.SettingViewHolder>() {

    inner class SettingViewHolder(binding: ItemSettingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val icon = binding.ivIcon
        val title = binding.tvTitle
        val description = binding.tvDescription
        val switch = binding.switchSetting
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        return SettingViewHolder(
            ItemSettingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        val setting = settings[position]

        holder.icon.setImageResource(setting.icon)
        holder.title.text = setting.title
        holder.description.text = setting.description
        holder.description.visibility = if (setting.description != null) View.VISIBLE else View.GONE

        if (setting.type == SettingType.SWITCH) {
            holder.switch.visibility = View.VISIBLE
            holder.switch.isChecked = setting.isEnabled
            holder.switch.setOnCheckedChangeListener { _, isChecked ->
                setting.isEnabled = isChecked
                onClick(setting)
            }
        } else {
            holder.switch.visibility = View.GONE
            holder.itemView.setOnClickListener { onClick(setting) }
        }

    }

    override fun getItemCount(): Int = settings.size
}
