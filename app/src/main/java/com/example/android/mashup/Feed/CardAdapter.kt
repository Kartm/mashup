package com.example.android.mashup.Feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mashup.Feed.MashupClickListener
import com.example.android.mashup.Video
import com.example.android.mashup.databinding.CardViewBinding

class CardAdapter(
    private var videos: List<Video>,
    private val clickListener: MashupClickListener
)
    : RecyclerView.Adapter<CardViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder
    {
        val from = LayoutInflater.from(parent.context)
        val binding = CardViewBinding.inflate(from, parent, false)
        return CardViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int)
    {
        holder.bindVideo(videos[position])
    }

    override fun getItemCount(): Int = videos.size

    fun setData(videos: List<Video>)
    {
        this.videos = videos;
    }
}