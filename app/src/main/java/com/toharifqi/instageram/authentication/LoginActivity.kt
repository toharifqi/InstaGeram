package com.toharifqi.instageram.authentication

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.toharifqi.instageram.customview.InstaGeramEditText
import com.toharifqi.instageram.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.isEnabled = false

        setupFullScreen()
        setEditTextListener()
    }

    private fun setEditTextListener() {
        with(binding) {
            userNameEditTxt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    setLoginButtonEnable(userNameEditTxt)
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