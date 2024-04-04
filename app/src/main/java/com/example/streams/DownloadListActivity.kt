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
        private val data:List<Asset>
    ) : ListAdapter<Asset,DownloadListAdapter.DownloadListViewHolder>(DOWNLOAD_COMPARATOR) {

        inner class DownloadListViewHolder(private val binding: DownloadItemBinding) : RecyclerView.ViewHolder(binding.root) {

            val deleteButton : Button = binding.deleteButton
            val cancelButton : Button = binding.cancelButton
            val pauseButton : Button = binding.pauseButton
            val resumeButton : Button = binding.resumeButton
            val thumbnail : ImageView = binding.thumbnail

            fun bind(offlineAssetInfo: Asset) {
                binding.title.text = offlineAssetInfo.title
                thumbnail.setImageBitmap(offlineAssetInfo.getLocalThumbnail(applicationContext))
                binding.downloadImage.setImageResource(getDownloadImage(offlineAssetInfo.video.downloadState))
                binding.duration.text = offlineAssetInfo.video.duration
                binding.percentage.text = "${offlineAssetInfo.video.percentageDownloaded} %"
                updateButtonVisibility(offlineAssetInfo.video.downloadState)
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
            val offlineAssetInfo = data[position]
            holder.bind(offlineAssetInfo)
            holder.deleteButton.setOnClickListener { downloadListViewModel.deleteDownload(offlineAssetInfo) }
            holder.cancelButton.setOnClickListener { downloadListViewModel.cancelDownload(offlineAssetInfo) }
            holder.pauseButton.setOnClickListener { downloadListViewModel.pauseDownload(offlineAssetInfo) }
            holder.resumeButton.setOnClickListener { downloadListViewModel.resumeDownload(offlineAssetInfo) }
            holder.thumbnail.setOnClickListener {
                if (offlineAssetInfo.video.downloadState == DownloadStatus.COMPLETE){
                    playVideo(offlineAssetInfo)
                } else {
                    Toast.makeText(applicationContext,"Downloading",Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun playVideo(offlineAssetInfo: Asset){
            val intent = Intent(this@DownloadListActivity,PlayerActivity::class.java)
            intent.putExtra(TP_OFFLINE_PARAMS,TpInitParams.createOfflineParams(offlineAssetInfo.id))
            startActivity(intent)
        }

        override fun getItemCount() = data.size
    }

    companion object {

        private val DOWNLOAD_COMPARATOR = object : DiffUtil.ItemCallback<Asset>() {
            override fun areItemsTheSame(
                oldItem: Asset,
                newItem: Asset
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: Asset,
                newItem: Asset
            ): Boolean = oldItem.id == newItem.id
        }
    }
}