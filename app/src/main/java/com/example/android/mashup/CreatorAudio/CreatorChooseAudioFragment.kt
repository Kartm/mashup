package com.example.android.mashup.CreatorAudio

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.mashup.Feed.CardAdapter
import com.example.android.mashup.Feed.MashupClickListener
import com.example.android.mashup.R
import com.example.android.mashup.Video
import com.example.android.mashup.databinding.FragmentCreatorChooseAudioBinding
import com.example.android.mashup.videoData.audio.AudioUri
import com.example.android.mashup.videoData.audio.AudioUriViewModel
import java.io.*


class CreatorChooseAudioFragment : Fragment(), MashupClickListener {
    private lateinit var audioUriViewModel: AudioUriViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentCreatorChooseAudioBinding.inflate(inflater, container, false);

        val selectAudioButton = binding.selectAudio;

        audioUriViewModel = ViewModelProvider(this).get(AudioUriViewModel::class.java);

        val cardAdapter = CardAdapter(
            GetVideoDataForUris(audioUriViewModel.readAllData.value), this
        );

        val orientation = resources.configuration.orientation
        val span = if (orientation == Configuration.ORIENTATION_LANDSCAPE) 2 else 1

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, span)
            adapter = cardAdapter
        };

        selectAudioButton.setOnClickListener {
            openFile()
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

        return binding.root;
    }

    val OPEN_THE_THING = 3

    fun openFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "audio/*"
        }
        startActivityForResult(intent, OPEN_THE_THING)
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
        return Video(audioUri.uri, thumbnail, audioUri.name, audioUri.length);
    }

    private fun convertCompressedByteArrayToBitmap(src: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(src, 0, src.size)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
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
                requireContext().applicationInfo.dataDir, "importedAudios"
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

            val thumbnailBmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            val canvas = Canvas(thumbnailBmp)
            canvas.drawColor(0xfff)

            val stream = ByteArrayOutputStream()
            thumbnailBmp!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val thumbnailByteArray = resources.getDrawable(R.drawable.icon_volume);
//            thumbnailBmp!!.recycle()

            if (thumbnailByteArray == null || time == null) {
                Toast.makeText(context, "something was null", Toast.LENGTH_SHORT).show();
                return

            };

//            if (thumbnailByteArray == null) {
//                Toast.makeText(context, "thumbnail was null", Toast.LENGTH_SHORT).show();
//                return
//            }





            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.icon_volume)

            val bitmap = Bitmap.createBitmap(
                drawable!!.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val c = Canvas(bitmap)
            drawable.setBounds(0, 0, c.width, c.height)
            drawable.draw(c)

            val s = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, s)
            val byteArray = s.toByteArray()
            bitmap.recycle()



            val durationInSeconds = time.toLong() / 1000;
            val audioUri =
                AudioUri(0, file.toUri().toString(), byteArray, title, durationInSeconds);
            audioUriViewModel.addAudioUri(audioUri);

            return;
        };


    }
}