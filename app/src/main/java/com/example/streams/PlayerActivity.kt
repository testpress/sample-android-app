package com.example.streams

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.media3.common.Player.EVENT_IS_PLAYING_CHANGED
import com.tpstream.player.*
import com.tpstream.player.constants.PlaybackError
import com.tpstream.player.ui.InitializationListener
import com.tpstream.player.ui.TPStreamPlayerView
import com.tpstream.player.ui.TpStreamPlayerFragment

class PlayerActivity : AppCompatActivity() {
    lateinit var player: TpStreamPlayer
    lateinit var playerView: TPStreamPlayerView
    lateinit var playerFragment: TpStreamPlayerFragment
    private val accessToken = "c381512b-7337-4d8e-a8cf-880f4f08fd08"
    private val videoId = "C3XLe1CCcOq"
    private var parameters : TpInitParams? = null
    private var pausedAt: Long = 0L
    val TAG = "PlayerActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        TPStreamsSDK.initialize(TPStreamsSDK.Provider.TestPress, ORG_CODE)

        parameters = intent.getParcelableExtra(TP_OFFLINE_PARAMS)
        val sharedPreference =  getSharedPreferences("player", Context.MODE_PRIVATE)
        pausedAt = sharedPreference.getLong("pausedAt", 0L)

        playerFragment = supportFragmentManager.findFragmentById(R.id.tpstream_player_fragment) as TpStreamPlayerFragment
        playerFragment.setOnInitializationListener(object: InitializationListener {

            override fun onInitializationSuccess(player: TpStreamPlayer) {
                this@PlayerActivity.player = player
                player.setMaxResolution(560)
                playerView = playerFragment.tpStreamPlayerView
                loadPLayer()
                addPlayerListener()
                addWaterMark()
                addMarker()
            }
        });
        playerFragment.enableAutoFullScreenOnRotate()
        playerFragment.setPreferredFullscreenExitOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
    }

    fun loadPLayer(){
        if (parameters == null){
            parameters = TpInitParams.Builder()
                .setVideoId(videoId)
                .setAccessToken(accessToken)
                .startAt(pausedAt)
                .enableDownloadSupport(true)
                .build()
        }
        player.load(parameters!!)
    }

    private fun addPlayerListener(){
        player.setListener( object : TPStreamPlayerListener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                Log.d(TAG, "onPlaybackStateChanged: $playbackState")
            }

            override fun onMarkerCallback(timesInSeconds: Long) {
                Toast.makeText(this@PlayerActivity,"Time $timesInSeconds",Toast.LENGTH_SHORT).show()
            }

            override fun onPlayerError(playbackError: PlaybackError) {
                Toast.makeText(this@PlayerActivity,playbackError.name,Toast.LENGTH_SHORT).show()
            }

            override fun onEvents(player: TpStreamPlayer?, events: PlayerEvents) {
                if (events.contains(EVENT_IS_PLAYING_CHANGED)){
                    Log.d("TAG", "playing changed")
                }
            }
        })
    }

    private fun addWaterMark(){
        playerView.enableWaterMark("Sample App",Color.RED)
    }

    private fun addMarker(){
        playerView.setMarkers(longArrayOf(60,120,180,240,300),Color.YELLOW, deleteAfterDelivery = false)
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