package com.elenivoreos.notimetoworkout

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.elenivoreos.notimetoworkout.databinding.ActivityExerciseBinding
import java.util.*

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var restTimer: CountDownTimer? = null
    private var restProgress = 0
    private var restTimerDuration: Long =1

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseTimerDuration: Long =1

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null

    private var exerciseAdapter: ExerciseStatusAdapter? = null

    private var binding: ActivityExerciseBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this, this)
        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }
        setupRestView()
        setupExerciseStatusRecyclerView()

    }

    private fun setupExerciseStatusRecyclerView() {
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    /**
     * Function is used to set the timer for REST.
     */
    private fun setupRestView() {
        /**
         * Here firstly we will check if the timer is running and it is not null then cancel the running timer and start the new one.
         * And set the progress to initial which is 0.
         */

        try {
            val soundURI = Uri.parse(
                "android.resource://com.elenivoreos.notimetoworkout/" + R.raw.press_start
            )
            player = MediaPlayer.create(applicationContext, soundURI)
            player?.isLooping = false
            player?.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding?.frameLayoutProgressBar?.visibility = View.VISIBLE
        binding?.textViewTitle?.visibility = View.VISIBLE
        binding?.textViewExerciseName?.visibility = View.INVISIBLE
        binding?.frameLayoutProgressBarExercise?.visibility = View.INVISIBLE
        binding?.imageViewImage?.visibility = View.INVISIBLE
        binding?.textViewNextExerciseLabel?.visibility = View.VISIBLE
        binding?.textViewNextExercise?.visibility = View.VISIBLE


        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        binding?.textViewNextExercise?.text = exerciseList!![currentExercisePosition + 1].getName()

        setRestProgressBar()
    }

    /**
     * Function is used to set the progress of timer using the progress
     * */
    private fun setRestProgressBar() {
        binding?.progressBar?.progress = restProgress

        /**
         * @param millisInFuture The number of millis in the future from the call
         * to{#start()} until the countdown is done and {#onFinish()}
         * is called.
         * @param countDownInterval The interval along the way to receive
         * {#onTick(long)} callbacks.
         * */

        restTimer = object : CountDownTimer(1000, 1000) {
            override fun onTick(p0: Long) {
                restProgress++ //It is increased by 1
                binding?.progressBar?.progress = 10 - restProgress//Indicates progress bar progress
                binding?.textViewTimer?.text = (10 - restProgress)
                    .toString() // Current progress is set to text view in terms of seconds
            }

            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()

                setupExerciseView()
            }
        }.start()
    }

    /**
     * Function is used to set the progress of the timer using the progress for Exercise View.
     **/
    private fun setupExerciseView() {
        binding?.frameLayoutProgressBar?.visibility = View.INVISIBLE
        binding?.textViewTitle?.visibility = View.INVISIBLE
        binding?.textViewExerciseName?.visibility = View.VISIBLE
        binding?.frameLayoutProgressBarExercise?.visibility = View.VISIBLE
        binding?.imageViewImage?.visibility = View.VISIBLE
        binding?.textViewNextExerciseLabel?.visibility = View.INVISIBLE
        binding?.textViewNextExercise?.visibility = View.INVISIBLE

        /**
         * Here firstly we will check if the timer is running and it is not null
         * then cancel the running timer and start a new one.
         * And set the progress to the initial value which is 0.
         * */
        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.imageViewImage?.setImageResource(
            exerciseList!!
                    [currentExercisePosition].getImage()
        )
        binding?.textViewExerciseName?.text = exerciseList!![currentExercisePosition].getName()
        setExerciseProgressBar()
    }

    /**
     * Function is used to set the progress of the timer using the progress for Exercise View for 30 seconds
     **/
    private fun setExerciseProgressBar() {
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(exerciseTimerDuration*1000, 1000) {
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.textViewTimerExercise?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {

               if (currentExercisePosition < exerciseList?.size!! - 1) {
                   exerciseList!![currentExercisePosition].setIsSelected(false)
                   exerciseList!![currentExercisePosition].setIsComplete(true)
                   exerciseAdapter!!.notifyDataSetChanged()
                    setupRestView()
                } else {
                    finish()
                   val intent = Intent(this@ExerciseActivity,FinishActivity::class.java)
                   startActivity(intent)
                }
            }
        }.start()
    }

    /**
     * Here in the Destroy function we will reset the rest timer if it is running
     */
    override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        if (player != null)
            player!!.stop()



        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        binding = null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.ENGLISH)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                Log.e("TTS", "The Language is not supported!")

        } else {
            Log.e("TTS", "Initialization failed!")
        }
    }

    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}