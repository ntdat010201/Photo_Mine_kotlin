package com.example.photomine.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.photomine.adapter.VideoAdapter
import com.example.photomine.databinding.ActivityVideoBinding
import com.example.photomine.viewmodel.ViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoBinding
    private lateinit var adapter: VideoAdapter
    private val viewModel: ViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initView()
        initListener()
    }

    private fun initData() {

        viewModel.loadImagesAndVideos()
        adapter = VideoAdapter(emptyList(), this)
        binding.rcvVideo.adapter = adapter
        binding.rcvVideo.layoutManager = GridLayoutManager(this, 4)

        viewModel.videosLiveData.observe(this, Observer { videos ->
            adapter.updateVideo(videos)
            binding.quantity.text = videos.size.toString()
        })
    }

    private fun initView() {

    }

    private fun initListener() {
        binding.back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        adapter.onItemClickItem = { video, position ->

            val intent = Intent(this, VideoViewActivity::class.java)
            intent.putExtra("model_video", video)
            intent.putExtra("position_video", position)
            startActivity(intent)
        }

    }

}