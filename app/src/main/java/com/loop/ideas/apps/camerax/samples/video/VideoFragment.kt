package com.loop.ideas.apps.camerax.samples.video

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.core.impl.PreviewConfig
import androidx.camera.core.impl.VideoCaptureConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.animation.doOnCancel
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.loop.ideas.apps.camerax.R
import com.loop.ideas.apps.camerax.samples.photo.PhotoFragment
import kotlinx.android.synthetic.main.fragment_camera_x_photo.*
import java.io.File

/*
 * Created by Christopher Elias on 22/06/2020.
 * christopher.elias@loop-ideas.com
 * 
 * Loop Ideas
 * Lima, Peru.
 */
@SuppressLint("RestrictedApi")
class VideoFragment : Fragment() {

    companion object {
        private const val TAG = "VideoFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private val fabRecordVideo : Button? by lazy {
        view?.findViewById<Button>(R.id.camera_record_button)
    }
    private var camera: Camera? = null
    private lateinit var preview: Preview
    private lateinit var videoCapture: VideoCapture
    private lateinit var outputDirectory: File // The Folder where all the files will be stored
    private var isRecording = false
    private val animateRecord by lazy {
        ObjectAnimator.ofFloat(fabRecordVideo, View.ALPHA, 1f, 0.5f).apply {
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            doOnCancel { fabRecordVideo?.alpha = 1f }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_camera_x_video, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Init the output folder
        outputDirectory = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath
                ?: requireContext().externalMediaDirs.first().absolutePath
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionsResult")
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            Log.i(TAG, "REQUEST_CODE_PERMISSIONS")
            if (allPermissionsGranted()) {
                Log.i(TAG, "Start camera")
                startCamera()
            } else {
                Toast.makeText(requireActivity(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        }
    }

    private fun allPermissionsGranted()  = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireActivity().baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        //startCamera()
        fabRecordVideo?.setOnClickListener { recordVideo() }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        // The ratio for the output video and preview
        val ratio = AspectRatio.RATIO_16_9

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                .build()

            // The Configuration of how we want to capture the video
            val videoCaptureConfig = VideoCaptureConfig.Builder().apply {
                setTargetAspectRatio(ratio) // setting the aspect ration
                setVideoFrameRate(24) // setting the frame rate to 24 fps
            }.useCaseConfig

            videoCapture = VideoCapture(videoCaptureConfig)

            // Select back camera
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, videoCapture)
                preview.setSurfaceProvider(viewFinder.createSurfaceProvider())
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireActivity()))
    }


    private fun recordVideo() {
        // Get a stable reference of the modifiable image capture use case
        val videoCapture = videoCapture ?: return
        // Create the output file
        val videoFile = File(outputDirectory, "${System.currentTimeMillis()}.mp4")

        if (!isRecording) {
            animateRecord.start()

            // Capture the video, first parameter is the file where the video should be stored, the second parameter is the callback after racording a video
            videoCapture.startRecording(
                videoFile,
                ContextCompat.getMainExecutor(requireActivity()),
                object : VideoCapture.OnVideoSavedCallback {
                    override fun onVideoSaved(file: File) {
                        val msg = "Video saved in ${file.absolutePath}"
                        Log.d(TAG, msg)
                    }
                    override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                        // This function is called if there is some error during the video recording process
                        //animateRecord.cancel()
                        val msg = "Video capture failed: $message"
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, msg)
                        cause?.printStackTrace()
                    }
                }
            )
        } else {
            animateRecord.cancel()
            videoCapture.stopRecording()
        }
        isRecording = !isRecording
    }



}