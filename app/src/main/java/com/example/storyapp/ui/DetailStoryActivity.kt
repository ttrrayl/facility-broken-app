package com.example.storyapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var detailStory: ListStoryItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        detailStory = intent.getParcelableExtra<ListStoryItem>(STORY_DETAIL) as ListStoryItem
        binding.apply {
            Glide.with(applicationContext)
                .load(detailStory.photoUrl)
                .into(ivDetail)
            tvName.text = detailStory.name
            tvDeskripsi.text = detailStory.description
            tvTanggalDetail.text = detailStory.createdAt
        }

    }

    companion object{
        const val STORY_DETAIL = "detail"
    }
}