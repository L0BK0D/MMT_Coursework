package com.example.mmt_coursework


import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.AnimationDrawable
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private var animationCount = 0
    private var currentBG = 0
    private lateinit var life1 :ImageView
    private lateinit var life2 :ImageView
    private lateinit var life3 :ImageView
    private lateinit var mainSprite:ImageButton
    private lateinit var badSprite:ImageView
    private lateinit var badSprite2:ImageView
    private lateinit var badSprite3:ImageView
    private lateinit var goodSprite:ImageView
    private lateinit var testText:TextView
    private  var goodSound:Int = 0
    private  var badSound:Int = 0
    private var hp = 3
    private var score = 0
    private var saveHP = -1
    private var endGame=false
    private lateinit var mSoundPool: SoundPool
    val attributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSoundPool = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .setMaxStreams(3)
            .build()
        val constraintLayout= findViewById<ConstraintLayout>(R.id.conlay)
        mainSprite = findViewById(R.id.mainSprite)
        badSprite = findViewById(R.id.badSprite)
        badSprite2 = findViewById(R.id.badSprite2)
        badSprite3 = findViewById(R.id.badSprite3)
        goodSprite = findViewById(R.id.goodSprite)
        testText = findViewById(R.id.testText)
        life1 = findViewById(R.id.life_im1)
        life2 = findViewById(R.id.life_im2)
        life3 = findViewById(R.id.life_im3)
        testText.text = score.toString()
        goodSound = mSoundPool.load(this, R.raw.goodsample, 1  )
        badSound = mSoundPool.load(this, R.raw.badsample, 1)
        lifecycleScope.launch {

            launch {
                startAnimationWithRandomY(badSprite, false)
            }
            delay(500)
            launch {
                startAnimationWithRandomY(badSprite2, false)
            }
            delay(500)
            launch {
                startAnimationWithRandomY(goodSprite,true)
            }
            delay(500)
                launch {
                    if (DataHolder.maxDiff) {
                        startAnimationWithRandomY(badSprite3, false)
                    }
                }
            val animationDrawable = constraintLayout.background as AnimationDrawable
            val animationDuration = 100L
            while (isActive) {
                    animationDrawable.selectDrawable(currentBG)
                    if(currentBG==0) currentBG++
                    else currentBG--
                    delay(animationDuration)

            }

        }
        mainSprite.setOnTouchListener { view, event ->
            val parentView = view.parent as ViewGroup
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.tag = Pair(event.rawX, event.rawY)
                }
                MotionEvent.ACTION_MOVE -> {
                    val currentX = event.rawX
                    val currentY = event.rawY
                    val (startX, startY) = (view.tag as Pair<Float, Float>)
                    val deltaY = currentY - startY
                    val newPosY = view.y + deltaY
                    val maxY = parentView.height - view.height.toFloat()
                    val clampedY = newPosY.coerceIn(0f, maxY)
                    ObjectAnimator.ofFloat(view, "y", clampedY).apply {
                        duration = 0
                        start()
                    }
                    view.tag = Pair(currentX, currentY)
                }
            }
            true
        }
    }

    private fun startAnimationWithRandomY(Sprite: ImageView, Sign:Boolean ) {
        lifecycleScope.launch {
            mainSprite.setImageResource(R.drawable.sprite1)
            val screenWidth = resources.displayMetrics.widthPixels.toFloat()
            val screenHeight = resources.displayMetrics.heightPixels.toFloat()
            val spriteWidth = Sprite.width.toFloat()
            val translationX = screenWidth + spriteWidth
            val finalX = -translationX
            val spriteHeight = Sprite.height.toFloat()
            val randomY = Random.nextInt((screenHeight - spriteHeight).toInt())
            Sprite.y = randomY.toFloat()
            Sprite.visibility = View.VISIBLE
            val translationXProperty = PropertyValuesHolder.ofFloat("translationX", translationX, finalX)
            val rotationProperty = PropertyValuesHolder.ofFloat("rotation", 0f, 360f)
            val animator = ObjectAnimator.ofPropertyValuesHolder(Sprite, translationXProperty, rotationProperty).apply {
                duration = DataHolder.speed.toLong()
                addUpdateListener {
                    checkCollision(Sprite, Sign)
                }
            }
            animator.start()
            if (animationCount < DataHolder.levelDifficult && !endGame) {
                delay(DataHolder.speed.toLong()+300)
                animationCount++
                startAnimationWithRandomY(Sprite, Sign)
            }
            else if (!endGame){
                DataHolder.gameResult = "Вы выиграли :-)"
                val intent = Intent(this@MainActivity, EndGameActivity::class.java)
                startActivity(intent)
                finish()
                endGame = true

            }
        }
    }

    private fun checkCollision(Sprite: ImageView, Sign:Boolean) {
        val userHitRect = Rect()
        mainSprite.getHitRect(userHitRect)
        val otherVisibleRect = Rect()
        Sprite.getGlobalVisibleRect(otherVisibleRect)
        if (userHitRect.intersect(otherVisibleRect) && saveHP!=animationCount&&!Sign) {
            hp--
            mSoundPool.play(badSound, 1f, 1f, 1,0,1f)
            //testText.text=hp.toString()
            saveHP = animationCount
            mainSprite.setImageResource(R.drawable.deadsprite)
            if (hp==2){
                life3.visibility = View.INVISIBLE
            }
            if (hp==1)
                life2.visibility = View.INVISIBLE
            if (hp==0){
                life1.visibility = View.INVISIBLE
                if(!endGame) {
                    DataHolder.gameResult = "Вы проиграли :-("
                    val intent = Intent(this, EndGameActivity::class.java)
                    startActivity(intent)
                    mSoundPool.stop(goodSound)
                    mSoundPool.stop(badSound)
                    endGame=true
                    finish()
                }
            }
        }
        if(userHitRect.intersect(otherVisibleRect) && saveHP!=animationCount&&Sign){
            score+=10
            mSoundPool.play(goodSound, 0.5f, 0.5f, 2, 0, 1f)
            testText.text=score.toString()
            saveHP = animationCount
        }
    }
    override fun onDestroy() {
        mSoundPool.release()
        super.onDestroy()
    }
}