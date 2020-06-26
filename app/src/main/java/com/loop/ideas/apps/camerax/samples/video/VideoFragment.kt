package com.loop.ideas.apps.camerax.samples.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.fragment.app.Fragment
import com.loop.ideas.apps.camerax.R

/*
 * Created by Christopher Elias on 22/06/2020.
 * christopher.elias@loop-ideas.com
 * 
 * Loop Ideas
 * Lima, Peru.
 */
class VideoFragment : Fragment() {

    private lateinit var preview: Preview
    private lateinit var videoCapture: VideoCapture

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_camera_x_video, container, false)



}