package com.example.streams

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tpstream.player.FIFTEEN_DAYS
import com.tpstream.player.TpInitParams

// organization id (Replace this ORG_CODE with yours)
const val ORG_CODE = "6eafqn"

// Sample DRM Video (Replace this params with yours)
const val DRM_SAMPLE_VIDEO_ID = "6suEBPy7EG4"
const val DRM_SAMPLE_ACCESS_TOKEN = "ab70caed-6168-497f-89c1-1e308da2c9aa"

// Sample Non-DRM Video (Replace this params with yours)
const val NON_DRM_SAMPLE_VIDEO_ID = "8DjR3FzHy4Z"
const val NON_DRM_SAMPLE_ACCESS_TOKEN = "0cebd232-3699-4908-81f0-3cc2fa9497f8"

// Sample Vertical Video (Replace this params with yours)
const val SAMPLE_VERTICAL_VIDEO_ID = "AzMCkjZRDTg"
const val SAMPLE_VERTICAL_ACCESS_TOKEN = "a11dd41e-e28b-4ca6-b403-22ee45587d8d"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        askPushNotificationPermission()
    }

    fun sample1(view: View) {
        val parameters = TpInitParams.Builder()
            .setVideoId(DRM_SAMPLE_VIDEO_ID)
            .setAccessToken(DRM_SAMPLE_ACCESS_TOKEN)
            .setUserId("testUser")
            .setOfflineLicenseExpireTime(FIFTEEN_DAYS)
            .setInitialResolution(720)
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
            .setUserId("TestUser")
            .setInitialResolution(480)
            .enableDownloadSupport(true)
            .build()
        val myIntent = Intent(this, PlayerActivity::class.java)
        myIntent.putExtra(TP_OFFLINE_PARAMS,parameters)
        startActivity(myIntent)
    }

    fun sample3(view: View) {
        val myIntent = Intent(this, VerticalPlayerActivity::class.java)
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

    private fun askPushNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    POST_NOTIFICATIONS
                ) != PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(POST_NOTIFICATIONS),
                    1000
                )
            }
        }
    }
}