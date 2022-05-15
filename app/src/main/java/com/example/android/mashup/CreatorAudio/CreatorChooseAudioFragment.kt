package com.example.android.mashup.CreatorAudio

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.mashup.CreatorVideo.CreatorChooseVideoFragmentDirections
import com.example.android.mashup.Feed.CardAdapter
import com.example.android.mashup.Feed.MashupClickListener
import com.example.android.mashup.Video
import com.example.android.mashup.databinding.FragmentCreatorChooseAudioBinding
import com.example.android.mashup.videoData.audio.AudioUri
import com.example.android.mashup.videoData.audio.AudioUriViewModel
import com.example.android.mashup.videoData.video.VideoUri
import java.io.ByteArrayOutputStream
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreatorChooseAudioFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreatorChooseAudioFragment : Fragment(), MashupClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var audioUriViewModel: AudioUriViewModel

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

        val binding = FragmentCreatorChooseAudioBinding.inflate(inflater, container, false);

        val selectAudioButton = binding.selectAudio;

        audioUriViewModel = ViewModelProvider(this).get(AudioUriViewModel::class.java);

        audioUriViewModel.nukeTable();

        val cardAdapter = CardAdapter(
            GetVideoDataForUris(audioUriViewModel.readAllData.value), this
        );

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = cardAdapter
        };

        selectAudioButton.setOnClickListener {
            openFile(Uri.parse(""))
        }

        audioUriViewModel.readAllData.observe(viewLifecycleOwner, Observer { audioUri ->
            val audios = GetVideoDataForUris(audioUri);
            if (audios.isEmpty())
                binding.emptyMessage.visibility = View.VISIBLE
            else
                binding.emptyMessage.visibility = View.INVISIBLE
            audios.forEach { Log.i("database", it.title) }
            cardAdapter.setData(audios);
        })


        // Inflate the layout for this fragment
        return binding.root;
    }

    val OPEN_THE_THING = 3

    fun openFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/*"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
//            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, OPEN_THE_THING)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreatorChooseAudioFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreatorChooseAudioFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(video: Video) {
        val action =
            CreatorChooseAudioFragmentDirections.actionCreatorChooseAudioFragmentToCreatorFragment(
                null,
                video.uri
            )
        findNavController().navigate(action)
    }

    private fun GetVideoDataForUris(audioUri: List<AudioUri>?): List<Video> {
        val videos: MutableList<Video> = mutableListOf();
        if (audioUri != null) {
            for (uri in audioUri) {
                val video = GetAudioDataFromUri(uri)
                videos.add(video)
            }
        }
        return videos;
    }

    private fun GetAudioDataFromUri(audioUri: AudioUri): Video {
        val thumbnail: Bitmap = convertCompressedByteArrayToBitmap(audioUri.thumbnail)
        return Video(audioUri.uri, thumbnail, audioUri.name, audioUri.length, "no");
    }

    fun convertCompressedByteArrayToBitmap(src: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(src, 0, src.size)
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
            val thumbnail = retriever.


            if (thumbnail == null || time == null) {
                Toast.makeText(context, "something was null", Toast.LENGTH_SHORT).show();
                return

            };

//            val convertedThumbnail = convertBitmapToByteArray(thumbnail);
            val convertedThumbnail = thumbnail;

            if (convertedThumbnail == null) {
                Toast.makeText(context, "thumbnail was null", Toast.LENGTH_SHORT).show();
                return
            }
            val durationInSeconds = time.toLong() / 1000;
            val audioUri =
                AudioUri(0, uri.toString(), convertedThumbnail, title, durationInSeconds);
            audioUriViewModel.addAudioUri(audioUri);
            Toast.makeText(context, "Added audio", Toast.LENGTH_SHORT).show();
            return;
        };
        Toast.makeText(context, "No uri was gotten", Toast.LENGTH_SHORT).show();
    }
}