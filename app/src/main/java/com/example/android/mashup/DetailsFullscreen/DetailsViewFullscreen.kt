package com.example.android.mashup.DetailsFullscreen

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.android.mashup.R
import com.example.android.mashup.databinding.FragmentDetailsViewFullscreenBinding
import com.example.android.mashup.databinding.PlayerControllerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import java.io.File
import android.content.ContentResolver
import android.content.Context
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController


/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class DetailsViewFullscreen : Fragment() {

    private var _binding: FragmentDetailsViewFullscreenBinding? = null
    lateinit var simpleExoPlayer: ExoPlayer
//    private var _binding2 : PlayerControllerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
//    private val binding2 get() = _binding2!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDetailsViewFullscreenBinding.inflate(inflater, container, false)
//        _binding2 = PlayerControllerBinding.inflate(inflater, container, false)

        val playerView = binding.player
//        val btnExitFull = binding2.exitFullscreenBtn

        simpleExoPlayer= ExoPlayer.Builder(this.requireContext()).build()
        playerView.player = simpleExoPlayer
        playerView.keepScreenOn = true
//        simpleExoPlayer.addListener(object : Player.Listener{
//            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                super.onPlayerStateChanged(playWhenReady, playbackState)
//            }
//        })

        val uri = resourceToUri(this.requireContext(), R.raw.video)

        val videoFile = File("E:\\Repositories\\AndroidStudioProjects-for-Uni\\mashup\\app\\src\\main\\res\\raw\\video.mp4")
        if (uri != null){
            val mediaItem = MediaItem.fromUri(uri)
            simpleExoPlayer.setMediaItem(mediaItem)
            simpleExoPlayer.prepare()
            simpleExoPlayer.play()
        }
        else{
            Log.i("full", "uri is null")
        }



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewLifecycleOwner, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                if (simpleExoPlayer != null) {
//                    simpleExoPlayer.setPlayWhenReady(false);
//                    simpleExoPlayer.stop();
//                    simpleExoPlayer.seekTo(0);
//                }
//                requireActivity().onBackPressedDispatcher.onBackPressed()
//            }
//
//        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
//
//        }
//
//        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun resourceToUri(context: Context, resID: Int): Uri? {
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    context.resources.getResourcePackageName(resID) + '/' +
                    context.resources.getResourceTypeName(resID) + '/' +
                    context.resources.getResourceEntryName(resID)
        )
    }

    override fun onStop() {
        super.onStop()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}