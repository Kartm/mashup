package com.example.android.mashup.CreatorVideo

import android.R
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.Messenger
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.mashup.Feed.CardAdapter
import com.example.android.mashup.Feed.MashupClickListener
import com.example.android.mashup.Video
import com.example.android.mashup.data.VideoUri
import com.example.android.mashup.data.VideoUriViewModel
import com.example.android.mashup.databinding.FragmentCreatorChooseVideoBinding
import com.example.android.mashup.videoList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreatorChooseVideoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreatorChooseVideoFragment : Fragment(), MashupClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var videoUriViewModel: VideoUriViewModel

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) {
            Toast.makeText(context, "No video chosen", Toast.LENGTH_SHORT).show();
        } else {

            getContext()?.getContentResolver()?.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

            // if we specify 0, it should autogenerate index (I hope)
            val videoUri = VideoUri(0, uri.toString());
            videoUriViewModel.addVideoUri(videoUri);
            Toast.makeText(context, "Added video", Toast.LENGTH_SHORT).show();
        }
    }

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

        val binding = FragmentCreatorChooseVideoBinding.inflate(inflater, container, false)

        val selectVideoButton = binding.selectVideo;

        videoUriViewModel = ViewModelProvider(this).get(VideoUriViewModel::class.java)
        val cardAdapter = CardAdapter(videoList, this@CreatorChooseVideoFragment);

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = cardAdapter
        }


        selectVideoButton.setOnClickListener {
            getContent.launch("video/mp4")
        }

        videoUriViewModel.readAllData.observe(viewLifecycleOwner, Observer { videoUri ->
            val videos = GetVideoDataForUris(videoUri)
            cardAdapter.setData(videos)
        })


        return binding.root;
    }

    private fun GetVideoDataForUris(videoUri: List<VideoUri>?): List<Video> {
        val videos: MutableList<Video> = mutableListOf();
        if (videoUri != null) {
            for (uri in videoUri) {
                val video = GetVideoDataFromUri(uri)
                if (video != null) {
                    videos.add(video)
                }
            }
        }
        return videos;
    }

    private fun GetVideoDataFromUri(videoUri: VideoUri): Video? {
        val thumb = ThumbnailUtils.createVideoThumbnail(
            videoUri.uri,
            MediaStore.Images.Thumbnails.MINI_KIND
        );

//        requestPermissionLauncher.launch(android.Manifest.permission.ACTION_OPEN_DOCUMENT)
//        val file = File(Uri.parse(videoUri.uri));
        val uri = Uri.parse(videoUri.uri)
        val parcelFileDescriptor = requireContext().contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor;
        val retriever =
            MediaMetadataRetriever(); //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(fileDescriptor);
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val thumbnail = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        retriever.release()

        if (thumbnail == null || title == null || time == null) return null;
        val durationInSeconds = time.toLong() / 1000;

        return Video(thumbnail, title, durationInSeconds, "none");
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreatorChooseVideoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreatorChooseVideoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(video: Video) {
        TODO("Not yet implemented")
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted ->
            {
                if (isGranted) {

                } else {

                }
            }
        }

//    private fun requestPermission() {
//        when {
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permissions.ACTION_OPEN_DOCUMENT
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                // granted
//            }
//            ActivityCompat.shouldShowRequestPermissionRationale(
//                this, Manifest.permission.ACTION_OPEN_DOCUMENT
//            ) -> {
//                // additional rationale should be displayed
//            }
//            else -> {
//                // Permission has not been asked yet
//            }
//
//        }
//    }


}