package com.example.streams

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.media.MediaCodecList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tpstream.player.FIFTEEN_DAYS
import com.tpstream.player.TPStreamsSDK
import com.tpstream.player.TpInitParams

// organization id (Replace this ORG_CODE with yours)
const val ORG_CODE = "9q94nm"

// Provider (Replace this with yours)
val PROVIDER = TPStreamsSDK.Provider.TPStreams

// Sample DRM Video (Replace this params with yours)
const val DRM_SAMPLE_VIDEO_ID = "37ASbsmecu4"
const val DRM_SAMPLE_ACCESS_TOKEN = "79fde9c0-fd70-4f72-855f-a1c4306cc4ca"

// Sample Non-DRM Video (Replace this params with yours)
const val NON_DRM_SAMPLE_VIDEO_ID = "ACGhHuD7DEa"
const val NON_DRM_SAMPLE_ACCESS_TOKEN = "5bea276d-7882-4f8f-951a-c628622817e0"

// Sample Vertical Video (Replace this params with yours)
const val SAMPLE_VERTICAL_VIDEO_ID = "AzMCkjZRDTg"
const val SAMPLE_VERTICAL_ACCESS_TOKEN = "a11dd41e-e28b-4ca6-b403-22ee45587d8d"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        askPushNotificationPermission()
        findViewById<TextView>(R.id.text_view).text = getAVCCodecDetails()
    }

    private fun getAVCCodecDetails(): String {
        val codecInfos = MediaCodecList(MediaCodecList.ALL_CODECS).codecInfos
        val sb = StringBuilder()

        for (codecInfo in codecInfos) {
            if (!codecInfo.isEncoder) {
                val types = codecInfo.supportedTypes
                for (type in types) {
                    if (type.equals("video/avc", ignoreCase = true)) {
                        sb.appendLine("Codec Name: ${codecInfo.name}")
                        val capabilities = codecInfo.getCapabilitiesForType(type)
                        val videoCaps = capabilities.videoCapabilities

                        // Resolution
                        sb.appendLine("Supported Widths: ${videoCaps.supportedWidths}")
                        sb.appendLine("Supported Heights: ${videoCaps.supportedHeights}")
                        sb.appendLine("Width Alignment: ${videoCaps.widthAlignment}")
                        sb.appendLine("Height Alignment: ${videoCaps.heightAlignment}")

                        // Frame rate
                        sb.appendLine("Supported Frame Rates: ${videoCaps.supportedFrameRates}")

                        // Profile/Level as integers
                        val profileLevels = capabilities.profileLevels
                        for (pl in profileLevels) {
                            sb.appendLine("Profile: ${pl.profile}, Level: ${pl.level}")
                        }

                        sb.appendLine("--------")
                    }
                }
            }
        }

        return sb.toString()
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