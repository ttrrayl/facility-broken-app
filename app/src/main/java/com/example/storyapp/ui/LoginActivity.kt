package com.example.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.local.UserModel
import com.example.storyapp.data.local.UserSession
import com.example.storyapp.viewmodel.LoginViewModel
import com.example.storyapp.viewmodel.ViewModelFactory
import com.example.storyapp.databinding.ActivityLoginBinding
import java.lang.ref.WeakReference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
lateinit var weakReference: WeakReference<ActivityLoginBinding>

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var user: UserModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        weakReference = WeakReference(binding)

        binding.buttonLogin.setOnClickListener{
            val email = binding.edEmail.text.toString()
            val password = binding.etPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.layoutEmail.error = "Isi email"
                }
                password.isEmpty() -> {
                    binding.layoutPassword.error = "Isi password"
                }
                else -> {
                    loginViewModel.authenticate(email, password)

                }
            }
        }

        binding.tvAkun.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        Glide.with(this)
            .load("https://th.bing.com/th/id/OIP.1MZ3IQ50dBysLAdeCnt5wwHaHa?pid=ImgDet&rs=1")
            .into(binding.ivLogo)

        viewModelConfig()
        playAnimation()
    }

    private fun viewModelConfig(){
        val pref = UserSession.getInstance(dataStore)

        loginViewModel = ViewModelProvider(this, ViewModelFactory(this, pref))[LoginViewModel::class.java]
        loginViewModel.getToken().observe(this) { user ->
            this.user = user
        }
        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        loginViewModel.msg.observe(this) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.app_name))
                setMessage(it)
                setPositiveButton("OK") {_,_ ->
                }
                create()
                show()
            }
        }
        loginViewModel.isLogin.observe(this){
            if (it) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.pbLogin.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        val logo = ObjectAnimator.ofFloat(binding.ivLogo, View.ALPHA, 1f).setDuration(200)
        val tv_halo = ObjectAnimator.ofFloat(binding.tvHalo, View.ALPHA, ALPHA).setDuration(DURATION)
        val tv_login = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, ALPHA).setDuration(DURATION)
        val layout_email = ObjectAnimator.ofFloat(binding.layoutEmail, View.ALPHA, ALPHA).setDuration(DURATION)
        val layout_password = ObjectAnimator.ofFloat(binding.layoutPassword, View.ALPHA, ALPHA).setDuration(DURATION)
        val tv_regis = ObjectAnimator.ofFloat(binding.tvAkun, View.ALPHA, ALPHA).setDuration(DURATION)
        val button_login = ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, ALPHA).setDuration(DURATION)
        AnimatorSet().apply {
            playSequentially(
                logo,
                tv_halo,
                tv_login,
                layout_email,
                layout_password,
                tv_regis,
                button_login
            )
            start()
        }
    }

    companion object {
        fun isPassError(isError: Boolean) {
            val binding = weakReference.get()
            binding?.layoutEmail?.isEndIconVisible = !isError
        }
        private const val DURATION = 200L
        private const val ALPHA = 1f
    }

}