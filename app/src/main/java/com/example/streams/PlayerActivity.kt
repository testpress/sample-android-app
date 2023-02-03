package com.example.streams

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.media3.common.*
import com.tpstream.player.*

class PlayerActivity : AppCompatActivity() {
    lateinit var playerFragment: TpStreamPlayerFragment;
    private val accessToken = "bbf23112-0c14-4519-a848-73c95cb024ac"
    private val videoId = "E44ulfSWhYx"
    private val orgCode = "drm"
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
                this@PlayerActivity.player = player
            }
        });
        playerFragment.enableAutoFullScreenOnRotate()
        playerFragment.playbackStateListener = object : TPPlayerListener {
            override fun onDeviceInfoChanged(deviceInfo: DeviceInfo) {
                Log.d(TAG, "onDeviceInfoChanged: ")
            }

            override fun onEvents(player: TpStreamPlayer?, events: Player.Events) {
                Log.d(TAG, "onEvents: ")
            }

            override fun onIsLoadingChanged(loading: Boolean) {
                Log.d(TAG, "onIsLoadingChanged: ")
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                Log.d(TAG, "onIsPlayingChanged: ")
            }

            override fun onMetadata(metadata: Metadata) {
                Log.d(TAG, "onMetadata: ")
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                Log.d(TAG, "onPlayWhenReadyChanged: ")
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                /*
                * Playback states can be compared with TpStreamPlayer.PLAYBACK_STATE
                * */
                Log.d(TAG, "onPlaybackStateChanged: ")
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.d(TAG, "onPlayerError: ")
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                Log.d(TAG, "onPlayerErrorChanged: ")
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                Log.d(TAG, "onPositionDiscontinuity: ")
            }

            override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
                Log.d(TAG, "onSeekBackIncrementChanged: ")
            }

            override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
                Log.d(TAG, "onSeekForwardIncrementChanged: ")
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                Log.d(TAG, "onTimelineChanged: ")
            }

            override fun onTracksChanged(tracks: Tracks) {
                Log.d(TAG, "onTracksChanged: ")
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                Log.d(TAG, "onVideoSizeChanged: ")
            }

        }
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