package com.example.photomine.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.photomine.adapter.FavoriteAdapter
import com.example.photomine.databinding.ActivityFavoriteBinding
import com.example.photomine.viewmodel.ViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: FavoriteAdapter
    private val viewModel: ViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
        initListener()
    }

    private fun initData() {
        binding.rcvFavorite.layoutManager = GridLayoutManager(this, 4)
        adapter = FavoriteAdapter(emptyList())
        binding.rcvFavorite.adapter = adapter

        viewModel.favoriteLiveData.observe(this) { files ->
            adapter.updateImage(files)
        }

        adapter.onItemClickItem = { image ->
            val intent = Intent(this, PhotoViewActivity::class.java)
            intent.putExtra("model_image", image)
            startActivity(intent)
        }

    }

    private fun initView() {
    }

    private fun initListener() {
        binding.back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


    }
}