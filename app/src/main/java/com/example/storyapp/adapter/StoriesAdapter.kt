package com.example.storyapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ItemRvBinding
import com.example.storyapp.ui.DetailStoryActivity

class StoriesAdapter : PagingDataAdapter<ListStoryItem, StoriesAdapter.ViewHolder>(DIFF_CALLBACK){

    class ViewHolder (private val binding: ItemRvBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem, holder: ViewHolder){
            Glide.with(holder.itemView.context)
                .load(data.photoUrl)
                .into(holder.binding.ivGambarRv)

            holder.binding.tvUsername.text = data.name
            holder.binding.tvDeskripsi.text = data.description
            holder.binding.tvTanggal.text = data.createdAt
            holder.itemView.setOnClickListener{
                val intent = Intent(holder.itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.STORY_DETAIL, data)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data, holder)
        }

    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>(){
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}