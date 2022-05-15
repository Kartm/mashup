package com.example.android.mashup.Feed

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.mashup.*
import com.example.android.mashup.databinding.FragmentFeedBinding
import com.example.android.mashup.videoData.video.VideoUri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FeedFragment : Fragment(), MashupClickListener {

    private var _binding: FragmentFeedBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFeedBinding.inflate(inflater, container, false)

        val firstFragment = this

        listAllVideos()

        if (videoList.isEmpty())
            binding.emptyMessage.visibility = View.VISIBLE
        else
            binding.emptyMessage.visibility = View.INVISIBLE

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = CardAdapter(videoList, firstFragment)
        }


        binding.fab.setOnClickListener { view ->
            findNavController().navigate(R.id.action_FirstFragment_to_creatorFragment)
        }




        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, )
//        }


    }

    override fun onClick(video: Video)
    {
//        val intent = Intent(context, SecondFragment::class.java)
//        intent.putExtra(VIDEO_ID_EXTRA, video.id)
//        startActivity(intent)
        val action = FeedFragmentDirections.actionFirstFragmentToSecondFragment(video)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun listAllVideos()
    {
        val folder = File(
            requireContext().getExternalFilesDir(null),
            "Mashup"
        )

        if (!folder.exists()) {
            folder.mkdirs()
        }
        val directory = File(folder, "video")
//        Log.d("Files", "Path: $path")
//        val directory = File(path)
        val files = directory.listFiles()
        if (files != null) {
            Log.d("Files", "Size: " + files.size)
        }
        if (files != null) {
            for (i in files.indices) {
                val file = files[i];
                val extension: String = file.absolutePath.substring(file.absolutePath.lastIndexOf("."));
                Log.i("fileGetting", extension)
                if(!extension.equals(".mp4")) continue;

                val uri = file.toUri();
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

                val durationInSeconds = time.toLong() / 1000;
                val video =
                    Video(uri.toString(), thumbnail, title, durationInSeconds, "no", 0);
                videoList.add(video);

            }
        }
    }
}