package com.example.kumandra.ui.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.kumandra.PushNotificationService
import com.example.kumandra.R
import com.example.kumandra.adapter.SectionPageAdapter
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.data.remote.ApiConfig
import com.example.kumandra.databinding.FragmentParentReportBinding
import com.example.kumandra.databinding.FragmentReportBinding
import com.example.kumandra.ui.AddStoryActivity
import com.example.kumandra.ui.LoginActivity
import com.example.kumandra.ui.MainActivity
import com.example.kumandra.viewmodel.MainViewModel
import com.example.kumandra.viewmodel.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ParentReportFragment : Fragment() {
    private lateinit var binding: FragmentParentReportBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Laporan Kerusakan"
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        binding = FragmentParentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fbAddStory.setOnClickListener{
            startActivity(Intent(context, AddStoryActivity::class.java))
        }

        val sectionsPagerAdapter = SectionPageAdapter(requireActivity())
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabReport
        TabLayoutMediator(tabs, viewPager){
                tab,position -> tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

//    override fun onResume() {
//        super.onResume()
//        (activity as? AppCompatActivity)?.supportActionBar?.show()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        (activity as? AppCompatActivity)?.supportActionBar?.hide()
//    }

//    private fun showLoading(isLoading: Boolean) {
//        binding.pbMain.visibility =
//            if (isLoading) View.VISIBLE else View.GONE
//    }

    companion object{
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tabText1,
            R.string.tabText2
        )
    }
}