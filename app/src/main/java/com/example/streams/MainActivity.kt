package com.example.streams

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.tpstream.player.TpInitParams

// organization id (Replace this ORG_CODE with yours)
const val ORG_CODE = "edee9b"

// Sample DRM Video (Replace this params with yours)
const val DRM_SAMPLE_VIDEO_ID = "7kHJKnME3p2"
const val DRM_SAMPLE_ACCESS_TOKEN = "14fe790e-2ce7-48de-b428-9f45eed77887"

// Sample Non-DRM Video (Replace this params with yours)
const val NON_DRM_SAMPLE_VIDEO_ID = "ATJfRdHIUC9"
const val NON_DRM_SAMPLE_ACCESS_TOKEN = "a4c04ca8-9c0e-4c9c-a889-bd3bf8ea586a"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun sample1(view: View) {
        val parameters = TpInitParams.Builder()
            .setVideoId(DRM_SAMPLE_VIDEO_ID)
            .setAccessToken(DRM_SAMPLE_ACCESS_TOKEN)
            .enableDownloadSupport(true)
            .build()
        val myIntent = Intent(this, PlayerActivity::class.java)
        myIntent.putExtra(TP_OFFLINE_PARAMS,parameters)
        startActivity(myIntent)
    }

    fun sample2(view: View) {
        val parameters = TpInitParams.Builder()
            .setVideoId(NON_DRM_SAMPLE_VIDEO_ID)
            .setAccessToken(NON_DRM_SAMPLE_ACCESS_TOKEN)
            .enableDownloadSupport(true)
            .build()
        val myIntent = Intent(this, PlayerActivity::class.java)
        myIntent.putExtra(TP_OFFLINE_PARAMS,parameters)
        startActivity(myIntent)
    }

    fun downloadListClick(view: View) {
        val myIntent = Intent(this, DownloadListActivity::class.java)
        startActivity(myIntent)
    }

    fun openFragmentActivity(view: View) {
        val myIntent = Intent(this, PlayerFragmentActivity::class.java)
        startActivity(myIntent)
    }

    fun openPlayerActivityJava(view: View) {
        val myIntent = Intent(this, PlayerActivityJava::class.java)
        startActivity(myIntent)
    }
}