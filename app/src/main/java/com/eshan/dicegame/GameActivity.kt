package com.eshan.dicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameScreen()
        }
    }
}

