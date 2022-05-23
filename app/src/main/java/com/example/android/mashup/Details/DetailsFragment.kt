package com.example.android.mashup.Details

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.mashup.R
import com.example.android.mashup.databinding.FragmentDetailsBinding
import java.io.File


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val args: DetailsFragmentArgs by navArgs()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDetailsBinding.inflate(inflater, container, false)

        binding.description.text = args.video.description
        binding.title.text = args.video.title
        binding.thumbnail2.setImageBitmap(args.video.thumbnail)

        binding.thumbnail2.setOnClickListener {
            val action =
                DetailsFragmentDirections.actionSecondFragmentToDetailsViewFullscreen(args.video.uri)
            findNavController().navigate(action)
        }

        setHasOptionsMenu(true)

        return binding.root

    }

    override fun onSaveInstanceState(oldInstanceState: Bundle) {
        super.onSaveInstanceState(oldInstanceState)
        oldInstanceState.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.details_menu, menu)
//        if (null == getShareIntent().resolveActivity(requireActivity().packageManager)) {
//            //hide the share menu if doesn't resolve
//            menu?.findItem(R.id.share)?.isVisible = false
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item!!.itemId) {
            R.id.share -> shareMashup()
            R.id.save -> saveMashup()
        }
        return super.onOptionsItemSelected(item)
    }

/*    //todo change this to video
    private fun getShareIntent() : Intent {
        val args: DetailsFragmentArgs by navArgs()
        return ShareCompat.IntentBuilder.from(requireActivity())
//            .setType("video/mp4")
//            .setStream()
            .setText("lmao share")
            .setType("text/plain")
            .intent
    }*/

/*
//    https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed

    //todo change this to video
    private fun getShareIntent(): Intent {
        val videoFile = File(args.video.uri)
        val videoURI: Uri = videoFile.toUri();
        Log.i("share", "trying to share: $videoURI")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            FileProvider.getUriForFile(
//                requireContext(),
//                requireContext().getPackageName() + ".provider",
//                videoFile
//            )
//        } else Uri.fromFile(videoFile)
        return ShareCompat.IntentBuilder.from(requireActivity())
            .setType("video/*")
            .setStream(videoURI)
            .setChooserTitle("Share video...")
            .intent
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .createChooser()
    }
 */
 */

    //    https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
    //todo change this to video
    private fun getShareIntent() {
        val intentShareFile = Intent(Intent.ACTION_SEND)
        val fileWithinMyDir = File(args.video.uri)
        val certifiedUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", fileWithinMyDir);
        intentShareFile.type = "video/*"
        intentShareFile.putExtra(Intent.EXTRA_STREAM, certifiedUri)
        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "cool video")
        intentShareFile.putExtra(Intent.EXTRA_TEXT, "cool extra text")
        startActivity(Intent.createChooser(intentShareFile, "jupi"))
    }

    private fun saveMashup() {
        Toast.makeText(context, "you tried to save", Toast.LENGTH_SHORT).show()
        //todo this
    }

    private fun shareMashup() {
//        startActivity(getShareIntent())
        getShareIntent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}