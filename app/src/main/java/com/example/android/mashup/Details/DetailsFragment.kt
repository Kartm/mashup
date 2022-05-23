package com.example.android.mashup.Details

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.mashup.R
import com.example.android.mashup.databinding.FragmentDetailsBinding

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

        val durationMins = args.video.duration.toInt() / 60;
        val durationSec = args.video.duration.toInt() % 60;
        binding.duration.text = "${durationMins} min ${durationSec} s"
        binding.title.text = args.video.title
        binding.thumbnail2.setImageBitmap(args.video.thumbnail)

        binding.thumbnail2.setOnClickListener {
            val action = DetailsFragmentDirections.actionSecondFragmentToDetailsViewFullscreen(args.video.uri)
            findNavController().navigate(action)
        }

        setHasOptionsMenu(true)

        return binding.root

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.details_menu, menu)
        if (null == getShareIntent().resolveActivity(requireActivity().packageManager)) {
            //hide the share menu if doesn't resolve
            menu?.findItem(R.id.share)?.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item!!.itemId) {
            R.id.share -> shareMashup()
        }
        return super.onOptionsItemSelected(item)
    }

    //todo change this to video
    private fun getShareIntent() : Intent {
        val args: DetailsFragmentArgs by navArgs()
        return ShareCompat.IntentBuilder.from(requireActivity())
//            .setType("video/mp4")
//            .setStream()
            .setText("lmao share")
            .setType("text/plain")
            .intent
    }

    private fun shareMashup() {
        startActivity(getShareIntent())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}