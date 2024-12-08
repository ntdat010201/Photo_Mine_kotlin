package com.example.photomine.adapter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photomine.databinding.ItemVideoBinding
import com.example.photomine.model.ModelImage
import com.example.photomine.utils.FormatUtil

class VideoAdapter(
    private var videos: List<ModelImage>, private var context: Context
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {
    var onItemClickItem: ((ModelImage,Int) -> Unit)? = null


    fun updateVideo(videos: List<ModelImage>) {
        this.videos = videos
        notifyDataSetChanged()
    }

    inner class VideoViewHolder(binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        val itemVideo = binding.itemVideo
        val timeVideo = binding.timeVideo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            ItemVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]

        Glide.with(holder.itemView.context)
            .load(video.imageFile)
            .into(holder.itemVideo)

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, video.imageFile.toUri())
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val durationMs = durationStr?.toInt()
        retriever.release()

        holder.timeVideo.text = durationMs?.let { FormatUtil.formatDuration(it) }

        holder.itemView.setOnClickListener {
            onItemClickItem!!.invoke(video,position)
        }
    }
}