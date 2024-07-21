package com.example.kumandra.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.kumandra.PushNotificationService
import com.example.kumandra.R
import com.example.kumandra.adapter.SectionPageAdapter
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.data.remote.ApiConfig
import com.example.kumandra.databinding.ActivityMainBinding
import com.example.kumandra.viewmodel.MainViewModel
import com.example.kumandra.viewmodel.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Setting")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var token: String
    private var idStudent: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.fbAddStory.setOnClickListener{
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }

        val sectionsPagerAdapter = SectionPageAdapter(this)
       // sectionsPagerAdapter.username = username.toString()
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabReport
        TabLayoutMediator(tabs, viewPager){
                tab,position -> tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        // Mendapatkan token FCM
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCMToken", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Dapatkan token baru
            val token = task.result

            // Log token
            Log.d("FCMToken", "Refreshed token: $token")

            mainViewModel.getUser().observe(this) { user ->
                sendRegistrationToServer(user.idStudent.toString(), token)
            }

        }
        viewModelConfig()
        requestNotificationPermission()
    }
    private fun requestNotificationPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if(!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }

    private fun sendRegistrationToServer(idStudent: String, token: String) {
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val response = ApiConfig.getApiService().addFcmToken(idStudent, "1", token)
                if (response.isSuccessful){
                    response.body()?.let {
                        Log.i("FcmMainOK", "fcm: $response")
                    }
                } else{
                    Log.i("FcmMain", "fcm: $response")
                }

            } catch (e: Throwable){
                Log.i("FcmViewModel", e.message.toString())
            }
        }
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
                this.token = user.token
                AddStoryActivity.TOKEN = user.token
            } else {
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
        }

        mainViewModel.getUser().observe(this) {
            this.idStudent = it.idStudent
            AddStoryActivity.IDSTUDENT = it.idStudent
            PushNotificationService.ID = it.idStudent.toString()
        }
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