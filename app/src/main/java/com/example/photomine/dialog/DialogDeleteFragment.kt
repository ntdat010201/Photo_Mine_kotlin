package com.example.photomine.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.photomine.databinding.FragmentDialogDeleteBinding
import com.example.photomine.model.ModelImage
import com.example.photomine.viewmodel.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class DialogDeleteFragment(
    private var file: ModelImage
) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentDialogDeleteBinding
    private val viewModel: ViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogDeleteBinding.inflate(LayoutInflater.from(requireContext()))
        initData()
        initView()
        initListener()
        return binding.root
    }

    private fun initData() {

    }

    private fun initView() {

    }

    private fun initListener() {

        binding.tvExit.setOnClickListener {
            dismiss()
        }
        binding.tvDelete.setOnClickListener {
           viewModel.deleteImage(file)
            dismiss()
        }
    }

}