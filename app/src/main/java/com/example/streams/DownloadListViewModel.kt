package com.example.streams

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tpstream.player.data.Asset
import com.tpstream.player.offline.TpStreamDownloadManager

class DownloadListViewModel(context: Context): ViewModel() {

    private var tpStreamDownloadManager: TpStreamDownloadManager = TpStreamDownloadManager(context)

    fun getDownloadData(): LiveData<List<Asset>?> {
        return tpStreamDownloadManager.getAllDownloads()
    }

    fun getAssetsByMetadata(metadata: Map<String, String>): LiveData<List<Asset>?> {
        return tpStreamDownloadManager.getAssetsByMetadata(metadata)
    }

    fun pauseDownload(offlineAssetInfo: Asset) {
        tpStreamDownloadManager.pauseDownload(offlineAssetInfo)
    }

    fun resumeDownload(offlineAssetInfo: Asset) {
        tpStreamDownloadManager.resumeDownload(offlineAssetInfo)
    }

    fun cancelDownload(offlineAssetInfo: Asset) {
        tpStreamDownloadManager.cancelDownload(offlineAssetInfo)
    }

    fun deleteDownload(offlineAssetInfo: Asset) {
        tpStreamDownloadManager.deleteDownload(offlineAssetInfo)
    }
}