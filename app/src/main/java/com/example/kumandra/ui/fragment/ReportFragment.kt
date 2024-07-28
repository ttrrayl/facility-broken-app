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


/**
 * A simple [Fragment] subclass.
 * Use the [ReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Setting")
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
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       return when(item.itemId) {
            R.id.menu_filter -> {
               showFilter()
                true
            }
            R.id.menu_logout -> {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle("CONFIRMATION")
                    setMessage("Logout of your account?")
                    setPositiveButton("Yes") {_,_ ->
                        viewModel.logout()
                        requireActivity().finish()
                    }
                    setNegativeButton("No") {dialog,_ -> dialog.cancel()}
                    create()
                    show()
                }
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
                    R.id.filter1 -> {
                        getReport(TOKEN, ID, "1")
                        true
                    }
                    R.id.filter2 -> {
                        getReport(TOKEN, ID, "2")
                        true
                    }
                    R.id.filter3 -> {
                        getReport(TOKEN, ID, "3")
                        true
                    }
                    R.id.filter4 -> {
                        getReport(TOKEN, ID, "4")
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
            getReport(TOKEN, null, null)
        } else{
            getReport(TOKEN, ID, null)
        }
    }

    private fun getReport(token: String, id_student: String?, idStatus: String?) {
        val adapter = ReportAdapter()
        binding.rvStory.layoutManager = LinearLayoutManager(requireActivity())
      //  binding.rvStory.setHasFixedSize(true)
        viewModel.getStories(token, id_student, idStatus).observe(viewLifecycleOwner){
            adapter.submitData(lifecycle, it)
        }
        binding.rvStory.adapter = adapter
    }





}