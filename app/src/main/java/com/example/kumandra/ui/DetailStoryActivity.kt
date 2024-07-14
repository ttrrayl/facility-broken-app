package com.example.kumandra.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.data.remote.response.ListStoryItem
import com.example.kumandra.data.remote.response.Report
import com.example.kumandra.databinding.ActivityDetailStoryBinding
import com.example.kumandra.viewmodel.DetailStoryViewModel
import com.example.kumandra.viewmodel.ViewModelFactory

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
        binding.btEdit.setOnClickListener{ editReport()}
        binding.btDelete.setOnClickListener{ deleteReport()}
    }
    private fun viewModelConfig() {
        val pref = UserSession.getInstance(dataStore)
        viewModel =
            ViewModelProvider(this, ViewModelFactory(this, pref))[DetailStoryViewModel::class.java]

        viewModel.getUser().observe(this){
            val id = it.idStudent
            setLayout(id)
        }
    }

    private fun setLayout(idStudent: Int) {
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
        if (detailReport.id_student != idStudent){
            binding.btEdit.visibility = View.GONE
            binding.btDelete.visibility = View.GONE
        } else{
            binding.btEdit.visibility = View.VISIBLE
            binding.btDelete.visibility = View.VISIBLE
        }
    }

    private fun editReport() {
        TODO("Not yet implemented")
    }

    private fun deleteReport() {
        TODO("Not yet implemented")
    }



    companion object{
        const val STORY_DETAIL = "detail"
    }
}