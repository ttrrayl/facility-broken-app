package com.example.kumandra.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kumandra.R
import com.example.kumandra.adapter.ReportAdapter
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.databinding.FragmentHomeBinding
import com.example.kumandra.databinding.FragmentReportBinding
import com.example.kumandra.viewmodel.MainViewModel
import com.example.kumandra.viewmodel.ViewModelFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Setting")

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dataStore = requireContext().dataStore
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext(), UserSession.getInstance(dataStore))
        )[MainViewModel::class.java]

        viewModel.getToken().observe(viewLifecycleOwner) {
            HomeFragment.TOKEN = it.token
        }

        viewModel.getUser().observe(viewLifecycleOwner) { id ->
            binding.username.text = "Halo, "+id.username
            getReport(TOKEN, id.idStudent, null)
            viewModel.getTotalReports(id.idStudent,null).observe(viewLifecycleOwner){
                binding.tvTotal.text = it.toString()
            }
            viewModel.getTotalReports(id.idStudent,"2").observe(viewLifecycleOwner){
                binding.tvStatus2.text = it.toString()
            }
            viewModel.getTotalReports(id.idStudent,"3").observe(viewLifecycleOwner){
                binding.tvStatus3.text = it.toString()
            }
            viewModel.getTotalReports(id.idStudent,"4").observe(viewLifecycleOwner){
                binding.tvStatus4.text = it.toString()
            }
            viewModel.getTotalReports(id.idStudent,"5").observe(viewLifecycleOwner){
                binding.tvStatus5.text = it.toString()
            }
        }
    }
    private fun getReport(token: String, id_student: String, idStatus: String?) {
        val adapter = ReportAdapter(requireContext())
        adapter.notifyDataSetChanged()
        binding.rvStory.layoutManager = LinearLayoutManager(requireActivity())
        viewModel.getStories(token, id_student, idStatus).observe(viewLifecycleOwner){
            adapter.submitData(lifecycle, it)
        }
        binding.rvStory.adapter = adapter
    }

    companion object{
        var ID: String = ""
        var TOKEN: String = ""
        var USERNAME: String = ""

    }

}