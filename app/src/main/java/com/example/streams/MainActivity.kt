package com.example.streams

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.tpstream.player.TpInitParams

// Sample DRM Video (Replace this params with yours)
const val SAMPLE_1_VIDEO_ID = "z1TLpfuZzXh"
const val SAMPLE_1_ACCESS_TOKEN = "5c49285b-0557-4cef-b214-66034d0b77c3"
const val SAMPLE_1_ORG_CODE = "lmsdemo"
// Sample Non-DRM Video
const val SAMPLE_2_VIDEO_ID = "XRvyrS2CSju"
const val SAMPLE_2_ACCESS_TOKEN = "87dcb513-4535-4be0-b91a-486f008086ff"
const val SAMPLE_2_ORG_CODE = "lmsdemo"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_two)
    }

    fun sample1(view: View) {
        val parameters = TpInitParams.Builder()
            .setVideoId(SAMPLE_1_VIDEO_ID)
            .setAccessToken(SAMPLE_1_ACCESS_TOKEN)
            .setOrgCode(SAMPLE_1_ORG_CODE)
            .enableDownloadSupport(true)
            .build()
        val myIntent = Intent(this, PlayerActivity::class.java)
        myIntent.putExtra(TP_OFFLINE_PARAMS,parameters)
        startActivity(myIntent)
    }

    fun sample2(view: View) {
        val parameters = TpInitParams.Builder()
            .setVideoId(SAMPLE_2_VIDEO_ID)
            .setAccessToken(SAMPLE_2_ACCESS_TOKEN)
            .setOrgCode(SAMPLE_2_ORG_CODE)
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

    fun playButton(view: View) {

        var orgcode = findViewById<EditText>(R.id.org_code).text.toString()
        var accessToken = findViewById<EditText>(R.id.access_token).text.toString()
        var videoId = findViewById<EditText>(R.id.video_id).text.toString()

        val parameters = TpInitParams.Builder()
            .setVideoId(videoId)
            .setAccessToken(accessToken)
            .setOrgCode(orgcode)
            .enableDownloadSupport(true)
            .build()
        val myIntent = Intent(this, PlayerActivity::class.java)
        myIntent.putExtra(TP_OFFLINE_PARAMS,parameters)
        startActivity(myIntent)
    }

}