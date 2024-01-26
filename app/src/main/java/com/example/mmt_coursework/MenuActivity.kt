package com.example.mmt_coursework

import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.mmt_coursework.DataHolder

class MenuActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)



        if(!DataHolder.soundplay){
            mediaPlayer = MediaPlayer.create(this, R.raw.soundtrack)
            // Циклическое воспроизведение
            mediaPlayer.isLooping = true
            mediaPlayer.start()
            DataHolder.soundplay = true
        }



        val start1 = findViewById<Button>(R.id.startButton1)
        val start2 = findViewById<Button>(R.id.startButton2)
        val start3 = findViewById<Button>(R.id.startButton3)
        val start4 = findViewById<Button>(R.id.startButton4)
        val title = findViewById<TextView>(R.id.textView)
        val btnClose = findViewById<Button>(R.id.btnClose)
        start1.setTextColor(Color.WHITE)
        start2.setTextColor(Color.WHITE)
        start3.setTextColor(Color.WHITE)
        start4.setTextColor(Color.WHITE)
        btnClose.setTextColor(Color.WHITE)
        title.setTextColor(Color.WHITE)
        start1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            DataHolder.levelDifficult = 30
            DataHolder.speed = 4000
            startActivity(intent)
        }
        start2.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            DataHolder.levelDifficult = 40
            DataHolder.speed = 3000
            startActivity(intent)
        }
        start3.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            DataHolder.levelDifficult = 50
            DataHolder.speed = 3000
            DataHolder.maxDiff = true
            startActivity(intent)
        }
        start4.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            DataHolder.levelDifficult = 50
            DataHolder.maxDiff = true
            DataHolder.speed = 2000
            startActivity(intent)
        }
        btnClose.setOnClickListener {
            finish()
        }

    }
    override fun onDestroy() {
        if (mediaPlayer.isPlaying){
        mediaPlayer.stop()
        mediaPlayer.release()
        }
        super.onDestroy()
    }

}