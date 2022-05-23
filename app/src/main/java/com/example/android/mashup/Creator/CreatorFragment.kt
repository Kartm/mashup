package com.example.android.mashup.Creator

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.mashup.CreatorVideo.CreatorChooseVideoFragmentDirections
import com.example.android.mashup.R
import com.example.android.mashup.databinding.FragmentCreatorBinding
import com.example.android.mashup.utils.AudioVideoMerger
import com.example.android.mashup.utils.AudioWaveformGenerator
import com.example.android.mashup.utils.FFMpegCallback
import com.example.android.mashup.utils.OutputType
import com.google.android.material.slider.RangeSlider
import java.io.File
import java.io.FileInputStream


class CreatorFragment : Fragment(), FFMpegCallback {
    private var audioFile: File? = null
    private var videoFile: File? = null
    private var filename: File? = null
    private var audioDurationMs: Int? = null
    private var _binding: FragmentCreatorBinding? = null

    private val args: CreatorFragmentArgs by navArgs()

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_creatorFragment_to_cancelDialogFragment2)
                }

            })
    }

    fun makeMashup(audioStart: Float, audioEnd: Float) {
        mergeAudioVideo(
            audioStart,
            audioEnd,
            audioDurationMs!!,
            audioFile!!,
            videoFile!!,
            filename!!
        )
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

        binding.videoView.setOnPreparedListener { it.isLooping = true }

        binding.playPauseButton.setOnClickListener {
            if(binding.videoView.isPlaying){
                binding.videoView.pause();
            } else {
                binding.videoView.start();
            }
        }
        binding.rewindButton.setOnClickListener {
            binding.videoView.seekTo(0)
            binding.videoView.start()
        }

        binding.rangeBar.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {}

            override fun onStopTrackingTouch(slider: RangeSlider) {
                var audioStart = slider.values[0]
                var audioEnd = slider.values[1]

                makeMashup(audioStart, audioEnd)
            }
        })

        if (args.videoUri != null) {
            videoUri = Uri.parse(args.videoUri);
        }

        if (args.audioUri != null) {
            audioUri = Uri.parse(args.audioUri);
        }

        if (videoUri != null && audioUri != null) {
            this.videoFile = File(videoUri!!.path)
            this.audioFile = File(audioUri!!.path)

            try {
                val mmr = MediaMetadataRetriever()
                val inputStream = FileInputStream(audioUri!!.path);
                mmr.setDataSource(inputStream.fd)

                val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                this.audioDurationMs = durationStr!!.toInt()

                val folder = File(
                    requireContext().getExternalFilesDir(null),
                    "Mashup"
                )
                if (!folder.exists()) {
                    folder.mkdirs()
                }
                this.filename = File(folder, "video")

                generateWaveform(this.audioFile!!, filename!!)


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.creator_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save -> {
                val action =
                    CreatorFragmentDirections.actionCreatorFragmentToSaveDialogFragment(
                        outputUri.toString()
                    )
                findNavController().navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
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
        var videoUri: Uri? = null;
        var audioUri: Uri? = null;
        var outputUri: Uri? = null;
    }

    override fun onSuccess(convertedFile: File, type: OutputType) {
        when (type) {
            OutputType.VIDEO -> {
                binding.videoView.setVideoURI(convertedFile.toUri())
                binding.videoView.seekTo(0)
                binding.videoView.start()
                outputUri = convertedFile.toUri()
            }
            OutputType.WAVEFORM -> {
                binding.imageView.setImageURI(null) // because sometimes the change is not registered
                binding.imageView.setImageURI(convertedFile.toUri())
                makeMashup(0.0f, 1.0f)
            }
        }
    }
}