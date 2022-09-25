package com.toharifqi.instageram.storylist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.toharifqi.instageram.R

class StoryListActivity : AppCompatActivity() {

    companion object {
        const val TOKEN_EXTRA = "token_extra"
    }

    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_list)

        token = intent.getStringExtra(TOKEN_EXTRA) as String
    }
}