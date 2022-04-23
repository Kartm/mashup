package com.example.android.mashup.Creator

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.android.mashup.R
import com.example.android.mashup.databinding.FragmentCreatorBinding
import android.widget.VideoView
import com.example.android.mashup.utils.AudioVideoMerger
import com.example.android.mashup.utils.FFMpegCallback
import com.example.android.mashup.utils.Utils
import java.io.File
import android.os.Environment
import androidx.core.net.toUri


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        _binding = FragmentCreatorBinding.inflate(inflater, container, false);

        binding.selectVideoButton.setOnClickListener { view ->
            findNavController().navigate(R.id.action_creatorFragment_to_creatorChooseVideoFragment)
        }

        binding.selectAudioButton.setOnClickListener { view ->
            findNavController().navigate(R.id.action_creatorFragment_to_creatorChooseAudioFragment)
        }

//        val videoUri = Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.video)
//        val audioUri = Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.audio)

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


        binding.testButton.setOnClickListener {
            val folder = File(
                requireContext().getExternalFilesDir(null),
                "Mashup"
            )
            if (!folder.exists()) {
                folder.mkdirs()
            }
            val filename = File(folder, "video")

                AudioVideoMerger.with(requireContext())
                    .setAudioFile(audioFile)
                    .setVideoFile(videoFile)
                    .setOutputPath(filename!!.absolutePath)
                    .setOutputFileName("merged_" + System.currentTimeMillis() + ".mp4")
                    .setCallback(this)
                    .merge()

            // to find the file, go to SD Card -> /Android/data/com.example.android.mashup/files/Mashup/video/
        }




        binding.videoView.setVideoURI(videoFile.toUri())
        binding.videoView.start()

        return binding.root
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

    override fun onSuccess(convertedFile: File, type: String) {
        Log.v("me", "success!");

        binding.videoView.setVideoURI(convertedFile.toUri())
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