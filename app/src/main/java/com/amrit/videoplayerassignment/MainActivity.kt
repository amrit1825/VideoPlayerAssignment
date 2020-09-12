package com.amrit.videoplayerassignment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.amrit.videoplayerassignment.databinding.ActivityMainBinding
import com.amrit.videoplayerassignment.model.Video
import com.amrit.videoplayerassignment.viewmodel.READ_REQUEST_CODE
import com.amrit.videoplayerassignment.viewmodel.VideoViewModel

class MainActivity : AppCompatActivity(), VideoAdapter.OnVideoBookmarkListener {

    private lateinit var mActivityMainBinding: ActivityMainBinding
    private lateinit var mVideoViewModel: VideoViewModel
    private lateinit var pagerSnapHelper: PagerSnapHelper
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mVideoViewModel = ViewModelProviders.of(this).get(VideoViewModel::class.java)
        init()
        mVideoViewModel.init(this)
        setViewModelObservers()
    }

    private fun init() {
        mRecyclerView = mActivityMainBinding.videoRecyclerView
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(mRecyclerView)
    }

    private fun setViewModelObservers() {
        mVideoViewModel.getIsReadPermissionGranted().observe(this, Observer { isPermissionGranted ->
            handlePermission(isPermissionGranted)
        })
        mVideoViewModel.getVideoListLiveData().observe(this, Observer { videoList ->
            mRecyclerView.adapter = VideoAdapter(videoList, this)
        })
    }

    private fun handlePermission(isPermissionGranted: Boolean) {
        if (isPermissionGranted) {
            mVideoViewModel.getVideoList()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mVideoViewModel.getVideoList()
                }
            }
        }
    }

    override fun onVideoBookmark(video: Video) {
        mVideoViewModel.handleBookmarkChanged(video)
    }

    override fun onStop() {
        super.onStop()
        ExoPlayerHelper.killPlayer()
    }

}