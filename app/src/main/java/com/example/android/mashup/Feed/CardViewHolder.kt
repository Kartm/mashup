package com.example.android.mashup.Feed

import androidx.recyclerview.widget.RecyclerView
import com.example.android.mashup.Video
import com.example.android.mashup.databinding.CardViewBinding

class CardViewHolder(
    private val cardCellBinding: CardViewBinding,
    private val clickListener: MashupClickListener
) : RecyclerView.ViewHolder(cardCellBinding.root)
{
    fun bindVideo(video: Video)
    {
        cardCellBinding.thumbnail.setImageBitmap(video.thumbnail)
        cardCellBinding.title.text = video.title

        val durationMins = video.duration.toInt() / 60;
        val durationSec = video.duration.toInt() % 60;
        cardCellBinding.duration.text  = "${durationMins} min ${durationSec} s"

        cardCellBinding.cardView.setOnClickListener{
            clickListener.onClick(video)
        }
    }
}