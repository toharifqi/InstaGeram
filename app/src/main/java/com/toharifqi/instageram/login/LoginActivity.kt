package com.toharifqi.instageram.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.toharifqi.instageram.AppComponent
import com.toharifqi.instageram.BaseApplication
import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.customview.InstaGeramEditText
import com.toharifqi.instageram.databinding.ActivityLoginBinding
import com.toharifqi.instageram.register.RegisterActivity
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

        observeViewModel()
        setupOnClickListener()
        setupFullScreen()
        setEditTextListener()
    }

    private fun observeViewModel() {
        with(viewModel) {
            loginResult.observe(this@LoginActivity) { loginResult ->
                when (loginResult) {
                    is ResultLoad.Success -> {
                        val loginResponse = loginResult.data?.loginResult
                        loginResponse?.let { viewModel.saveUser(it.name, it.token) }

                        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this@LoginActivity, "Berhasil Login!", Toast.LENGTH_SHORT)
                            .show()
                    }
                    is ResultLoad.Error   -> {
                        Toast.makeText(this@LoginActivity, loginResult.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupOnClickListener() {
        with(binding) {
            loginButton.setOnClickListener {
                val email = emailEditTxt.text.toString()
                val pass = passwordEditTxt.text.toString()
                viewModel.loginUser(email, pass)
            }
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