package com.toharifqi.instageram.storydetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.toharifqi.instageram.R
import com.toharifqi.instageram.common.setFormattedDate
import com.toharifqi.instageram.common.setImageFromUrl
import com.toharifqi.instageram.databinding.ActivityStoryDetailBinding
import com.toharifqi.instageram.storylist.StoryDomainData

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<StoryDomainData>(STORY_EXTRA) as StoryDomainData

        setUpViews(story)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.title_story_detail, story.name)
        }
    }

    private fun setUpViews(story: StoryDomainData) {
        with(binding) {
            nameTxt.text = story.name
            photoImage.setImageFromUrl(this@StoryDetailActivity, story.photoUrl)
            descriptionTxt.text = story.description
            dateTxt.setFormattedDate(this@StoryDetailActivity, story.createdAt)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object{
        const val STORY_EXTRA = "story_extra"
    }
}
