package com.example.submissionstoryapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.submissionstoryapp.databinding.ItemLoadingBinding

class LoadingStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadingStateAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): MyViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, retry)
    }

    class MyViewHolder(private val binding: ItemLoadingBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener {
                retry.invoke()
            }
        }


        fun bind(state: LoadState) {
            if (state is LoadState.Error) {
                binding.errorMsg.text = state.error.localizedMessage
            }
            binding.progressBar.isVisible = state is LoadState.Loading
            binding.retryButton.isVisible = state is LoadState.Error
            binding.errorMsg.isVisible = state is LoadState.Error
        }

    }
}