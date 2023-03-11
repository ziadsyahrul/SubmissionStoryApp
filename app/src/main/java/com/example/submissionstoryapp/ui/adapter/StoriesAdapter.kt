package com.example.submissionstoryapp.ui.adapter

import android.app.Activity
import android.app.ActivityOptions
import android.content.ClipData.Item
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.database.StoryEntity
import com.example.submissionstoryapp.databinding.ItemListStoriesBinding
import com.example.submissionstoryapp.model.ListStory
import com.example.submissionstoryapp.ui.detail.DetailActivity
import com.example.submissionstoryapp.utils.dateFormat
import java.util.TimeZone

class StoriesAdapter : PagingDataAdapter<StoryEntity, StoriesAdapter.MyViewHolder>(Diffcallback) {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(holder.itemView.context, story)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemListStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    class MyViewHolder(private val binding: ItemListStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ctx: Context, data: StoryEntity) {
            binding.apply {
                tvStoryName.text = data.name
                tvStoryDescription.text = data.description
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tvStoryDate.text = dateFormat(data.createdAt, TimeZone.getDefault().id)
                }
                val opt = RequestOptions().error(R.drawable.ic_baseline_broken_image_24)
                Glide.with(root.context).load(data.photoUrl).apply(opt).into(imgStory)

                root.setOnClickListener {
                    val activityOptionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(binding.imgStory, "photo"),
                            Pair(binding.tvStoryName, "name"),
                            Pair(binding.tvStoryDescription, "desc")
                        )

                    val intent = Intent(root.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_DETAIL, data)
                    root.context.startActivity(intent, activityOptionsCompat.toBundle())
                }
            }
        }

    }

    companion object {
        val Diffcallback = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

        }
    }
}