package com.example.streams

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.tpstream.player.TpInitParams

const val SAMPLE_2_VIDEO_ID = "o7pOsacWaJt"
const val SAMPLE_2_ACCESS_TOKEN = "143a0c71-567e-4ecd-b22d-06177228c25b"
const val SAMPLE_3_VIDEO_ID = "qJQlWGLJvNv"
const val SAMPLE_3_ACCESS_TOKEN = "70f61402-3724-4ed8-99de-5473b2310efe"
const val SAMPLE_ORG_CODE = "demoveranda"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun sample1(view: View) {
        val myIntent = Intent(this, PlayerActivity::class.java)
        startActivity(myIntent)
    }

    fun sample2(view: View) {
        val parameters = TpInitParams.Builder()
            .setVideoId(SAMPLE_2_VIDEO_ID)
            .setAccessToken(SAMPLE_2_ACCESS_TOKEN)
            .setOrgCode(SAMPLE_ORG_CODE)
            .setAutoPlay(true)
            .enableDownloadSupport(true)
            .build()
        val myIntent = Intent(this, PlayerActivity::class.java)
        myIntent.putExtra(TP_OFFLINE_PARAMS,parameters)
        startActivity(myIntent)
    }

    fun sample3(view: View) {
        val parameters = TpInitParams.Builder()
            .setVideoId(SAMPLE_3_VIDEO_ID)
            .setAccessToken(SAMPLE_3_ACCESS_TOKEN)
            .setOrgCode(SAMPLE_ORG_CODE)
            .setAutoPlay(true)
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
}