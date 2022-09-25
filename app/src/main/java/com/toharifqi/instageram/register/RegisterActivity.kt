package com.toharifqi.instageram.register

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
import com.toharifqi.instageram.BaseApplication
import com.toharifqi.instageram.R
import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.core.ResultLoad.Error
import com.toharifqi.instageram.core.ResultLoad.Success
import com.toharifqi.instageram.customview.InstaGeramEditText
import com.toharifqi.instageram.databinding.ActivityRegisterBinding
import javax.inject.Inject

class RegisterActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory<RegisterViewModel>
    private val viewModel: RegisterViewModel by viewModels { viewModelFactory }

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.isEnabled = false

        observeViewModel()
        setupOnClickListener()
        setupFullScreen()
        setEditTextListener()
    }

    private fun setEditTextListener() {
        with(binding) {
            nameEditTxt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    setLoginButtonEnable(nameEditTxt)
                }

                override fun afterTextChanged(p0: Editable?) {}
            })

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

    private fun setLoginButtonEnable(editText: InstaGeramEditText) {
        with(binding) {
            val result = editText.text
            registerButton.isEnabled =
                (result != null && result.toString().isNotEmpty() && editText.error == null)
        }
    }

    private fun setupOnClickListener() {
        with(binding) {
            registerButton.setOnClickListener {
                progressCircular.visibility = View.VISIBLE
                val name = nameEditTxt.text.toString()
                val email = emailEditTxt.text.toString()
                val pass = passwordEditTxt.text.toString()
                viewModel.registerUser(name, email, pass)
            }
            loginText.setOnClickListener { onBackPressed() }
        }
    }

    private fun observeViewModel() {
        with(viewModel) {
            registerResult.observe(this@RegisterActivity) { registerResult ->
                when (registerResult) {
                    is Success -> {
                        binding.progressCircular.visibility = View.GONE
                        onBackPressed()
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.text_toast_user_created),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Error   -> {
                        binding.progressCircular.visibility = View.GONE
                        Toast.makeText(
                            this@RegisterActivity,
                            registerResult.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
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
}
