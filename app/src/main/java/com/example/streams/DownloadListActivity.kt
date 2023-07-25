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
import com.tpstream.player.data.Video
import com.tpstream.player.data.source.local.DownloadStatus

const val TP_OFFLINE_PARAMS = "tp_offline_params"

class DownloadListActivity : AppCompatActivity() {

    lateinit var downloadListViewModel: DownloadListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDownloadListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViewModel()
        downloadListViewModel.getDownloadData().observe(this) {
            binding.recycleView.adapter = DownloadListAdapter(it!!)
        }
    }

    private fun initializeViewModel(){
        downloadListViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DownloadListViewModel(this@DownloadListActivity) as T
            }
        })[DownloadListViewModel::class.java]
    }

    inner class DownloadListAdapter(
        private val data:List<Video>
    ) : ListAdapter<Video,DownloadListAdapter.DownloadListViewHolder>(DOWNLOAD_COMPARATOR) {

        inner class DownloadListViewHolder(private val binding: DownloadItemBinding) : RecyclerView.ViewHolder(binding.root) {

            val deleteButton : Button = binding.deleteButton
            val cancelButton : Button = binding.cancelButton
            val pauseButton : Button = binding.pauseButton
            val resumeButton : Button = binding.resumeButton
            val thumbnail : ImageView = binding.thumbnail

            fun bind(offlineVideoInfo: Video) {
                binding.title.text = offlineVideoInfo.title
                thumbnail.setImageBitmap(offlineVideoInfo.getLocalThumbnail(applicationContext))
                binding.downloadImage.setImageResource(getDownloadImage(offlineVideoInfo.downloadState))
                binding.duration.text = offlineVideoInfo.duration
                binding.percentage.text = "${offlineVideoInfo.percentageDownloaded} %"
                updateButtonVisibility(offlineVideoInfo.downloadState)
            }

            private fun getDownloadImage(offlineVideoState: DownloadStatus?): Int {
                return when (offlineVideoState) {
                    DownloadStatus.DOWNLOADING -> com.tpstream.player.R.drawable.ic_baseline_downloading_24
                    DownloadStatus.PAUSE -> com.tpstream.player.R.drawable.ic_baseline_pause_circle_filled_24
                    else -> com.tpstream.player.R.drawable.ic_baseline_file_download_done_24
                }
            }

            private fun updateButtonVisibility(offlineVideoState: DownloadStatus?) {
                when (offlineVideoState) {
                    DownloadStatus.DOWNLOADING -> {
                        deleteButton.visibility = View.GONE
                        cancelButton.visibility = View.VISIBLE
                        pauseButton.visibility = View.VISIBLE
                        resumeButton.visibility = View.GONE
                    }
                    DownloadStatus.PAUSE -> {
                        deleteButton.visibility = View.GONE
                        cancelButton.visibility = View.VISIBLE
                        pauseButton.visibility = View.GONE
                        resumeButton.visibility = View.VISIBLE
                    }
                    DownloadStatus.COMPLETE -> {
                        deleteButton.visibility = View.VISIBLE
                        cancelButton.visibility = View.GONE
                        pauseButton.visibility = View.GONE
                        resumeButton.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadListViewHolder {
            return DownloadListViewHolder(
                DownloadItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: DownloadListViewHolder, position: Int) {
            val offlineVideoInfo = data[position]
            holder.bind(offlineVideoInfo)
            holder.deleteButton.setOnClickListener { downloadListViewModel.deleteDownload(offlineVideoInfo) }
            holder.cancelButton.setOnClickListener { downloadListViewModel.cancelDownload(offlineVideoInfo) }
            holder.pauseButton.setOnClickListener { downloadListViewModel.pauseDownload(offlineVideoInfo) }
            holder.resumeButton.setOnClickListener { downloadListViewModel.resumeDownload(offlineVideoInfo) }
            holder.thumbnail.setOnClickListener {
                if (offlineVideoInfo.downloadState == DownloadStatus.COMPLETE){
                    playVideo(offlineVideoInfo)
                } else {
                    Toast.makeText(applicationContext,"Downloading",Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun playVideo(offlineVideoInfo: Video){
            val intent = Intent(this@DownloadListActivity,PlayerActivity::class.java)
            intent.putExtra(TP_OFFLINE_PARAMS,TpInitParams.createOfflineParams(offlineVideoInfo.videoId))
            startActivity(intent)
        }

        override fun getItemCount() = data.size
    }

    companion object {

        private val DOWNLOAD_COMPARATOR = object : DiffUtil.ItemCallback<Video>() {
            override fun areItemsTheSame(
                oldItem: Video,
                newItem: Video
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: Video,
                newItem: Video
            ): Boolean = oldItem.videoId == newItem.videoId
        }
    }
}