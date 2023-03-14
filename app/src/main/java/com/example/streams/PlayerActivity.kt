package com.example.streams

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.media3.common.*
import com.tpstream.player.*

class PlayerActivity : AppCompatActivity() {
    lateinit var playerFragment: TpStreamPlayerFragment;
    private val accessToken = "c381512b-7337-4d8e-a8cf-880f4f08fd08"
    private val videoId = "C3XLe1CCcOq"
    private val orgCode = "demoveranda"
    lateinit var player: TpStreamPlayer
    private var parameters : TpInitParams? = null
    val TAG = "PlayerActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        parameters = intent.getParcelableExtra(TP_OFFLINE_PARAMS)
        playerFragment =
            supportFragmentManager.findFragmentById(R.id.tpstream_player_fragment) as TpStreamPlayerFragment
        playerFragment.setOnInitializationListener(object: InitializationListener {
            val sharedPreference =  getSharedPreferences("player", Context.MODE_PRIVATE)
            val pausedAt = sharedPreference.getLong("pausedAt", 0L)

            override fun onInitializationSuccess(player: TpStreamPlayer) {
                this@PlayerActivity.player = player
                if (parameters == null){
                    parameters = TpInitParams.Builder()
                        .setVideoId(videoId)
                        .setAccessToken(accessToken)
                        .setOrgCode(orgCode)
                        .startAt(pausedAt)
                        .enableDownloadSupport(true)
                        .build()
                }
                playerFragment.load(parameters!!)
                addPlayerListener()
            }
        });
        playerFragment.enableAutoFullScreenOnRotate()
    }

    private fun addPlayerListener(){
        player.setListener( object : TPStreamPlayerListener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                Log.d(TAG, "onPlaybackStateChanged: $playbackState")
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