package com.example.streams

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.tpstream.player.TPStreamPlayerListener
import com.tpstream.player.TPStreamsSDK
import com.tpstream.player.TpInitParams
import com.tpstream.player.TpStreamPlayer
import com.tpstream.player.ui.InitializationListener
import com.tpstream.player.ui.TPStreamPlayerView
import com.tpstream.player.ui.TpStreamPlayerFragment

class PlayerFragment : Fragment() {

    lateinit var player: TpStreamPlayer
    lateinit var playerView: TPStreamPlayerView
    lateinit var playerFragment: TpStreamPlayerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TPStreamsSDK.initialize(TPStreamsSDK.Provider.TestPress, ORG_CODE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerFragment = childFragmentManager.findFragmentById(R.id.tpstream_player_fragment) as TpStreamPlayerFragment
        playerFragment.setOnInitializationListener(object: InitializationListener {

            override fun onInitializationSuccess(player: TpStreamPlayer) {
                this@PlayerFragment.player = player
                playerView = playerFragment.tpStreamPlayerView
                loadPLayer()
                addPlayerListener()
            }
        })
        playerFragment.enableAutoFullScreenOnRotate()
    }

    fun loadPLayer() {
        val parameters = TpInitParams.Builder()
            .setVideoId(NON_DRM_SAMPLE_VIDEO_ID)
            .setAccessToken(NON_DRM_SAMPLE_ACCESS_TOKEN)
            .setAutoPlay(true)
            .build()
        player.load(parameters)
        playerFragment.showFullScreen()
    }

    private fun addPlayerListener(){
        player.setListener( object : TPStreamPlayerListener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                Log.d("TAG", "onPlaybackStateChanged: $playbackState")
            }

            override fun onMarkerCallback(timesInSeconds: Long) {
                Toast.makeText(requireContext(),"Time $timesInSeconds", Toast.LENGTH_SHORT).show()
            }
        })
    }

}