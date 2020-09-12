package com.amrit.videoplayerassignment.viewmodel

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amrit.videoplayerassignment.R
import com.amrit.videoplayerassignment.model.Video

const val READ_REQUEST_CODE = 99
const val VIDEO_PLAYER_SHARED_PREFERENCE = "video_player_shared_preference"

class VideoViewModel : ViewModel() {

    private var mVideoListLiveData: MutableLiveData<List<Video>> = MutableLiveData()
    private var mIsReadPermissionGranted: MutableLiveData<Boolean> = MutableLiveData()
    private val mCurrentPlayingVideo: MutableLiveData<Video> = MutableLiveData()
    private lateinit var mContext: Context
    private lateinit var mVideoPlayerSharedPreferences: SharedPreferences
    private lateinit var mVideoPlayerEditor: SharedPreferences.Editor

    private val videoList = mutableListOf<Video>()
    private val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.SIZE
    )
    private val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

    fun init(context: Context) {
        mContext = context
        mIsReadPermissionGranted.value = checkReadPermission()
        mVideoPlayerSharedPreferences =
            mContext.getSharedPreferences(VIDEO_PLAYER_SHARED_PREFERENCE, Context.MODE_PRIVATE)
    }

    fun setCurrentPlayingVideo(video: Video) {
        mCurrentPlayingVideo.value = video
    }

    fun getIsReadPermissionGranted(): MutableLiveData<Boolean> {
        return mIsReadPermissionGranted
    }

    fun getVideoListLiveData(): MutableLiveData<List<Video>> {
        return mVideoListLiveData
    }

    fun getVideoList() {
        mContext.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val size = cursor.getInt(sizeColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                videoList += Video(
                    contentUri,
                    name,
                    size,
                    mContext.getDrawable(R.drawable.ic_unfilled_star)
                )
            }
        }
        mVideoListLiveData.value = videoList
    }

    private fun checkReadPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun handleBookmarkChanged(video: Video) {
        videoList.remove(video)
        mVideoPlayerEditor = mVideoPlayerSharedPreferences.edit()
        if (mVideoPlayerSharedPreferences.getBoolean(video.uri.toString(), false)) {
            mVideoPlayerEditor.putBoolean(video.uri.toString(), false)
            videoList.add(
                Video(
                    video.uri,
                    video.name,
                    video.size,
                    mContext.getDrawable(R.drawable.ic_unfilled_star)
                )
            )
        } else {
            mVideoPlayerEditor.putBoolean(video.uri.toString(), true)
            videoList.add(
                Video(
                    video.uri,
                    video.name,
                    video.size,
                    mContext.getDrawable(R.drawable.ic_filled_star)
                )
            )
        }
        mVideoPlayerEditor.apply()
        mVideoListLiveData.value = videoList
    }

}