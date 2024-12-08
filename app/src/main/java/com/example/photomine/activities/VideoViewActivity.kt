package com.example.photomine.activities

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.photomine.R
import com.example.photomine.databinding.ActivityVideoViewBinding
import com.example.photomine.model.ModelImage
import com.example.photomine.utils.FormatUtil
import com.example.photomine.viewmodel.ViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class VideoViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoViewBinding
    private val viewModel: ViewModel by viewModel()
    private var videos: List<ModelImage>? = null
    private var video: ModelImage? = null
    private lateinit var player: ExoPlayer
    private var handler = Handler()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initView()
        initListener()
    }

    private fun initData() {
        viewModel.loadImagesAndVideos()
        video = intent.getSerializableExtra("model_video") as ModelImage?
        currentIndex = intent.getIntExtra("position_video", -1)
        player = ExoPlayer.Builder(this).build()
        binding.playerView.player = player
        viewModel.videosLiveData.observe(this, Observer { videos ->
            this.videos = videos
        })
        setupPlayerForCurrentVideo()
    }


    private fun setupPlayerForCurrentVideo() {
        val mediaItem = MediaItem.fromUri(video!!.imageFile)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        binding.playOrPause.setImageResource(R.drawable.ic_pause_white)

    }

    private fun initView() {
        binding.tvName.text = File(video!!.imageFile).nameWithoutExtension
        createSeekbar()
    }

    private fun initListener() {
        userClickSeekbar()
        binding.previous.setOnClickListener {
            clickPrevious()
        }
        binding.next.setOnClickListener {
            clickNext()
        }
        binding.playOrPause.setOnClickListener {
            clickPlayOrPause()
        }
        binding.constraint.setOnClickListener {
            toggleControlsVisibility()
        }
    }

    private fun toggleControlsVisibility() {
        if (binding.constraint1.visibility == View.VISIBLE) {

            binding.constraint1.visibility = View.GONE
            binding.constraint2.visibility = View.GONE
        } else {
            binding.constraint1.visibility = View.VISIBLE
            binding.constraint2.visibility = View.VISIBLE

        }
    }


    private fun clickPlayOrPause() {
        if (player.playbackState == Player.STATE_ENDED) {
            // Nếu video đã kết thúc, phát lại từ đầu
            player.seekTo(0) // Đặt lại vị trí video về đầu
            player.play()
            binding.playOrPause.setImageResource(R.drawable.ic_pause_white)
        } else if (player.isPlaying) {
            player.pause()
            binding.playOrPause.setImageResource(R.drawable.ic_play_white)
        } else {
            player.play()
            binding.playOrPause.setImageResource(R.drawable.ic_pause_white)
        }
    }

    private fun clickNext() {

        if (currentIndex < videos!!.size - 1) {
            currentIndex++
            video = videos?.get(currentIndex)
            setupPlayerForCurrentVideo()
        } else {
            currentIndex = 0
            video = videos?.get(currentIndex)
            setupPlayerForCurrentVideo()
        }

    }

    private fun clickPrevious() {

        if (currentIndex > 0) {
            currentIndex--
            video = videos?.get(currentIndex)
            setupPlayerForCurrentVideo()
        } else {
            currentIndex = videos!!.size - 1
            video = videos?.get(currentIndex)
            setupPlayerForCurrentVideo()
        }

    }

    private fun userClickSeekbar() {
        // Xử lý sự kiện khi người dùng kéo SeekBar
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player.seekTo(progress.toLong())
                    binding.tvSeekBarStart.text = FormatUtil.formatDuration(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun createSeekbar() {
        // chuẩn bị
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    val duration = player.duration.toInt()
                    binding.seekBar.max = duration
                    binding.tvSeekbarEnd.text = FormatUtil.formatDuration(duration)
                } else if (state == Player.STATE_ENDED) {
                    // Khi video kết thúc
                    player.pause()
                    binding.playOrPause.setImageResource(R.drawable.ic_play_white)
                }

            }
        })

        // Cập nhật SeekBar khi video đang phát

        handler.post(object : Runnable {
            override fun run() {
                if (player.isPlaying) {
                    val currentPosition = player.currentPosition.toInt()
                    binding.seekBar.progress = currentPosition
                    binding.tvSeekBarStart.text = FormatUtil.formatDuration(currentPosition)
                }
                handler.postDelayed(this, 100)
            }
        })

    }


    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        player.seekTo(currentIndex.toLong()) // Khôi phục vị trí video đã lưu
    }

    override fun onPause() {
        super.onPause()
        if (player.isPlaying) {
            currentIndex = player.currentPosition.toInt() //Lưu vị trí hiện tại
            player.pause() // Tạm dừng video để khi quay lại không tự phát lại từ đầu
            binding.playOrPause.setImageResource(R.drawable.ic_play_white)

        }
    }

    override fun onStop() {
        super.onStop()
        if (player.isPlaying) {
            currentIndex = player.currentPosition.toInt()
            player.pause() // Tạm dừng video để khi quay lại không tự phát lại từ đầu
            binding.playOrPause.setImageResource(R.drawable.ic_play_white)

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        handler.removeCallbacksAndMessages(null)

    }


}