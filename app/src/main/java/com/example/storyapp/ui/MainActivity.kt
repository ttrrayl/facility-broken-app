package com.example.storyapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.StoriesAdapter
import com.example.storyapp.data.local.UserSession
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.viewmodel.MainViewModel
import com.example.storyapp.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Setting")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        binding.fbAddStory.setOnClickListener{
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }

        viewModelConfig()
    }

    private fun viewModelConfig(){
        val pref = UserSession.getInstance(dataStore)
        mainViewModel =
            ViewModelProvider(this, ViewModelFactory(this, pref))[MainViewModel::class.java]

        mainViewModel.isLoading.observe(this){
            showLoading(it)
        }

        mainViewModel.getToken().observe(this){ user ->
            if (user.isLogin) {
                AddStoryActivity.TOKEN = user.token
                getStory(user.token)
            } else {
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
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

    private fun showLoading(isLoading: Boolean) {
        binding.pbMain.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun getStory(token: String) {
        val adapter = StoriesAdapter()
        binding.rvStory.setHasFixedSize(true)
        mainViewModel.getStories(token).observe(this){

            adapter.submitData(lifecycle, it)
        }
        binding.rvStory.adapter = adapter
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
            R.id.menu_map -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
            }
        }
        return true
    }
}