package com.amrit.videoplayerassignment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amrit.videoplayerassignment.databinding.ItemVideoLayoutBinding
import com.amrit.videoplayerassignment.model.Video
import kotlinx.android.synthetic.main.item_video_layout.view.*


class VideoAdapter(
    private val videoList: List<Video>,
    private val onVideoBookmarkListener: OnVideoBookmarkListener
) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    interface OnVideoBookmarkListener {
        fun onVideoBookmark(video: Video)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemVideoLayoutBinding =
            ItemVideoLayoutBinding.inflate(layoutInflater, parent, false)
        return VideoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bindData(videoList[position])
        holder.setVideoData(videoList[position])
        holder.itemView.bookmarkImage.setOnClickListener {
            onVideoBookmarkListener.onVideoBookmark(videoList[position])
        }
    }

    class VideoViewHolder(private val binding: ItemVideoLayoutBinding) :
        RecyclerView.ViewHolder(binding.itemLayout) {

        fun bindData(video: Video) {
            binding.video = video
            binding.executePendingBindings()
        }

        fun setVideoData(video: Video) {
            ExoPlayerHelper.initializePlayer(itemView.videoView, video)
        }
    }

}