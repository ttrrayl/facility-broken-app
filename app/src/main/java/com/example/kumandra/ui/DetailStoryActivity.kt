package com.example.kumandra.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.kumandra.data.remote.response.ListStoryItem
import com.example.kumandra.data.remote.response.Report
import com.example.kumandra.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var detailReport: Report

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

      //  supportActionBar?.setDisplayHomeAsUpEnabled(true)

        detailReport = intent.getParcelableExtra<Report>(STORY_DETAIL) as Report
        binding.apply {
            Glide.with(applicationContext)
                .load(detailReport.image_url)
                .into(ivDetail)
            tvCreated.text = ":" + detailReport.created_at
            tvUpdate.text =  ":" + detailReport.updated_at
            tvEmail.text =  ":" + detailReport.email
            tvFacil.text =  ":" + detailReport.nama_detail_facilities
            tvClass.text =  ":" + detailReport.nama_classes
            tvDesc.text =  ":" + detailReport.description
        }

    }

    companion object{
        const val STORY_DETAIL = "detail"
    }
}