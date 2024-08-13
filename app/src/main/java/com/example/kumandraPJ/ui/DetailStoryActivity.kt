package com.example.kumandraPJ.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.kumandraPJ.data.local.UserSession
import com.example.kumandraPJ.data.remote.response.Report
import com.example.kumandraPJ.databinding.ActivityDetailStoryBinding
import com.example.kumandraPJ.viewmodel.DetailStoryViewModel
import com.example.kumandraPJ.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var detailReport: Report
    private lateinit var viewModel: DetailStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelConfig()
        setLayout()
        binding.btResponse.setOnClickListener{
            val intent = Intent(this, AddStoryActivity::class.java).apply {
                putExtra(AddStoryActivity.REPORT, detailReport)
            }
            startActivity(intent)
        }
        binding.ivBack.setOnClickListener{
            onBackPressed()
        }
        if(detailReport.id_status == "4" || detailReport.id_status == "5"){
            binding.btResponse.visibility = View.GONE
        }
    }
    private fun viewModelConfig() {
        val pref = UserSession.getInstance(dataStore)
        viewModel =
            ViewModelProvider(this, ViewModelFactory(this, pref))[DetailStoryViewModel::class.java]

        viewModel.getUser().observe(this){
            val id = it.idPj

        }
    }

    private fun setLayout() {
        detailReport = intent.getParcelableExtra<Report>(STORY_DETAIL) as Report
        binding.apply {
            Glide.with(applicationContext)
                .load(detailReport.image_url)
                .into(ivDetail)
            tvCreated.text = detailReport.created_at
            tvEmail.text =  detailReport.email
            tvFacil.text =  detailReport.nama_detail_facilities
            tvClass.text =  detailReport.nama_classes
            tvDesc.text =  detailReport.description
            tvStatus.text =  detailReport.nama_status
            tvRespon.text = detailReport.content
            tvUsername.text = detailReport.username

        }
    }



    companion object{
        const val STORY_DETAIL = "detail"
    }
}