package com.example.android.mashup.Creator

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.android.mashup.R
import com.example.android.mashup.databinding.FragmentCreatorBinding
import com.example.android.mashup.utils.AudioVideoMerger
import com.example.android.mashup.utils.FFMpegCallback
import java.io.File
import androidx.core.net.toUri

import android.os.Build
import androidx.annotation.RequiresApi
import android.media.MediaMetadataRetriever
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import com.example.android.mashup.utils.AudioWaveformGenerator
import com.example.android.mashup.utils.OutputType
import com.google.android.material.slider.RangeSlider
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity

import com.example.android.mashup.MainActivity

import com.google.android.material.dialog.MaterialAlertDialogBuilder





// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreatorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreatorFragment : Fragment(), FFMpegCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentCreatorBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatorBinding.inflate(inflater, container, false);

        binding.selectVideoButton.setOnClickListener { view ->
            findNavController().navigate(R.id.action_creatorFragment_to_creatorChooseVideoFragment)
        }

        binding.selectAudioButton.setOnClickListener { view ->
            findNavController().navigate(R.id.action_creatorFragment_to_creatorChooseAudioFragment)
        }

        binding.saveButton.setOnClickListener {
            findNavController().navigate(R.id.action_creatorFragment_to_cancelDialogFragment2)
        }

        val videoStream = resources.openRawResource(R.raw.video)
        val videoFile: File = createTempFile()

        videoStream.use { input ->
            videoFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val audioStream = resources.openRawResource(R.raw.audio)
        val audioFile: File = createTempFile()

        audioStream.use { input ->
            audioFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, audioFile.toUri())
        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val audioDurationMs = durationStr!!.toInt()

        val folder = File(
            requireContext().getExternalFilesDir(null),
            "Mashup"
        )
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val filename = File(folder, "video")

        // todo make it run simultaneously
        generateWaveform(audioFile, filename)
//        mergeAudioVideo(0.0f, 1.0f, audioDurationMs, audioFile, videoFile, filename)

        binding.rangeBar.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {
            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                var audioStart = slider.values[0]
                var audioEnd = slider.values[1]

                mergeAudioVideo(
                    audioStart,
                    audioEnd,
                    audioDurationMs,
                    audioFile,
                    videoFile,
                    filename
                )
            }
        })
        return binding.root
    }

    private fun generateWaveform(audioFile: File, filename: File) {
        AudioWaveformGenerator.with(requireContext())
            .setAudioFile(audioFile)
            .setOutputPath(filename!!.absolutePath)
            .setOutputFileName("waveform_" + System.currentTimeMillis() + ".png")
            .setCallback(this)
            .merge()
    }

    private fun mergeAudioVideo(
        audioStart: Float,
        audioEnd: Float,
        audioDurationMs: Int,
        audioFile: File,
        videoFile: File,
        filename: File
    ) {
        AudioVideoMerger.with(requireContext())
            .setAudioFile(audioFile)
            .setVideoFile(videoFile)
            .setAudioStartMs((audioStart * audioDurationMs).toInt())
            .setAudioDurationMs(((audioEnd - audioStart) * audioDurationMs).toInt())
            .setOutputPath(filename!!.absolutePath)
            .setOutputFileName("merged_" + System.currentTimeMillis() + ".mp4")
            .setCallback(this)
            .merge()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreatorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreatorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onProgress(progress: String) {
        Log.v("me", progress);
    }

    override fun onSuccess(convertedFile: File, type: OutputType) {
        Log.v("me", "success!");

        when (type) {
            OutputType.VIDEO -> {
                binding.videoView.setVideoURI(convertedFile.toUri())
                binding.videoView.seekTo(0)
                binding.videoView.start()
            }
            OutputType.WAVEFORM -> {
                binding.imageView.setImageURI(null) // because sometimes the change is not registered
                binding.imageView.setImageURI(convertedFile.toUri())
            }
        }


    }

    override fun onFailure(error: Exception) {
        Log.v("me", error.toString());
        error.printStackTrace()

    }

    override fun onNotAvailable(error: Exception) {
        Log.v("me", "not available");

    }

    override fun onFinish() {
        Log.v("me", "finish");

    }
}