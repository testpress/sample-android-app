package com.example.streams;

import static androidx.media3.common.Player.EVENT_IS_PLAYING_CHANGED;

import static com.example.streams.MainActivityKt.DRM_SAMPLE_ACCESS_TOKEN;
import static com.example.streams.MainActivityKt.DRM_SAMPLE_VIDEO_ID;
import static com.example.streams.MainActivityKt.NON_DRM_SAMPLE_ACCESS_TOKEN;
import static com.example.streams.MainActivityKt.NON_DRM_SAMPLE_VIDEO_ID;
import static com.example.streams.MainActivityKt.ORG_CODE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.DeviceInfo;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;
import androidx.media3.common.Tracks;
import androidx.media3.common.VideoSize;

import com.tpstream.player.TPStreamPlayerListener;
import com.tpstream.player.TPStreamsSDK;
import com.tpstream.player.TpInitParams;
import com.tpstream.player.TpInitParamsKt;
import com.tpstream.player.TpStreamPlayer;
import com.tpstream.player.constants.PlaybackError;
import com.tpstream.player.ui.InitializationListener;
import com.tpstream.player.ui.TPStreamPlayerView;
import com.tpstream.player.ui.TpStreamPlayerFragment;

import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class PlayerActivityJava extends AppCompatActivity {
    private TpStreamPlayer player;
    private TPStreamPlayerView playerView;
    private TpStreamPlayerFragment playerFragment;
    private long pausedAt = 0L;
    private String TAG = "PlayerActivityJava";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_java);
        TPStreamsSDK.INSTANCE.initialize(TPStreamsSDK.Provider.TPStreams, ORG_CODE);

        SharedPreferences sharedPreference = getSharedPreferences("player", Context.MODE_PRIVATE);
        pausedAt = sharedPreference.getLong("pausedAt", 0L);

        playerFragment = (TpStreamPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.tpstream_player_fragment);
        playerFragment.setOnInitializationListener(new InitializationListener() {
            @Override
            public void onInitializationSuccess(@NonNull TpStreamPlayer tpStreamPlayer) {
                PlayerActivityJava.this.player = tpStreamPlayer;
                playerView = playerFragment.getTpStreamPlayerView();
                loadPLayer();
                addPlayerListener();
            }
        });
        playerFragment.enableAutoFullScreenOnRotate();
        playerFragment.setPreferredFullscreenExitOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    private void loadPLayer() {
        TpInitParams parameters = new TpInitParams.Builder()
                .setVideoId(DRM_SAMPLE_VIDEO_ID)
                .setAccessToken(DRM_SAMPLE_ACCESS_TOKEN)
                .setOfflineLicenseExpireTime(TpInitParamsKt.FIFTEEN_DAYS)
                .startAt(pausedAt)
                .enableDownloadSupport(true)
                .build();
        player.load(parameters,getMetadata());
    }

    private HashMap<String,String> getMetadata() {
        HashMap<String,String> metadata = new HashMap<>();
        metadata.put("userId","123");
        return metadata;
    }

    private void addPlayerListener() {
        player.setListener(new TPStreamPlayerListener() {
            @Override
            public void onPlaybackSpeedChange(float v) {
                Log.d(TAG, "onPlaybackSpeedChange: speed"+v);
            }

            @Override
            public void onAccessTokenExpired(@NonNull String s, @NonNull Function1<? super String, Unit> function1) {
                switch (s) {
                    case DRM_SAMPLE_VIDEO_ID: {
                        function1.invoke(DRM_SAMPLE_ACCESS_TOKEN);
                        break;
                    }
                    case NON_DRM_SAMPLE_VIDEO_ID: {
                        function1.invoke(NON_DRM_SAMPLE_ACCESS_TOKEN);
                        break;
                    }
                }
            }

            @Override
            public void onTracksChanged(@NonNull Tracks tracks) {

            }

            @Override
            public void onIsPlayingChanged(boolean b) {

            }

            @Override
            public void onIsLoadingChanged(boolean b) {

            }

            @Override
            public void onDeviceInfoChanged(@NonNull DeviceInfo deviceInfo) {

            }

            @Override
            public void onPlayWhenReadyChanged(boolean b, int i) {

            }

            @Override
            public void onEvents(@Nullable TpStreamPlayer tpStreamPlayer, @NonNull Player.Events events) {
                if (events.contains(EVENT_IS_PLAYING_CHANGED)){
                    Log.d("TAG", "playing changed");
                }
            }

            @Override
            public void onSeekBackIncrementChanged(long l) {

            }

            @Override
            public void onSeekForwardIncrementChanged(long l) {

            }

            @Override
            public void onVideoSizeChanged(@NonNull VideoSize videoSize) {

            }

            @Override
            public void onPositionDiscontinuity(@NonNull Player.PositionInfo positionInfo, @NonNull Player.PositionInfo positionInfo1, int i) {

            }

            @Override
            public void onTimelineChanged(@NonNull Timeline timeline, int i) {

            }

            @Override
            public void onPlaybackStateChanged(int i) {
                Log.d(TAG, "onPlaybackStateChanged: $playbackState");
            }

            @Override
            public void onPlayerError(@NonNull PlaybackError playbackError) {
                Toast.makeText(PlayerActivityJava.this,playbackError.name(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMarkerCallback(long l) {

            }

            @Override
            public void onFullScreenChanged(boolean b) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sharedPreference = getSharedPreferences("player", Context.MODE_PRIVATE);

        if (player != null) {
            SharedPreferences.Editor editor = sharedPreference.edit();
            editor.putLong("pausedAt", (player.getCurrentTime() / 1000L));
            editor.apply();
        }
    }
}

