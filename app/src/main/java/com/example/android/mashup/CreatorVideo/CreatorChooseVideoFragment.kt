package com.example.android.mashup.CreatorVideo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.mashup.BuildConfig
import com.example.android.mashup.Feed.CardAdapter
import com.example.android.mashup.Feed.MashupClickListener
import com.example.android.mashup.Video
import com.example.android.mashup.data.VideoUri
import com.example.android.mashup.data.VideoUriViewModel
import com.example.android.mashup.databinding.FragmentCreatorChooseVideoBinding
import com.example.android.mashup.videoList
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.jar.Manifest


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
        videoUriViewModel.nukeTable();
        val cardAdapter = CardAdapter(
            GetVideoDataForUris(videoUriViewModel.readAllData.value),
            this@CreatorChooseVideoFragment
        );

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = cardAdapter
        }


        selectVideoButton.setOnClickListener {
//            getContent.launch("video/mp4")
            openFile(Uri.parse(""));
        }

        videoUriViewModel.readAllData.observe(viewLifecycleOwner, Observer { videoUri ->
            val videos = GetVideoDataForUris(videoUri);
            videos.forEach { Log.i("database", it.title) }
            cardAdapter.setData(videos);
        })


        return binding.root;
    }

    private fun GetVideoDataForUris(videoUri: List<VideoUri>?): List<Video> {
        val videos: MutableList<Video> = mutableListOf();
        if (videoUri != null) {
            for (uri in videoUri) {
                val video = GetVideoDataFromUri(uri)
                videos.add(video)
            }
        }
        return videos;
    }

    private fun GetVideoDataFromUri(videoUri: VideoUri): Video {
        val thumbnail: Bitmap = convertCompressedByteArrayToBitmap(videoUri.thumbnail)
        return Video(thumbnail, videoUri.name, videoUri.length, "no");
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


    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) {
            Toast.makeText(context, "No video chosen", Toast.LENGTH_SHORT).show();
        } else {
            requireContext().grantUriPermission(
                BuildConfig.APPLICATION_ID,
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            openFile(uri);

        }
    }

    // Request code for selecting a PDF document.
    val OPEN_THE_THING = 2

    fun openFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "video/mp4"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
//            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, OPEN_THE_THING)
    }

    private lateinit var bigUri: Uri;

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val uri = data?.data;
        if (uri != null) {
            Toast.makeText(context, uri.toString(), Toast.LENGTH_SHORT).show();
            val parcelFileDescriptor = requireContext().contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor;
            val retriever =
                MediaMetadataRetriever(); //use one of overloaded setDataSource() functions to set your data source
            retriever.setDataSource(fileDescriptor);
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            var title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            if (title == null) {
                title = uri.toString().split("/").last();
            }
            val thumbnail = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            retriever.release()


            if (thumbnail == null || time == null) {
                Toast.makeText(context, "something was null", Toast.LENGTH_SHORT).show();
                return

            };
            val convertedThumbnail = convertBitmapToByteArray(thumbnail);
            if (convertedThumbnail == null) {
                Toast.makeText(context, "thumbnail was null", Toast.LENGTH_SHORT).show();
                return
            }
            val durationInSeconds = time.toLong() / 1000;
            val videoUri =
                VideoUri(0, uri.toString(), convertedThumbnail, title, durationInSeconds);
            videoUriViewModel.addVideoUri(videoUri);
            Toast.makeText(context, "Added video", Toast.LENGTH_SHORT).show();
            return;
        };
        Toast.makeText(context, "No uri was gotten", Toast.LENGTH_SHORT).show();
    }

    fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray? {
        var baos: ByteArrayOutputStream? = null
        return try {
            baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            baos.toByteArray()
        } finally {
            if (baos != null) {
                try {
                    baos.close()
                } catch (e: IOException) {
                    Log.i(
                        "app",
                        "ByteArrayOutputStream was not closed"
                    )
                }
            }
        }
    }

    fun convertCompressedByteArrayToBitmap(src: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(src, 0, src.size)
    }


}