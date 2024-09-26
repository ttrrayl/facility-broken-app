package com.example.kumandra.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.kumandra.R
import com.example.kumandra.data.Results
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.data.remote.response.Report
import com.example.kumandra.data.remote.response.ReportDetail
import com.example.kumandra.databinding.ActivityDetailStoryBinding
import com.example.kumandra.ui.fragment.ReportFragment
import com.example.kumandra.viewmodel.DetailReportViewModel
import com.example.kumandra.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var detailReport: Report
    private lateinit var viewModel: DetailReportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailReport = intent.getParcelableExtra<Report>(STORY_DETAIL) as Report

        viewModelConfig()
        binding.btEdit.setOnClickListener{ editReport()}
        binding.btDelete.setOnClickListener{ deleteReport()}
        binding.ivBack.setOnClickListener{
            onBackPressed()
        }
        binding.cardHome.setOnClickListener {
            val intent = Intent(this, DetailResponActivity::class.java).apply {
                putExtra(DetailResponActivity.REPORT, detailReport)
            }
            startActivity(intent)
            DetailResponActivity.sign = "1"
        }
        binding.cardHome2.setOnClickListener {
            val intent = Intent(this, DetailResponActivity::class.java).apply {
                putExtra(DetailResponActivity.REPORT, detailReport)
            }
            startActivity(intent)
            DetailResponActivity.sign = "2"
        }
    }
    private fun viewModelConfig() {
        val pref = UserSession.getInstance(dataStore)
        viewModel =
            ViewModelProvider(this, ViewModelFactory(this, pref))[DetailReportViewModel::class.java]

        val reportId = intent.getStringExtra("ID REPORT")
        if (reportId != null) {
            viewModel.getDetailReport(reportId)
        }


//        viewModel.report.observe(this){
//            when(it){
//                is Results.Error -> {
//                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
//                }
//                is Results.Loading -> {
//
//                }
//                is Results.Success -> {
//                    val report = it.data?.report
//                    setUI(report)
//                }
//            }
//        }

        viewModel.getUser().observe(this){
                val id = it.idStudent
                setLayout(id)
        }

        viewModel.isLoading.observe(this){
            showLoading(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbDetail.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

//    private fun setUI(report: ReportDetail?) {
//        binding.apply {
//            Glide.with(applicationContext)
//                .load(report?.image_url)
//                .into(ivDetail)
//            tvCreated.text = report?.created_at
//     //       tvUpdate.text =  ":" + report?.updated_at
//            tvEmail.text =  report?.email
//            tvFacil.text =  report?.nama_detail_facilities
//            tvClass.text =  report?.nama_classes
//            tvDesc.text =  report?.description
//        }
//
//        if (report?.id_status == "5"){
//            binding.btEdit.visibility = View.GONE
//            binding.btDelete.visibility = View.GONE
//        } else {
//            binding.btEdit.visibility = View.VISIBLE
//            binding.btDelete.visibility = View.VISIBLE
//        }
//
//    }

//    private fun setUI(): Results<DetailReportResponses>? {
//
//    }

    private fun setLayout(idStudent: String) {
        binding.apply {
            Glide.with(applicationContext)
                .load(detailReport.image_url)
                .into(ivDetail)
            tvCreated.text = detailReport.created_at
        //    tvUpdate.text =  ":" + detailReport.updated_at
            tvEmail.text =  detailReport.email
            tvFacil.text =  detailReport.nama_detail_facilities
            tvClass.text =  detailReport.nama_classes
            tvDesc.text =  detailReport.description
            tvStatus.text = detailReport.nama_status
            tvUsername.text = detailReport.username


        }
        if (detailReport.id_student != idStudent){
            binding.btEdit.visibility = View.GONE
            binding.btDelete.visibility = View.GONE
        } else{
            if (detailReport.id_status == "2") {
                binding.btEdit.visibility = View.VISIBLE
                binding.btDelete.visibility = View.VISIBLE
            }
        }

        if (detailReport.id_status =="5"){
            binding.cardHome2.visibility = View.VISIBLE
            binding.cardHome.visibility = View.VISIBLE
        } else if (detailReport.id_status == "3"){
            binding.cardHome.visibility = View.VISIBLE
        }
    }

    private fun editReport() {
        val intent = Intent(this, AddStoryActivity::class.java)
        intent.putExtra(AddStoryActivity.report, detailReport)
        startActivity(intent)
    }

    private fun deleteReport() {
        AlertDialog.Builder(this).apply {
            setTitle("Konfirmasi")
            setMessage("Hapus laporan ini?")
            setPositiveButton("Ya") {_,_ ->
                viewModel.deleteReport(detailReport.id_report)
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            create()
            show()
        }

        viewModel.delReport.observe(this){
            when(it){
                is Results.Error -> {
                    Toast.makeText(this, it.data?.message, Toast.LENGTH_SHORT).show()
                }
                is Results.Loading -> {

                }
                is Results.Success -> {
                    Toast.makeText(this, it.data?.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }



    companion object{
        const val STORY_DETAIL = "detail"
    }
}