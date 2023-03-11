package com.example.submissionstoryapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.submissionstoryapp.database.StoryEntity
import com.example.submissionstoryapp.databinding.ActivityDetailBinding
import com.example.submissionstoryapp.model.ListStory
import com.example.submissionstoryapp.utils.dateFormat
import dagger.hilt.android.AndroidEntryPoint
import java.util.TimeZone

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDetail()
    }

    private fun getDetail() {
        val data = intent.getParcelableExtra<StoryEntity>(EXTRA_DETAIL)
        binding.tvNameDetail.text = data?.name
        binding.tvDescDetail.text = data?.description
        binding.tvDescCreatedAt.text =
            data?.let { dateFormat(it.createdAt, TimeZone.getDefault().id) }
        Glide.with(this).load(data?.photoUrl).into(binding.imgDetail)
    }

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }
}