package com.example.kumandra.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
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

class ReportFragment : Fragment() {
    companion object{
        const val ARG_POSITION = "position"
        var STATUS: String? = null
        var ID: String? = null
        var TOKEN: String = ""
    }

    private lateinit var binding: FragmentReportBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       return when(item.itemId) {
            R.id.menu_filter -> {
                showFilter()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFilter() {
        val view = requireActivity().findViewById<View>(R.id.menu_filter)

        PopupMenu(requireContext(), view).apply {
            menuInflater.inflate(R.menu.filter_sort, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.filter -> {
                        var position = 0
                        arguments?.let { arg ->
                            position = arg.getInt(ARG_POSITION)
                        }
                        if(position == 1){
                            getReport(TOKEN, null, null)
                        } else{
                            getReport(TOKEN, ID, null)
                        }
                        true
                    }
                    R.id.filter1 -> {
                        var position = 0
                        arguments?.let { arg ->
                            position = arg.getInt(ARG_POSITION)
                        }
                        if(position == 1){
                            getReport(TOKEN, null, "2")
                        } else{
                            getReport(TOKEN, ID, "2")
                        }
                        true
                    }
                    R.id.filter2 -> {
                        var position = 0
                        arguments?.let { arg ->
                            position = arg.getInt(ARG_POSITION)
                        }
                        if(position == 1){
                            getReport(TOKEN, null, "3")
                        } else{
                            getReport(TOKEN, ID, "3")
                        }
                        true
                    }
                    R.id.filter3 -> {
                        var position = 0
                        arguments?.let { arg ->
                            position = arg.getInt(ARG_POSITION)
                        }
                        if(position == 1){
                            getReport(TOKEN, null, "4")
                        } else{
                            getReport(TOKEN, ID, "4")
                        }
                        true
                    }
                    R.id.filter4 -> {
                        var position = 0
                        arguments?.let { arg ->
                            position = arg.getInt(ARG_POSITION)
                        }
                        if(position == 1){
                            getReport(TOKEN, null, "5")
                        } else{
                            getReport(TOKEN, ID, "5")
                        }
                        true
                        }
                    else -> false
                }
            }
            show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = requireContext().dataStore
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext(), UserSession.getInstance(dataStore))
        )[MainViewModel::class.java]

        viewModel.getToken().observe(viewLifecycleOwner) {
            ReportFragment.TOKEN = it.token
        }

        viewModel.getUser().observe(viewLifecycleOwner) { id ->
            ReportFragment.ID = id.idStudent

        }

        var position = 0
        arguments?.let {
            position = it.getInt(ARG_POSITION)
        }
        if(position == 1){
            ID = null
            getReport(TOKEN, null, null)
        } else{
            getReport(TOKEN, ID, null)
        }
    }

    private fun getReport(token: String, id_student: String?, idStatus: String?) {
        val adapter = ReportAdapter(requireContext())
        adapter.notifyDataSetChanged()
        binding.rvStory.layoutManager = LinearLayoutManager(requireActivity())
        viewModel.getStories(token, id_student, idStatus).observe(viewLifecycleOwner){
            adapter.submitData(lifecycle, it)
        }
        binding.rvStory.adapter = adapter
      //  adapter.notifyDataSetChanged()
    }
}