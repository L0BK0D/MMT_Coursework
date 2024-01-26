package com.example.mmt_coursework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class EndGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_game)
        val restart = findViewById<ImageButton>(R.id.restartButton)
        val menu = findViewById<ImageButton>(R.id.menuButton)
        val gameText = findViewById<TextView>(R.id.gameTV)
        gameText.text = DataHolder.gameResult
        restart.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        menu.setOnClickListener {
            //val intent = Intent(this, MenuActivity::class.java)
            //startActivity(intent)
            finish()
        }
    }


}