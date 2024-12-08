package com.example.photomine.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photomine.R
import com.example.photomine.adapter.SettingAdapter
import com.example.photomine.databinding.FragmentSettingBinding
import com.example.photomine.model.ModelSetting
import com.example.photomine.model.SettingType

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private lateinit var adapter: SettingAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(layoutInflater)
        initData()
        initView()
        initListener()
        return binding.root
    }

    private fun initData() {
        val settings = listOf(
//            ModelSetting(R.drawable.ic_favorite_red, "Notifications", "Enable app notifications", SettingType.SWITCH),
            ModelSetting(R.drawable.ic_camera, "Camera", "Camera,phân loại,tỉ lệ", SettingType.NAVIGATION),
            ModelSetting(R.drawable.ic_flashlight_orange, "Chế độ flash", "Bật,tắt", SettingType.NAVIGATION),
            ModelSetting(R.drawable.ic_collections_blue, "Bộ sưu tập", "Danh sách,lưới", SettingType.NAVIGATION),
            ModelSetting(R.drawable.ic_lock_person_green, "Bảo mật ảnh/video", "bằng mã pin,vân tay", SettingType.NAVIGATION),
            ModelSetting(R.drawable.ic_memory, "Thiết lập lưu trữ", "bộ nhớ,thẻ SD", SettingType.NAVIGATION),
            ModelSetting(R.drawable.ic_topic_language, "giao diện", "chủ đề,ngôn ngữ", SettingType.NAVIGATION),
            ModelSetting(R.drawable.ic_information_support, "Thông tin", "Phien bản,hỗ trợ", SettingType.NAVIGATION),
        )

        adapter = SettingAdapter(settings) { setting ->
            Toast.makeText(requireContext(), "${setting.title} clicked", Toast.LENGTH_SHORT).show()
        }

        binding.rcvSetting.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvSetting.adapter = adapter
    }

    private fun initView() {

    }

    private fun initListener() {

    }


}