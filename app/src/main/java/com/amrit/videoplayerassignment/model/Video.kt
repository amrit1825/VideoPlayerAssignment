package com.amrit.videoplayerassignment.model

import android.graphics.drawable.Drawable
import android.net.Uri

data class Video(
    val uri: Uri,
    val name: String,
    val size: Int,
    var drawable: Drawable?
)

