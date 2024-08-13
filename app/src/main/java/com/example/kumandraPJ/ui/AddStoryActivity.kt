package com.example.kumandraPJ.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.example.kumandraPJ.R
import com.example.kumandraPJ.data.Results
import com.example.kumandraPJ.data.local.UserSession
import com.example.kumandraPJ.databinding.ActivityAddStoryBinding
import com.example.kumandraPJ.data.remote.response.Report
import com.example.kumandraPJ.viewmodel.AddStoryViewModel
import com.example.kumandraPJ.viewmodel.StatusViewModel
import com.example.kumandraPJ.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var statusViewModel: StatusViewModel
    private lateinit var detailReport: Report
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailReport = intent.getParcelableExtra<Report>(REPORT) as Report
        binding.buttonUpload.setOnClickListener { submit() }

        viewModelConfig()
        updateUIStatus()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.addRespon)

        if (detailReport.id_status != "2" ){
            binding.buttonUpload.text = getString(R.string.finish)
            binding.levelInputLayout.visibility = View.GONE
            binding.tvLevel.visibility = View.GONE
        }
        val level = mutableListOf<String>("Darurat","Biasa")
        val adapter = ArrayAdapter(this, R.layout.item_dropdown,level)
        (binding.levelInputLayout.editText as? AutoCompleteTextView)?.setAdapter(
            adapter
        )
    }
    private fun viewModelConfig(){
        addStoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserSession.getInstance(dataStore))
        )[AddStoryViewModel::class.java]
        addStoryViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        addStoryViewModel.msg.observe(this) {
            AlertDialog.Builder(this).apply {
                setTitle("STATE")
                setMessage(it)
                setPositiveButton("OK") { _, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        }

        statusViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserSession.getInstance(dataStore))
        )[StatusViewModel::class.java]

    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbAdd.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun updateUIStatus(){
        statusViewModel.getStatus()
        statusViewModel.status.observe(this){
            when(it){
                is Results.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is Results.Loading -> {
                }
                is Results.Success -> {
                    val items = mutableListOf<String>()
                    it.data?.status?.filter { status ->
                        status.id_status_respon != "1"
                    }?.forEach { status ->
                        items.add(status.nama_status)
                    }
                    val adapter = ArrayAdapter(this, R.layout.item_dropdown,items)
                    (binding.statusInputLayout.editText as? AutoCompleteTextView)?.setAdapter(
                        adapter
                    )
                }
            }
        }
    }

    private fun submit() {
        val desc = binding.etResp.text.toString()
        val selectedStatus = binding.statusInputLayout.editText?.text.toString()
        val selectedStatusId = statusViewModel.status.value?.data?.status?.find{
            it.nama_status == selectedStatus
        }?.id_status_respon
        val selectedLevel = binding.levelInputLayout.editText?.text.toString()

        when {
            selectedStatus.isEmpty() -> binding.statusInputLayout.editText?.error = "Perbarui status laporan"
        //    selectedLevel.isEmpty() -> binding.levelInputLayout.editText?.error = "Pilih level laporan"
            desc.isEmpty() -> binding.etResp.error = "Kolom tidak boleh kosong"

            else -> {
                val idReport = detailReport.id_report
                val idPj = detailReport.id_pj
                val respon = binding.etResp.text.toString()
                val idStatus = selectedStatusId.toString()
               addStoryViewModel.sendResponse(TOKEN, idReport, idPj, idStatus, selectedLevel, respon)

            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // or onBackPressed() to handle back navigation
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        var TOKEN = "token"
        const val REPORT = "report"
    }
}