package com.loop.ideas.apps.camerax.samples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.loop.ideas.apps.camerax.R
import com.loop.ideas.apps.camerax.samples.photo.PhotoFragment
import com.loop.ideas.apps.camerax.samples.video.VideoFragment

class PreviewCameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_camera)
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                VideoFragment()
            )
            .commit()
    }
}