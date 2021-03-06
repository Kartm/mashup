package com.example.android.mashup.DetailsFullscreen

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.mashup.databinding.FragmentDetailsViewFullscreenBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import android.content.ContentResolver
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import android.widget.ImageView


/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class DetailsViewFullscreen : Fragment() {

    private var _binding: FragmentDetailsViewFullscreenBinding? = null
    private val args: DetailsViewFullscreenArgs by navArgs()
    private lateinit var simpleExoPlayer: ExoPlayer

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailsViewFullscreenBinding.inflate(inflater, container, false)

        val playerView = binding.player

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        simpleExoPlayer= ExoPlayer.Builder(this.requireContext()).build()
        playerView.player = simpleExoPlayer
        playerView.keepScreenOn = true
        val uri = Uri.parse(args.uriString)

        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

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

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onPause() {
        super.onPause()
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false)
            simpleExoPlayer.stop()
            simpleExoPlayer.seekTo(0)
        }
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView: ImageView = requireView().findViewById<View>(com.example.android.mashup.R.id.exit_fullscreen_btn) as ImageView

        imageView.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun resourceToUri(context: Context, resID: Int): Uri? {
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    context.resources.getResourcePackageName(resID) + '/' +
                    context.resources.getResourceTypeName(resID) + '/' +
                    context.resources.getResourceEntryName(resID)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}