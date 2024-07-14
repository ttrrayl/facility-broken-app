package com.example.kumandra.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kumandra.ui.fragment.ReportFragment

class SectionPageAdapter(activity: AppCompatActivity): FragmentStateAdapter(activity) {

    var username: String = ""

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = ReportFragment()
        fragment.arguments = Bundle().apply {
            putInt(ReportFragment.ARG_POSITION, position+1)
         //   putString(ReportFragment.ARG_USERNAME, username)
        }
        return fragment
    }
}