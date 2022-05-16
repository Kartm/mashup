package com.example.android.mashup.Details

import android.os.Bundle
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
        binding.thumbnail2.setImageBitmap(args.video.thubnail)

        binding.thumbnail2.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_detailsViewFullscreen)
        }


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}