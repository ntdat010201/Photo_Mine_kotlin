package com.example.photomine.model

data class ModelSetting(
    val icon: Int,
    val title: String,
    val description: String? = null,
    val type: SettingType,
    var isEnabled: Boolean = false
)

enum class SettingType {
    SWITCH, NAVIGATION
}