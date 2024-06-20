package com.example.streams

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.streams.databinding.ActivityDownloadListBinding
import com.example.streams.databinding.DownloadItemBinding
import com.tpstream.player.TpInitParams
import com.tpstream.player.data.Asset
import com.tpstream.player.data.source.local.DownloadStatus

const val TP_OFFLINE_PARAMS = "tp_offline_params"

class DownloadListActivity : AppCompatActivity() {

    lateinit var downloadListViewModel: DownloadListViewModel
    private lateinit var downloadListAdapter: DownloadListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDownloadListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViewModel()

        downloadListAdapter = DownloadListAdapter()
        binding.recycleView.adapter = downloadListAdapter

        downloadListViewModel.getDownloadData().observe(this) { assets ->
            downloadListAdapter.submitList(assets)
        }
    }

    private fun initializeViewModel() {
        downloadListViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DownloadListViewModel(this@DownloadListActivity) as T
            }
        })[DownloadListViewModel::class.java]
    }

    inner class DownloadListAdapter :
        ListAdapter<Asset, DownloadListAdapter.DownloadListViewHolder>(DOWNLOAD_COMPARATOR) {

        inner class DownloadListViewHolder(private val binding: DownloadItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(asset: Asset) {
                binding.title.text = asset.title
                binding.thumbnail.setImageBitmap(asset.getLocalThumbnail(applicationContext))
                binding.downloadImage.setImageResource(getDownloadImage(asset.video.downloadState))
                binding.duration.text = asset.video.duration.toString()
                binding.percentage.text = "${asset.video.percentageDownloaded} %"
                showOrHideButtons(asset.video.downloadState)

                binding.deleteButton.setOnClickListener { downloadListViewModel.deleteDownload(asset) }
                binding.cancelButton.setOnClickListener { downloadListViewModel.cancelDownload(asset) }
                binding.pauseButton.setOnClickListener { downloadListViewModel.pauseDownload(asset) }
                binding.resumeButton.setOnClickListener { downloadListViewModel.resumeDownload(asset) }
                binding.thumbnail.setOnClickListener {
                    if (asset.video.downloadState == DownloadStatus.COMPLETE) {
                        playVideo(asset)
                    } else {
                        Toast.makeText(applicationContext, "Downloading", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            private fun getDownloadImage(videoState: DownloadStatus?): Int {
                return when (videoState) {
                    DownloadStatus.DOWNLOADING -> com.tpstream.player.R.drawable.ic_baseline_downloading_24
                    DownloadStatus.PAUSE -> com.tpstream.player.R.drawable.ic_baseline_pause_circle_filled_24
                    else -> com.tpstream.player.R.drawable.ic_baseline_file_download_done_24
                }
            }

            private fun showOrHideButtons(videoState: DownloadStatus?) {
                when (videoState) {
                    DownloadStatus.DOWNLOADING -> {
                        binding.deleteButton.visibility = View.GONE
                        binding.cancelButton.visibility = View.VISIBLE
                        binding.pauseButton.visibility = View.VISIBLE
                        binding.resumeButton.visibility = View.GONE
                    }
                    DownloadStatus.PAUSE -> {
                        binding.deleteButton.visibility = View.GONE
                        binding.cancelButton.visibility = View.VISIBLE
                        binding.pauseButton.visibility = View.GONE
                        binding.resumeButton.visibility = View.VISIBLE
                    }
                    DownloadStatus.COMPLETE -> {
                        binding.deleteButton.visibility = View.VISIBLE
                        binding.cancelButton.visibility = View.GONE
                        binding.pauseButton.visibility = View.GONE
                        binding.resumeButton.visibility = View.GONE
                    }
                    else -> {}
                }
            }

            private fun playVideo(asset: Asset) {
                val intent = Intent(this@DownloadListActivity, PlayerActivity::class.java)
                intent.putExtra(
                    TP_OFFLINE_PARAMS,
                    TpInitParams.createOfflineParams(asset.id)
                )
                startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadListViewHolder {
            val binding =
                DownloadItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DownloadListViewHolder(binding)
        }

        override fun onBindViewHolder(holder: DownloadListViewHolder, position: Int) {
            val asset = getItem(position)
            holder.bind(asset)
        }
    }

    companion object {
        private val DOWNLOAD_COMPARATOR = object : DiffUtil.ItemCallback<Asset>() {
            override fun areItemsTheSame(oldItem: Asset, newItem: Asset): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Asset, newItem: Asset): Boolean {
                return oldItem == newItem
            }
        }
    }
}