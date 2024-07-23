package com.example.kumandraPJ.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kumandraPJ.PushNotificationService
import com.example.kumandraPJ.R
import com.example.kumandraPJ.adapter.ReportAdapter
import com.example.kumandraPJ.data.local.PjModel
import com.example.kumandraPJ.data.local.UserSession
import com.example.kumandraPJ.data.remote.ApiConfig
import com.example.kumandraPJ.databinding.ActivityMainBinding
import com.example.kumandraPJ.viewmodel.MainViewModel
import com.example.kumandraPJ.viewmodel.ViewModelFactory
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Setting")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var pj: PjModel
  //  private var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelConfig()
//        FirebaseApp.initializeApp(this)
//        val token = Firebase.messaging.token
//        Log.i("FCM TOKEN", "TOKEN : $token")

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
                sendRegistrationToServer(user.idPj, token)
            }
        }


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


        requestNotificationPermission()
        getStory(MainActivity.TOKEN, MainActivity.ID, null)
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
                MainActivity.TOKEN = user.token
            } else {
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
        }


        mainViewModel.getUser().observe(this) { user ->
//            getStory(token, user.idPj, MainActivity.status)
            MainActivity.ID = user.idPj
            PushNotificationService.ID = user.idPj
        }

        //this.id = idPj



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



    private fun sendRegistrationToServer(idPj: String,token: String) {
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val response = ApiConfig.getApiService().addFcmToken(idPj, "2", token)
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

    private fun getStory(token: String, idPj: String, idStatus: String?) {
        Log.i("STATUS", "STATUS: $idStatus")
        val adapter = ReportAdapter()
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        binding.rvStory.setHasFixedSize(true)
        mainViewModel.getStories(token, idPj, idStatus).observe(this){

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
            R.id.menu_filter -> {
                showFilter()
            }
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

    private fun showFilter() {
        val view = findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(this, view).run {
            menuInflater.inflate(R.menu.filter_sort, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.filter1 ->
                        getStory(MainActivity.TOKEN, MainActivity.ID, "1")
                    R.id.filter2 ->
                        getStory(MainActivity.TOKEN, MainActivity.ID, "2")
                    R.id.filter3 ->
                        getStory(MainActivity.TOKEN, MainActivity.ID, "3")
                    R.id.filter4 ->
                        getStory(MainActivity.TOKEN, MainActivity.ID, "4")

                }
                Log.i("TOKEN", "STATUS: $TOKEN")
                Log.i("ID", "STATUS: $ID")
                true
            }
            show()
        }
    }

    companion object{
        const val EXTRA_DETAIL = "extra_detail"
        const val AVATAR_DETAIL = "avatar_detail"
        var status: String? = null
        var ID: String = ""
        var TOKEN: String = ""
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tabText1,
            R.string.tabText2
        )
    }
}