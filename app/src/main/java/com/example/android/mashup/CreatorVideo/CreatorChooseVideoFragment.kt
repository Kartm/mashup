package com.example.android.mashup.CreatorVideo

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.mashup.Feed.CardAdapter
import com.example.android.mashup.Feed.MashupClickListener
import com.example.android.mashup.Video
import com.example.android.mashup.videoData.video.VideoUri
import com.example.android.mashup.videoData.video.VideoUriViewModel
import com.example.android.mashup.databinding.FragmentCreatorChooseVideoBinding
import java.io.*

class CreatorChooseVideoFragment : Fragment(), MashupClickListener {
    private lateinit var videoUriViewModel: VideoUriViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCreatorChooseVideoBinding.inflate(inflater, container, false)

        val selectVideoButton = binding.selectVideo;

        videoUriViewModel = ViewModelProvider(this).get(VideoUriViewModel::class.java)
        val cardAdapter = CardAdapter(
            GetVideoDataForUris(videoUriViewModel.readAllData.value),
            this@CreatorChooseVideoFragment
        );

        val orientation = resources.configuration.orientation
        val span = if(orientation == Configuration.ORIENTATION_LANDSCAPE) 2 else 1

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, span)
            adapter = cardAdapter
        }


        selectVideoButton.setOnClickListener {
            openFile(Uri.parse(""));
        }

        videoUriViewModel.readAllData.observe(viewLifecycleOwner, Observer { videoUri ->
            val videos = GetVideoDataForUris(videoUri);
            if (videos.isEmpty())
                binding.emptyMessage.visibility = View.VISIBLE
            else
                binding.emptyMessage.visibility = View.INVISIBLE
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
        return Video(videoUri.uri, thumbnail, videoUri.name, videoUri.length, "no");
    }

    override fun onClick(video: Video) {
        val action =
            CreatorChooseVideoFragmentDirections.actionCreatorChooseVideoFragmentToCreatorFragment(
                video.uri,
                null
            )
        findNavController().navigate(action)
    }

    val OPEN_THE_THING = 2

    fun openFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "video/mp4"
        }
        startActivityForResult(intent, OPEN_THE_THING)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val uri = data?.data;

        if (uri != null) {
            activity?.contentResolver?.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            val parcelFileDescriptor = requireContext().contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor;

            val retriever =
                MediaMetadataRetriever();
            retriever.setDataSource(fileDescriptor);
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            val title = uri.path!!.split("/").last().replace(" ", "_")

            val dir1 = File(
                requireContext().applicationInfo.dataDir, "importedVideos"
            )
            if (!dir1.exists()) {
                dir1.mkdirs()
            }

            val file = File(dir1, title)

            if (file.exists()) {
                file.delete()
                file.createNewFile()
            } else {
                file.createNewFile()
            }

            val originalBytes = (requireContext().contentResolver.openInputStream(uri))?.readBytes()

            val fos = FileOutputStream(file!!.path)
            fos.write(originalBytes)
            fos.close()


            val thumbnail = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            retriever.release()

            if (thumbnail == null || time == null) {
                Toast.makeText(context, "something was null", Toast.LENGTH_SHORT).show();
                return

            };
            val thumbnailByteArray = convertBitmapToByteArray(thumbnail);
            if (thumbnailByteArray == null) {
                Toast.makeText(context, "thumbnail was null", Toast.LENGTH_SHORT).show();
                return
            }
            val durationInSeconds = time.toLong() / 1000;
            val videoUri =
                VideoUri(0, file.toUri().toString(), thumbnailByteArray, title, durationInSeconds);
            videoUriViewModel.addVideoUri(videoUri);
            return;
        };
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray? {
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

    private fun convertCompressedByteArrayToBitmap(src: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(src, 0, src.size)
    }
}