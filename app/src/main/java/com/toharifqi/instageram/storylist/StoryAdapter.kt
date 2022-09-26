package com.toharifqi.instageram.storylist

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.core.util.Pair
import com.toharifqi.instageram.common.setFormattedDate
import com.toharifqi.instageram.common.setImageFromUrl
import com.toharifqi.instageram.databinding.ItemStoryBinding
import com.toharifqi.instageram.storydetail.StoryDetailActivity
import com.toharifqi.instageram.storydetail.StoryDetailActivity.Companion.STORY_EXTRA
import com.toharifqi.instageram.storylist.StoryAdapter.StoryViewHolder

class StoryDomainComparator : DiffUtil.ItemCallback<StoryDomainData>() {
    override fun areItemsTheSame(oldItem: StoryDomainData, newItem: StoryDomainData): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: StoryDomainData, newItem: StoryDomainData): Boolean =
        oldItem == newItem
}

class StoryAdapter : ListAdapter<StoryDomainData, StoryViewHolder>(StoryDomainComparator()) {

    inner class StoryViewHolder(
        private val binding: ItemStoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoryDomainData) {
            with(binding) {
                val context = binding.root.context
                nameTxt.text = item.name
                photoImage.setImageFromUrl(context, item.photoUrl)
                descriptionTxt.text = item.description
                dateTxt.setFormattedDate(context, item.createdAt)

                root.setOnClickListener {

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            context as Activity,
                            Pair(photoImage, "photo_image"),
                            Pair(personImage, "person_image"),
                            Pair(nameTxt, "name_txt"),
                            Pair(descriptionTxt, "description_txt"),
                            Pair(dateTxt, "date_txt")
                        )

                    Intent(context, StoryDetailActivity::class.java).run {
                        putExtra(STORY_EXTRA, item)
                        context.startActivity(this, optionsCompat.toBundle())
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder =
        StoryViewHolder(
            ItemStoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) =
        holder.bind(getItem(position))
}