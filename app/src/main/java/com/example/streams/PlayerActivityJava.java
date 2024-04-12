package com.example.streams;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tpstream.player.TPStreamsSDK;
import com.tpstream.player.TpInitParams;
import com.tpstream.player.TpStreamPlayer;
import com.tpstream.player.ui.InitializationListener;
import com.tpstream.player.ui.TPStreamPlayerView;
import com.tpstream.player.ui.TpStreamPlayerFragment;

public class PlayerActivityJava extends AppCompatActivity {
    private TpStreamPlayer player;
    private TPStreamPlayerView playerView;
    private TpStreamPlayerFragment playerFragment;
    private long pausedAt = 0L;
    private static final String ORG_CODE = "lmsdemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_java);
        TPStreamsSDK.INSTANCE.initialize(TPStreamsSDK.Provider.TestPress, ORG_CODE);

        SharedPreferences sharedPreference = getSharedPreferences("player", Context.MODE_PRIVATE);
        pausedAt = sharedPreference.getLong("pausedAt", 0L);

        playerFragment = (TpStreamPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.tpstream_player_fragment);
        playerFragment.setOnInitializationListener(new InitializationListener() {
            @Override
            public void onInitializationSuccess(@NonNull TpStreamPlayer tpStreamPlayer) {
                PlayerActivityJava.this.player = tpStreamPlayer;
                player.setMaxResolution(560);
                playerView = playerFragment.getTpStreamPlayerView();
                loadPLayer();
            }
        });
        playerFragment.enableAutoFullScreenOnRotate();
        playerFragment.setPreferredFullscreenExitOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    private void loadPLayer() {
        String accessToken = "a4c04ca8-9c0e-4c9c-a889-bd3bf8ea586a";
        String videoId = "ATJfRdHIUC9";
        TpInitParams parameters = new TpInitParams.Builder()
                .setVideoId(videoId)
                .setAccessToken(accessToken)
                .startAt(pausedAt)
                .enableDownloadSupport(true)
                .build();
        player.load(parameters);
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

