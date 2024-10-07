package com.example.memorygame

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.models.BoardSize
import java.net.URI
import kotlin.math.min

class ImagePickerAdapter(private val context: Context, private val imageURIs: List<Uri>, private val boardSize: BoardSize,
        private val imageClickListener: ImageClickListener)
    : RecyclerView.Adapter<ImagePickerAdapter.ViewHolder>() {

        companion object
        {
            val MARGIN_SIZE = 10
        }

    fun interface ImageClickListener
    {
        fun onPlaceholderClicked()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val width = parent.width / boardSize.getWidth() - (2* MARGIN_SIZE)
        val height = parent.height / boardSize.getHeight() - (2* MARGIN_SIZE)
        val sideLen = min(width, height)

        val view = LayoutInflater.from(context).inflate(R.layout.activity_create_griditem, null, false)
        val layoutParams = view.findViewById<CardView>(R.id.gridItem_cardView).layoutParams as MarginLayoutParams

        layoutParams.width = sideLen
        layoutParams.height = sideLen
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)

        return ViewHolder(view)

    }

    override fun getItemCount() = boardSize.getNumPairs()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(position < imageURIs.size)
        {
            holder.bind(imageURIs[position])
        }
        else{
            holder.bind()
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView = itemView.findViewById<ImageView>(R.id.gridItem_ImageView)

        fun bind()
        {
            imageView.setOnClickListener()
            {
                imageClickListener.onPlaceholderClicked()
            }
        }

        fun bind(uri: Uri) {
            imageView.setImageURI(uri)
            imageView.setOnClickListener(null)
        }
    }

}
