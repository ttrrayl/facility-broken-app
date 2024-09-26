package com.example.kumandra.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.kumandra.data.remote.response.Report
import com.example.kumandra.databinding.ActivityDetailResponBinding

class DetailResponActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailResponBinding
    private var report: Report? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailResponBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        report = intent.getParcelableExtra(REPORT)
        binding.tvLevel.text = report?.level_report
        binding.tvStatus.text = report?.nama_status
        binding.tvDesc.text = report?.respon
        if (DetailResponActivity.sign == "1"){
            binding.apply {
                Glide.with(applicationContext)
                    .load(report?.processImage)
                    .into(ivDetailRespon)
                tvCreated.text = report?.processImageDate
            }
        } else{
            binding.apply {
                Glide.with(applicationContext)
                    .load(report?.completionImage)
                    .into(ivDetailRespon)
                tvCreated.text = report?.completionImageDate
            }
        }
    }

    companion object{
        const val REPORT = "report"
        var sign = ""
    }
}