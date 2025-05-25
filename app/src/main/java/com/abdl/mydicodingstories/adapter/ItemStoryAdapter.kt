package com.abdl.mydicodingstories.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.abdl.mydicodingstories.data.remote.response.ListStoryItem
import com.abdl.mydicodingstories.databinding.ItemStoryBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ItemStoryAdapter : PagingDataAdapter<ListStoryItem, ItemStoryAdapter.ItemViewHolder>(
    DIFF_CALLBACK
) {
    private var onItemClickCallback: OnItemClickCallback? = null


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback?) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemBinding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }

        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(data)
        }
    }

    class ItemViewHolder(private val itemBinding: ItemStoryBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: ListStoryItem) {
            with(itemBinding) {
                tvItemName.text = item.name
                tvItemStory.text = item.description

                btnLike.setOnClickListener {
                    Toast.makeText(
                        itemView.context,
                        "Fitur Like belum tersedia",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                btnComments.setOnClickListener {
                    Toast.makeText(
                        itemView.context,
                        "Fitur Komentar belum tersedia",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                btnShare.setOnClickListener {
                    Toast.makeText(
                        itemView.context,
                        "Fitur Share belum tersedia",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                Glide.with(itemBinding.root)
                    .load(item.photoUrl)
                    .apply(RequestOptions().override(300, 300))
                    .into(pictureFeed)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem,
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem?)
    }
}