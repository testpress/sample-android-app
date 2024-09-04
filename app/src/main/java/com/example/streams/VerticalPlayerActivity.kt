package com.example.streams

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tpstream.player.*
import com.tpstream.player.ui.InitializationListener
import com.tpstream.player.ui.TPStreamPlayerView
import com.tpstream.player.ui.TpStreamPlayerFragment

class VerticalPlayerActivity : AppCompatActivity() {
    lateinit var player: TpStreamPlayer
    lateinit var playerView: TPStreamPlayerView
    lateinit var playerFragment: TpStreamPlayerFragment
    private var parameters : TpInitParams? = null
    private var pausedAt: Long = 0L
    val TAG = "VerticalPlayerActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_vertical)
        TPStreamsSDK.initialize(TPStreamsSDK.Provider.TPStreams, ORG_CODE)

        val sharedPreference =  getSharedPreferences("player", Context.MODE_PRIVATE)
        pausedAt = sharedPreference.getLong("pausedAt", 0L)

        playerFragment = supportFragmentManager.findFragmentById(R.id.tpstream_player_fragment) as TpStreamPlayerFragment
        playerFragment.setOnInitializationListener(object: InitializationListener {

            override fun onInitializationSuccess(player: TpStreamPlayer) {
                this@VerticalPlayerActivity.player = player
                player.setMaxResolution(560)
                playerView = playerFragment.tpStreamPlayerView
                loadPLayer()
                addPlayerListener()
            }
        });
        playerFragment.enableAutoFullScreenOnRotate()
        playerFragment.setPreferredFullscreenExitOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
    }

    fun loadPLayer() {
        parameters = TpInitParams.Builder()
            .setVideoId(SAMPLE_VERTICAL_VIDEO_ID)
            .setAccessToken(SAMPLE_VERTICAL_ACCESS_TOKEN)
            .setUserId("testUser")
            .setOfflineLicenseExpireTime(FIFTEEN_DAYS)
            .startAt(pausedAt)
            .build()

        player.load(parameters!!)
    }

    private fun addPlayerListener(){
        player.setListener( object : TPStreamPlayerListener {

            override fun onAccessTokenExpired(videoId: String, callback: (String) -> Unit) {
                when(videoId){
                    DRM_SAMPLE_VIDEO_ID -> callback(DRM_SAMPLE_ACCESS_TOKEN)
                    NON_DRM_SAMPLE_VIDEO_ID -> callback(NON_DRM_SAMPLE_ACCESS_TOKEN)
                }
            }
        })
    }

    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
        val sharedPreference =  getSharedPreferences("player", Context.MODE_PRIVATE)

        if (::player.isInitialized) {
            with (sharedPreference.edit()) {
                putLong("pausedAt", (player.getCurrentTime()/1000L))
                apply()
            }
        }
    }
}