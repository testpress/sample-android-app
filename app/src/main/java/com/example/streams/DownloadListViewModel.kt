package com.example.streams

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tpstream.player.TpStreamDownloadManager
import com.tpstream.player.models.Video

class DownloadListViewModel(context: Context): ViewModel() {

    private var tpStreamDownloadManager: TpStreamDownloadManager = TpStreamDownloadManager(context)

    fun getDownloadData(): LiveData<List<Video>?> {
        return tpStreamDownloadManager.getAllDownloads()
    }

    fun pauseDownload(offlineVideoInfo: Video) {
        tpStreamDownloadManager.pauseDownload(offlineVideoInfo)
    }

    fun resumeDownload(offlineVideoInfo: Video) {
        tpStreamDownloadManager.resumeDownload(offlineVideoInfo)
    }

    fun cancelDownload(offlineVideoInfo: Video) {
        tpStreamDownloadManager.cancelDownload(offlineVideoInfo)
    }

    fun deleteDownload(offlineVideoInfo: Video) {
        tpStreamDownloadManager.deleteDownload(offlineVideoInfo)
    }
}