package com.toharifqi.instageram.storylist

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.toharifqi.instageram.BaseApplication
import com.toharifqi.instageram.R
import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.createstory.CreateStoryActivity
import com.toharifqi.instageram.databinding.ActivityStoryListBinding
import com.toharifqi.instageram.login.LoginActivity
import com.toharifqi.instageram.storymap.StoryMapActivity
import javax.inject.Inject

class StoryListActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory<StoryListViewModel>
    private val viewModel: StoryListViewModel by viewModels { viewModelFactory }

    private lateinit var binding: ActivityStoryListBinding
    private lateinit var storyAdapter: StoryAdapter
    private var userToken: String? = null

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }
    private var isMainFabClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityStoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getToken()
        setupRecyclerView()
        observeViewModel()
        setUpClickListener()
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter()

        with(binding) {
            swipeLayout.setOnRefreshListener {
                viewModel.loadAllStories(userToken)
            }

            storyAdapter.addLoadStateListener { loadState ->
                val isAdapterFinishedLoading = loadState.source.refresh is LoadState.NotLoading
                val isReachedEndOfPagination = loadState.append.endOfPaginationReached
                val isStoryEmpty = storyAdapter.itemCount < 1
                val isZeroStory = isAdapterFinishedLoading && isReachedEndOfPagination && isStoryEmpty
                val isError = loadState.source.refresh is LoadState.Error

                if (isZeroStory || isError) {
                    binding.swipeLayout.isRefreshing = false
                    progressCircular.visibility = View.GONE
                    setEmptyViews(true)
                } else {
                    binding.swipeLayout.isRefreshing = false
                    setEmptyViews(false)
                    progressCircular.visibility = View.GONE
                }
            }

            recylerView.adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )

            storyAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    recylerView.scrollToPosition(0)
                }
            })
        }
    }

    private fun setEmptyViews(isVisible: Boolean) {
        with(binding) {
            if (isVisible) {
                recylerView.visibility = View.GONE
                emptyGroup.visibility = View.VISIBLE
            } else {
                recylerView.visibility = View.VISIBLE
                emptyGroup.visibility = View.GONE
            }
        }
    }

    private fun observeViewModel() {
        with(viewModel) {
            token.observe(this@StoryListActivity) { token ->
                if (token == null) {
                    Intent(this@StoryListActivity, LoginActivity::class.java).run {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(this)
                    }
                    finish()
                } else {
                    binding.progressCircular.visibility = View.VISIBLE
                    userToken = token
                    viewModel.loadAllStories(token)
                }
            }
            stories.observe(this@StoryListActivity) {
                val recyclerViewState = binding.recylerView.layoutManager?.onSaveInstanceState()
                storyAdapter.submitData(lifecycle, it)
                binding.recylerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
            }
        }
    }

    private fun setUpClickListener() {
        with(binding) {
            fab.setOnClickListener {
                onMainFabClicked()
            }
            fabAddStory.setOnClickListener {
                Intent(this@StoryListActivity, CreateStoryActivity::class.java).run {
                    putExtra(CreateStoryActivity.TOKEN_EXTRA, userToken)
                    startActivity(this)
                }
            }
            fabStoryMap.setOnClickListener {
                Intent(this@StoryListActivity, StoryMapActivity::class.java).run {
                    putExtra(StoryMapActivity.TOKEN_EXTRA, userToken)
                    startActivity(this)
                }
            }
        }
    }

    private fun onMainFabClicked() {
        setChildFabVisibility(isMainFabClicked)
        setChildFabAnimation(isMainFabClicked)
        isMainFabClicked = !isMainFabClicked
    }

    private fun setChildFabAnimation(isMainFabClicked: Boolean) {
        with(binding) {
            if (!isMainFabClicked) {
                fabAddStory.startAnimation(fromBottom)
                fabStoryMap.startAnimation(fromBottom)
                fab.startAnimation(rotateOpen)
            } else {
                fabAddStory.startAnimation(toBottom)
                fabStoryMap.startAnimation(toBottom)
                fab.startAnimation(rotateClose)
            }
        }
    }

    private fun setChildFabVisibility(isMainFabClicked: Boolean) {
        with(binding) {
            if (!isMainFabClicked) {
                fabAddStory.visibility = View.VISIBLE
                fabStoryMap.visibility = View.VISIBLE
            } else {
                fabAddStory.visibility = View.INVISIBLE
                fabStoryMap.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.close_app             -> {
                finishAffinity()
                return true
            }
            R.id.action_change_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            R.id.action_logout         -> {
                viewModel.logOut()
                return true
            }
            else                       -> return true
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}