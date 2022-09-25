package com.toharifqi.instageram.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.toharifqi.instageram.BaseApplication
import com.toharifqi.instageram.R
import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.core.ResultLoad.Error
import com.toharifqi.instageram.core.ResultLoad.Success
import com.toharifqi.instageram.customview.InstaGeramEditText
import com.toharifqi.instageram.databinding.ActivityLoginBinding
import com.toharifqi.instageram.register.RegisterActivity
import com.toharifqi.instageram.storylist.StoryListActivity
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory<LoginViewModel>
    private val viewModel: LoginViewModel by viewModels { viewModelFactory }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.isEnabled = false

        viewModel.checkLoginSession()
        observeViewModel()
        setupOnClickListener()
        setupFullScreen()
        setEditTextListener()
    }

    private fun observeViewModel() {
        with(viewModel) {
            isLoggedIn.observe(this@LoginActivity) { isLoggedIn ->
                if (isLoggedIn) {
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.text_toast_welcome),
                        Toast.LENGTH_SHORT
                    ).show()
                    goToStoryListActivity()
                }
            }
            loginResult.observe(this@LoginActivity) { loginResult ->
                when (loginResult) {
                    is Success -> {
                        binding.progressCircular.visibility = View.GONE
                        val loginResponse = loginResult.data?.loginResult
                        loginResponse?.let {
                            saveUser(it.name, it.token)
                            goToStoryListActivity()
                        }

                        Toast.makeText(
                            this@LoginActivity,
                            getString(R.string.text_toast_welcome),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Error   -> {
                        binding.progressCircular.visibility = View.GONE
                        var errorMessage = loginResult.message
                        if (errorMessage.toString().contains("401")) {
                            errorMessage = getString(R.string.text_toast_register_first)

                            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT)
                                .show()
                            goToRegisterActivity()
                        }
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun goToRegisterActivity() {
        val optionsCompat: ActivityOptionsCompat =
            with(binding) {
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@LoginActivity,

                    Pair(mainBg, "main_bg"),
                    Pair(logo, "logo"),
                    Pair(appTitle, "app_title"),
                    Pair(emailEditTxt, "email_edit_txt"),
                    Pair(passwordEditTxt, "password_edit_txt"),
                    Pair(loginButton, "login_button"),
                    Pair(registerText, "register_txt")
                )
            }

        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent, optionsCompat.toBundle())
    }

    private fun goToStoryListActivity() {
        Intent(this@LoginActivity, StoryListActivity::class.java).run {
            startActivity(this)
        }
    }

    private fun setupOnClickListener() {
        with(binding) {
            loginButton.setOnClickListener {
                progressCircular.visibility = View.VISIBLE
                val email = emailEditTxt.text.toString()
                val pass = passwordEditTxt.text.toString()
                viewModel.loginUser(email, pass)
            }
            registerText.setOnClickListener { goToRegisterActivity() }
        }
    }

    private fun setEditTextListener() {
        with(binding) {
            emailEditTxt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    setLoginButtonEnable(emailEditTxt)
                }

                override fun afterTextChanged(p0: Editable?) {}
            })

            passwordEditTxt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    setLoginButtonEnable(passwordEditTxt)
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
        }
    }

    private fun setupFullScreen() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setLoginButtonEnable(editText: InstaGeramEditText) {
        with(binding) {
            val result = editText.text
            loginButton.isEnabled =
                (result != null && result.toString().isNotEmpty() && editText.error == null)
        }
    }
}
