package com.abdl.mydicodingstories.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abdl.mydicodingstories.data.remote.response.ListStoryItem
import com.abdl.mydicodingstories.databinding.ActivityDetailStoryBinding
import com.abdl.mydicodingstories.utils.DateFormatter
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyItem = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)

        binding.apply {
            tvTitle.text = storyItem?.name
            tvDescription.text = storyItem?.description
            createdAt.text =
                "Dibuat pada : ${storyItem?.createdAt?.let { DateFormatter.formatDate(it) }}"

            Glide.with(this@DetailStoryActivity)
                .load(storyItem?.photoUrl)
                .into(ivPicture)
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}