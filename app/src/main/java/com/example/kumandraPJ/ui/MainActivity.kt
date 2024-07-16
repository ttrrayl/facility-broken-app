package com.example.kumandraPJ.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.kumandraPJ.R
import com.example.kumandraPJ.adapter.ReportAdapter
import com.example.kumandraPJ.data.local.UserSession
import com.example.kumandraPJ.databinding.ActivityMainBinding
import com.example.kumandraPJ.viewmodel.MainViewModel
import com.example.kumandraPJ.viewmodel.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Setting")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var token: String
    private var idPj: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.fbAddStory.setOnClickListener{
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }

//        val sectionsPagerAdapter = SectionPageAdapter(this)
//       // sectionsPagerAdapter.username = username.toString()
//        val viewPager: ViewPager2 = binding.viewPager
//        viewPager.adapter = sectionsPagerAdapter
//        val tabs: TabLayout = binding.tabReport
//        TabLayoutMediator(tabs, viewPager){
//                tab,position -> tab.text = resources.getString(TAB_TITLES[position])
//        }.attach()

        viewModelConfig()
    }

    private fun viewModelConfig(){
        val pref = UserSession.getInstance(dataStore)
        mainViewModel =
            ViewModelProvider(this, ViewModelFactory(this, pref))[MainViewModel::class.java]

        mainViewModel.isLoading.observe(this){
            showLoading(it)
        }

        var token = ""
        mainViewModel.getToken().observe(this){ user ->
            if (user.isLogin) {
                token = user.token
                AddStoryActivity.TOKEN = user.token
                Log.i("LOGIN", "ID: ${user.isLogin}")
            } else {
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
        }

        mainViewModel.getUser().observe(this) {
            getStory(token, it.idPj)
        //    AddStoryActivity.IDSTUDENT = it.idPj
        }

//        mainViewModel.listStory.observe(this){
//            if (it.isEmpty()){
//                AlertDialog.Builder(this@MainActivity).apply {
//                    setTitle("Sorry :(")
//                    setMessage("There is nothing here")
//                    setPositiveButton("OK"){_,_ ->
//                        finish()
//                    }
//                    create()
//                    show()
//                }
//            }
//            listStory(it)
//        }
    }

    private fun getStory(token: String, idPj: String) {
        val adapter = ReportAdapter()
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        binding.rvStory.setHasFixedSize(true)
        mainViewModel.getStories(token, idPj).observe(this){

            adapter.submitData(lifecycle, it)
        }
        binding.rvStory.adapter = adapter

    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbMain.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_logout -> {
                AlertDialog.Builder(this).apply {
                    setTitle("CONFIRMATION")
                    setMessage("Logout of your account?")
                    setPositiveButton("Yes") {_,_ ->
                        mainViewModel.logout()
                        finish()
                    }
                    setNegativeButton("No") {dialog,_ -> dialog.cancel()}
                    create()
                    show()
                }
            }

        }
        return true
    }

    companion object{
        const val EXTRA_DETAIL = "extra_detail"
        const val AVATAR_DETAIL = "avatar_detail"
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tabText1,
            R.string.tabText2
        )
    }
}