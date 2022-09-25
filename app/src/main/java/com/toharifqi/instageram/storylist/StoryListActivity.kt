package com.toharifqi.instageram.storylist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.provider.Settings
import androidx.activity.viewModels
import com.toharifqi.instageram.BaseApplication
import com.toharifqi.instageram.R
import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.core.ResultLoad.Error
import com.toharifqi.instageram.core.ResultLoad.Success
import com.toharifqi.instageram.createstory.CreateStoryActivity
import com.toharifqi.instageram.databinding.ActivityStoryListBinding
import com.toharifqi.instageram.login.LoginActivity
import javax.inject.Inject

class StoryListActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory<StoryListViewModel>
    private val viewModel: StoryListViewModel by viewModels { viewModelFactory }

    private lateinit var binding: ActivityStoryListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityStoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getToken()
        observeViewModel()
        setOnclickListener()
    }

    private fun observeViewModel() {
        with(viewModel) {
            token.observe(this@StoryListActivity) {
                viewModel.getAllStories(it)
            }
            stories.observe(this@StoryListActivity) {
                when(it) {
                    is Success -> {

                    }
                    is Error -> {

                    }
                }
            }
        }
    }

    private fun setOnclickListener() {
        with(binding) {
            fab.setOnClickListener {
                Intent(this@StoryListActivity, CreateStoryActivity::class.java).run {
                    startActivity(this)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.close_app -> {
                finishAffinity()
                return true
            }
            R.id.action_change_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            R.id.action_logout -> {
                viewModel.logOut()
                Intent(this, LoginActivity::class.java).run {
                    startActivity(this)
                }
                return true
            }
            else -> return true
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}