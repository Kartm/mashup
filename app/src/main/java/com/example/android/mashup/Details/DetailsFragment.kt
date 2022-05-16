package com.example.android.mashup.Details

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        binding.description.text = args.video.description
        binding.title.text = args.video.title
        binding.thumbnail2.setImageBitmap(args.video.thumbnail)

        binding.thumbnail2.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_detailsViewFullscreen)
        }

        setHasOptionsMenu(true)

        return binding.root

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.details_menu, menu)
        //todo add check if share is possible so it isn't displayed if can't be shared
        if (null == getShareIntent().resolveActivity(requireActivity().packageManager)) {
            //hide the share menu if doesn't resolve
            menu?.findItem(R.id.share)?.setVisible(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item!!.itemId) {
            R.id.share -> shareMashup()
            R.id.save -> saveMashup()
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


    private fun saveMashup() {
        Toast.makeText(context, "you tried to save", Toast.LENGTH_SHORT).show()
        //todo this
    }

    private fun shareMashup() {
        startActivity(getShareIntent())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}