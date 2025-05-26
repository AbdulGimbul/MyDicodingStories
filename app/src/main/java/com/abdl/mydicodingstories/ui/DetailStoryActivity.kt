package com.abdl.mydicodingstories.ui

import android.os.Build
import android.os.Bundle
import com.abdl.mydicodingstories.R
import com.abdl.mydicodingstories.data.remote.response.ListStoryItem
import com.abdl.mydicodingstories.databinding.ActivityDetailStoryBinding
import com.abdl.mydicodingstories.utils.DateFormatter
import com.bumptech.glide.Glide

class DetailStoryActivity : BaseActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.story_detail_title)

        val storyItem: ListStoryItem? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_STORY, ListStoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_STORY)
        }
        binding.apply {
            tvTitle.text = storyItem?.name
            tvDescription.text = storyItem?.description
            createdAt.text =
                "${getString(R.string.created_at_label)} ${
                    storyItem?.createdAt?.let {
                        DateFormatter.formatDate(
                            it
                        )
                    }
                }"

            Glide.with(this@DetailStoryActivity)
                .load(storyItem?.photoUrl)
                .into(ivPicture)
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}