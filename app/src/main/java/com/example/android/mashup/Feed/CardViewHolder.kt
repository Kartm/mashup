package com.example.android.mashup.Feed

import androidx.recyclerview.widget.RecyclerView
import com.example.android.mashup.Feed.MashupClickListener
import com.example.android.mashup.Video
import com.example.android.mashup.databinding.CardViewBinding

class CardViewHolder(
    private val cardCellBinding: CardViewBinding,
    private val clickListener: MashupClickListener
) : RecyclerView.ViewHolder(cardCellBinding.root)
{
    fun bindVideo(video: Video)
    {
        cardCellBinding.thumbnail.setImageResource(video.thubnail)
        cardCellBinding.title.text = video.title
        cardCellBinding.duration.text = video.duration.toString()

        cardCellBinding.cardView.setOnClickListener{
            clickListener.onClick(video)
        }
    }
}