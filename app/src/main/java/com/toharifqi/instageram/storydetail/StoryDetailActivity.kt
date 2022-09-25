package com.toharifqi.instageram.storydetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.toharifqi.instageram.R

class StoryDetailActivity : AppCompatActivity() {

    companion object{
        const val STORY_EXTRA = "story_extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_detail)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.title_create_story)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
