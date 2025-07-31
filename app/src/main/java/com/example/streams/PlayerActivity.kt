package com.example.streams

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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

    private lateinit var originalParent: ViewGroup
    private var originalIndex = -1
    private var originalRequestedOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    private var isFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        TPStreamsSDK.initialize(PROVIDER, ORG_CODE)

        parameters = intent.getParcelableExtra(TP_OFFLINE_PARAMS)
        val sharedPreference =  getSharedPreferences("player", Context.MODE_PRIVATE)
        pausedAt = sharedPreference.getLong("pausedAt", 0L)

        playerFragment = supportFragmentManager.findFragmentById(R.id.tpstream_player_fragment) as TpStreamPlayerFragment
        playerFragment.useSoftwareDecoder()
        playerFragment.setOnInitializationListener(object: InitializationListener {

            override fun onInitializationSuccess(player: TpStreamPlayer) {
                this@PlayerActivity.player = player
                playerView = playerFragment.tpStreamPlayerView
                setupFullscreen()
                loadPLayer()
                addPlayerListener()
            }
        });
    }

    fun setupFullscreen() {
        playerView.findViewById<ImageButton>(com.tpstream.player.R.id.fullscreen).setOnClickListener {
            if(isFullScreen) {
                exitFullScreen()
            } else {
                showFullScreen()
            }
        }
    }

    fun loadPLayer(){
        if (parameters == null){
            parameters = TpInitParams.Builder()
                .setVideoId(videoId)
                .setAccessToken(accessToken)
                .setUserId("testUser")
                .setOfflineLicenseExpireTime(FIFTEEN_DAYS)
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

            override fun onAccessTokenExpired(videoId: String, callback: (String) -> Unit) {
                when(videoId){
                    DRM_SAMPLE_VIDEO_ID -> callback(DRM_SAMPLE_ACCESS_TOKEN)
                    NON_DRM_SAMPLE_VIDEO_ID -> callback(NON_DRM_SAMPLE_ACCESS_TOKEN)
                }
            }

            override fun onPlayerError(playbackError: PlaybackError) {
                Toast.makeText(this@PlayerActivity,playbackError.name,Toast.LENGTH_SHORT).show()
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

    fun showFullScreen() {
        if (isFullScreen) return

        // Save parent info
        originalParent = playerView.parent as ViewGroup
        originalIndex = originalParent.indexOfChild(playerView)

        // Detach and add to root decor view
        originalParent.removeView(playerView)
        val root = window.decorView.findViewById<ViewGroup>(android.R.id.content)
        root.addView(
            playerView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Hide system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller?.hide(WindowInsetsCompat.Type.systemBars())

        // Landscape
        originalRequestedOrientation = requestedOrientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        isFullScreen = true
    }

    fun exitFullScreen() {
        if (!isFullScreen) return

        // Detach from root and put back
        val root = window.decorView.findViewById<ViewGroup>(android.R.id.content)
        root.removeView(playerView)
        originalParent.addView(playerView, originalIndex)

        // Show system bars
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller?.show(WindowInsetsCompat.Type.systemBars())

        // Restore orientation
        requestedOrientation = originalRequestedOrientation

        isFullScreen = false
    }
}