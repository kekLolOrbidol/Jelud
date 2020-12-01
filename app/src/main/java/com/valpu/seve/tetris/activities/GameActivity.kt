package com.valpu.seve.tetris.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.valpu.seve.tetris.R
import com.valpu.seve.tetris.models.AppModel
import com.valpu.seve.tetris.storage.AppPreferences
import com.valpu.seve.tetris.view.TetView
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    var tvHighScore: TextView? = null
    var tvCurrentScore: TextView? = null
    private lateinit var tetView: TetView
    private var animScale: Animation? = null

    var appPreferences: AppPreferences? = null
    private val appModel: AppModel = AppModel()

    //private lateinit var mpMoveSound: MediaPlayer

    companion object {

        //lateinit var mpSongSound: MediaPlayer
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setIcon(R.mipmap.ic_launcher)

        animScale = AnimationUtils.loadAnimation(this, R.anim.anim_alpha)

        appPreferences = AppPreferences(this)
        appModel.setPreferences(appPreferences)

        val btnRestart = btn_restart

//        mpMoveSound = MediaPlayer.create(this, R.raw.move)
//        mpSongSound = MediaPlayer.create(this, R.raw.song)

        tetView = view_tetris
        tvHighScore = tv_high_score
        tvCurrentScore = tv_current_score

        tetView.setActivity(this)
        tetView.setModel(appModel)
        tetView.setOnTouchListener(this::onTetrisViewTouch)

        btnRestart.setOnClickListener(this::btnRestartClick)

        updateHighScore()
        updateCurrentScore()
    }

    override fun onStop() {
        super.onStop()
        //mpSongSound.stop()
        appModel.resetModel()
    }

    private fun moveSound(){
        //mpMoveSound.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun btnClicked(view: View) {
        val button = view as Button

        if (appModel.isGameActive()) {
            when (button.id) {

                R.id.btn_left -> {
                    button.setOnTouchListener { view, _ ->
                        moveTetromino(AppModel.Motions.LEFT)
                        view.startAnimation(animScale)
                        true
                    }
                }
                R.id.btn_down -> {
                    button.setOnTouchListener { view, _ ->
                        moveTetromino(AppModel.Motions.DOWN)
                        view.startAnimation(animScale)
                        true
                    }
                }
                R.id.btn_right -> {
                    button.setOnTouchListener { view, _ ->
                        moveTetromino(AppModel.Motions.RIGHT)
                        view.startAnimation(animScale)
                        true
                    }
                }
                R.id.btn_rotate -> {
                    button.setOnTouchListener { view, _ ->
                        moveTetromino(AppModel.Motions.ROTATE)
                        view.startAnimation(animScale)
                        true
                    }
                }
            }
        }
    }

    private fun btnRestartClick(view: View) {
        view.startAnimation(animScale)
        if (appModel.isGameActive()) {

            appModel.restartGame()
            //mpSongSound.start()
        }
    }

    private fun onTetrisViewTouch(view: View, event: MotionEvent): Boolean {
        if (appModel.isGameOver() || appModel.isGameAwaitingStart()) {
            appModel.startGame()
           // mpSongSound.start()
            tetView.setGameCommandWithDelay(AppModel.Motions.DOWN)
        } else if (appModel.isGameActive()) {
            when (resolveTouchDirection(view, event)) {
                0 -> moveTetromino(AppModel.Motions.LEFT)
                1 -> moveTetromino(AppModel.Motions.ROTATE)
                //2 -> moveTetromino(AppModel.Motions.DOWN)
                3 -> moveTetromino(AppModel.Motions.RIGHT)
            }
        }
        return true
    }

    private fun resolveTouchDirection(view: View, event: MotionEvent): Int {
        val x = event.x / view.width
        val y = event.y / view.height
        return if (y > x) {
            if (x > 1 - y) 2 else 0
        } else {
            if (x > 1 - y) 3 else 1
        }
    }

    private fun moveTetromino(motion: AppModel.Motions) {
        if (appModel.isGameActive()) {
            moveSound()
            tetView.setGameCommand(motion)
        } else{
           // mpSongSound.pause()
        }
    }

    private fun updateHighScore() {
        tvHighScore?.text = "${appPreferences?.getHighScore()}"
    }

    private fun updateCurrentScore() {
        tvCurrentScore?.text = "0"
    }
}
