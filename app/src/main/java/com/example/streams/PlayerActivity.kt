package com.example.streams

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
    val TAG = "PlayerActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        playerFragment =
            supportFragmentManager.findFragmentById(R.id.tpstream_player_fragment) as TpStreamPlayerFragment
        playerFragment.setOnInitializationListener(object: InitializationListener {
            override fun onInitializationSuccess(player: TpStreamPlayer) {
                val parameters = TpInitParams.Builder()
                    .setVideoId(videoId)
                    .setAccessToken(accessToken)
                    .setOrgCode(orgCode)
                    .setAutoPlay(true)
                    .build()
                playerFragment.load(parameters)
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
}