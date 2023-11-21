package com.example.streams

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PlayerFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_fragment)
        supportFragmentManager.beginTransaction().apply {
            this.replace(R.id.fragment,PlayerFragment())
            this.commit()
        }
    }
}