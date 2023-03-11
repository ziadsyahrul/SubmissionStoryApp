package com.example.submissionstoryapp.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.databinding.ActivityMainBinding
import com.example.submissionstoryapp.model.ListStory
import com.example.submissionstoryapp.repository.StoryRepository
import com.example.submissionstoryapp.ui.adapter.LoadingStateAdapter
import com.example.submissionstoryapp.ui.uploadstory.UploadStoryActivity
import com.example.submissionstoryapp.ui.detail.DetailActivity
import com.example.submissionstoryapp.ui.adapter.StoriesAdapter
import com.example.submissionstoryapp.ui.auth.login.LoginActivity
import com.example.submissionstoryapp.ui.maps.MapsActivity
import com.example.submissionstoryapp.utils.NetworkResource
import com.example.submissionstoryapp.utils.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var prefs: PreferencesManager
    private lateinit var storyAdapter: StoriesAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferencesManager(this)
        storyAdapter = StoriesAdapter()

        getDataList()
        setPaging()

        toUploadStory()

    }

    private fun setPaging() {
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.setHasFixedSize(true)
        binding.rvMain.adapter = storyAdapter.withLoadStateHeaderAndFooter(
            header = LoadingStateAdapter { storyAdapter.retry() },
            footer = LoadingStateAdapter { storyAdapter.retry() }
        )

        binding.viewError.btnRetry.setOnClickListener {
            storyAdapter.retry()
        }

        storyAdapter.addLoadStateListener {
            binding.pbMain.isVisible = it.source.refresh is LoadState.Loading
            binding.rvMain.isVisible = it.source.refresh is LoadState.NotLoading
            binding.viewError.txtError.isVisible = it.source.refresh is LoadState.Error
            binding.viewError.btnRetry.isVisible = it.source.refresh is LoadState.Error

            if (it.source.refresh is LoadState.NotLoading && it.append.endOfPaginationReached && storyAdapter.itemCount < 1) {
                binding.viewNotFound.txtNotFound.isVisible = true
                binding.rvMain.isVisible = false
            } else {
                binding.viewNotFound.txtNotFound.isVisible = false
                binding.rvMain.isVisible = true
            }
        }

    }

    private fun getDataList() {
        viewModel.getStory(prefs.token).observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.logout -> {
                prefs.clear()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                this@MainActivity.finish()
                Toast.makeText(this, "Logout Success", Toast.LENGTH_SHORT).show()
            }
            R.id.maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun toUploadStory() {
        binding.btnAddStory.setOnClickListener {
            val intent = Intent(this, UploadStoryActivity::class.java)
            startActivity(intent)
        }
    }
}