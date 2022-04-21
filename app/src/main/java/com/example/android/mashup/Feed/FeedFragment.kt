package com.example.android.mashup.Feed

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.mashup.*
import com.example.android.mashup.databinding.FragmentFeedBinding

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

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = CardAdapter(videoList, firstFragment)
        }

        populateVideos()

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

    private fun populateVideos() {
        val vid1 = Video(
            R.drawable.blackhole,
            "vid1",
            3,
            "asd"
        )
        videoList.add(vid1)
        val vid2 = Video(
            R.drawable.blackhole,
            "vid2",
            5,
            "lorem"
        )
        videoList.add(vid2)
        val vid3 = Video(
            R.drawable.blackhole,
            "vid3",
            3,
            "asd"
        )
        videoList.add(vid3)
        val vid4 = Video(
            R.drawable.blackhole,
            "vid4",
            5,
            "lorem"
        )
        videoList.add(vid4)
        val vid5 = Video(
            R.drawable.blackhole,
            "vid5",
            3,
            "asd"
        )
        videoList.add(vid5)
        val vid6 = Video(
            R.drawable.blackhole,
            "vid6",
            5,
            "lorem"
        )
        videoList.add(vid6)
    }
}