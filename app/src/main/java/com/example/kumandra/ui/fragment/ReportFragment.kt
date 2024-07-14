package com.example.kumandra.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kumandra.R
import com.example.kumandra.adapter.ReportAdapter
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.databinding.FragmentReportBinding
import com.example.kumandra.ui.AddStoryActivity
import com.example.kumandra.viewmodel.AddStoryViewModel
import com.example.kumandra.viewmodel.MainViewModel
import com.example.kumandra.viewmodel.ViewModelFactory


/**
 * A simple [Fragment] subclass.
 * Use the [ReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Setting")
class ReportFragment : Fragment() {
    companion object{
        const val ARG_POSITION = "position"
        const val ARG_USERNAME = "username"
    }



    private lateinit var binding: FragmentReportBinding
    private lateinit var viewModel: MainViewModel

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
        
       
    }

  

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = requireContext().dataStore
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext(), UserSession.getInstance(dataStore))
        )[MainViewModel::class.java]



//        viewModel.getUser().observe(viewLifecycleOwner) { id ->
//            this.idStudent = id.idStudent
//
//        }



        var token = ""
        var position = 0
        arguments?.let {
            position = it.getInt(ARG_POSITION)
        }
        if(position == 1){
            viewModel.getToken().observe(viewLifecycleOwner) {
                token = it.token
                getReport(token, null)
            }

        } else{
            viewModel.getUser().observe(viewLifecycleOwner) { id ->
                getReport(token, id.idStudent)
            }
        }
    }

    private fun getReport(token: String, id_student: Int?) {
        val adapter = ReportAdapter()
        binding.rvStory.layoutManager = LinearLayoutManager(requireActivity())
      //  binding.rvStory.setHasFixedSize(true)
        viewModel.getStories(token, id_student).observe(viewLifecycleOwner){

            adapter.submitData(lifecycle, it)
        }
        binding.rvStory.adapter = adapter
    }





}