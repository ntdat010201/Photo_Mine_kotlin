package com.example.photomine.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.photomine.databinding.FragmentDetailDialogBinding
import com.example.photomine.model.ModelImage
import com.example.photomine.utils.FormatUtil
import java.io.File


class DetailDialogFragment(
    private var image: ModelImage
) : DialogFragment() {
    private lateinit var binding: FragmentDetailDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailDialogBinding.inflate(layoutInflater)
        initData()
        initView()
        return binding.root
    }

    private fun initData() {
    }

    private fun initView() {


        binding.back.setOnClickListener {
            dismiss()
        }
        binding.tvCalendar.text = FormatUtil.formatFileDate(image.dateAdded)
        val nameWithoutExtension = File(image.imageFile).nameWithoutExtension
        binding.tvName.text = nameWithoutExtension
        binding.tvUri.text = image.imageFile
        binding.tvSize.text = FormatUtil.formatFileSize(image.size)
        binding.tvQuality.text = FormatUtil.getImageQualityBySize(image.size)
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}