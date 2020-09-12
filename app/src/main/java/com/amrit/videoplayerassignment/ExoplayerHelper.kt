package com.amrit.videoplayerassignment

import com.amrit.videoplayerassignment.model.Video
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


object ExoPlayerHelper {

    private lateinit var mPlayerView: PlayerView
    private lateinit var mVideo: Video
    private var exoPlayer: ExoPlayer? = null
    private var mediaSource: MediaSource? = null

    fun initializePlayer(playerView: PlayerView, video: Video) {
        exoPlayer = SimpleExoPlayer.Builder(playerView.context).build()
        exoPlayer!!.repeatMode = Player.REPEAT_MODE_ALL
        mPlayerView = playerView
        mVideo = video
        playerView.player = exoPlayer
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            playerView.context,
            Util.getUserAgent(playerView.context, "VideoPlayerAssignment"),
            null
        )
        mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(video.uri)
        exoPlayer!!.let {
            mediaSource!!.let {
                (exoPlayer as SimpleExoPlayer).prepare(
                    it,
                    true,
                    false
                )
            }
        }
        exoPlayer!!.playWhenReady = true
    }

    fun killPlayer() {
        if (exoPlayer != null) {
            exoPlayer!!.release()
            exoPlayer = null
            mediaSource = null
            mPlayerView.player = null
        }
    }

}