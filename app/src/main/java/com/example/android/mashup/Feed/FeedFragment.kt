package com.example.android.mashup.Feed

import android.content.res.Configuration
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.mashup.*
import com.example.android.mashup.databinding.FragmentFeedBinding
import java.io.File

class FeedFragment : Fragment(), MashupClickListener {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)

        val firstFragment = this

        videoList.clear()
        listAllVideos()

        if (videoList.isEmpty())
            binding.emptyMessage.visibility = View.VISIBLE
        else
            binding.emptyMessage.visibility = View.INVISIBLE

        val orientation = resources.configuration.orientation
        val span = if(orientation == Configuration.ORIENTATION_LANDSCAPE) 2 else 1

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, span)
            adapter = CardAdapter(videoList, firstFragment)
        }

        binding.fab.setOnClickListener { view ->
            val action =
                FeedFragmentDirections.actionFirstFragmentToCreatorFragment(
                    null,
                    null
                )

            findNavController().navigate(action)
        }
        return binding.root
    }

    override fun onClick(video: Video) {
        val action = FeedFragmentDirections.actionFirstFragmentToSecondFragment(video)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun listAllVideos() {
        val directory = File(
            requireContext().applicationInfo.dataDir, "results"
        )
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val files = directory.listFiles()
        if (files != null) {
            for (i in files.indices) {
                val file = files[i];
                val extension: String =
                    file.absolutePath.substring(file.absolutePath.lastIndexOf("."));
                if (!extension.equals(".mp4")) continue;

                val uri = file.toUri();
                val parcelFileDescriptor =
                    requireContext().contentResolver.openFileDescriptor(uri, "r")
                val fileDescriptor = parcelFileDescriptor!!.fileDescriptor;
                val retriever =
                    MediaMetadataRetriever(); //use one of overloaded setDataSource() functions to set your data source
                retriever.setDataSource(fileDescriptor);
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                var title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                if (title == null) {
                    title = uri.toString().split("/").last();
                }
                val thumbnail =
                    retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                retriever.release()


                if (thumbnail == null || time == null) {
                    Toast.makeText(context, "something was null", Toast.LENGTH_SHORT).show();
                    return

                };

                val durationInSeconds = time.toLong() / 1000;
                val video =
                    Video(uri.toString(), thumbnail, title, durationInSeconds, 0);
                videoList.add(video);

            }
        }
    }
}