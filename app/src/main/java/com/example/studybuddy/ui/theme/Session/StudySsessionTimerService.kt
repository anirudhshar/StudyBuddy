package com.example.studybuddy.ui.theme.Session

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.example.studybuddy.util.Constants.ACTION_SERVICE_CANCEL
import com.example.studybuddy.util.Constants.ACTION_SERVICE_START
import com.example.studybuddy.util.Constants.ACTION_SERVICE_STOP
import com.example.studybuddy.util.Constants.NOTIFICATION_CHANNEL_ID
import com.example.studybuddy.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.studybuddy.util.Constants.NOTIFICATION_ID
import com.example.studybuddy.util.pad
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


@AndroidEntryPoint
class StudySessionTimerService : android.app.Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    private val binder = StudySessionTimerBinder()

    private lateinit var timer: Timer

    var duration: Duration = Duration.ZERO
        private set     // this key word is used to ensure that
                        // this variable can be written only in this file
                         // and outside this file it should for ready only.


    var seconds= mutableStateOf("00")
        private set

    var minutes= mutableStateOf("00")
        private set

    var hours= mutableStateOf("00")
        private set

    var currentTimerState = mutableStateOf(TimerState.IDLE)
    private set      // this variable is taking value from the enum class where only 3 possible states of the timer
                     // have been defined.

    var subjectId = mutableStateOf<Int?>(null)





    override fun onBind(p0: Intent?) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.action.let {
            when (it) {
                ACTION_SERVICE_START -> {
                    startForegroundService()
                    startTimer{
                        hours, minutes, seconds ->
                        updateNotification(hours, minutes, seconds)
                    }


                }

                ACTION_SERVICE_STOP -> {
                    stopTimer()

                }

                ACTION_SERVICE_CANCEL -> {
                    stopTimer()
                    cancelTimer()
                    stopForegroundService()

                }


            }
        }
        return super.onStartCommand(intent, flags, startId)

    }


    private fun startForegroundService() {
        createNotificationChannel()

        startForeground(NOTIFICATION_ID,notificationBuilder.build())



    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW

            )
        notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(hours:String, minutes:String, seconds:String){
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText(
                "$hours:$minutes:$seconds"
            ).build()
        )
    }


    private fun startTimer(
        onTick :(h:String, m:String, s:String) -> Unit

    ){
        currentTimerState.value= TimerState.STARTED  //set the state to start when startTimer fun is called

        timer= fixedRateTimer(initialDelay =1000L, period = 1000L){
            duration = duration.plus(1.seconds)
            updateTimeUnits()
            onTick(hours.value, minutes.value, seconds.value)
        }

    }

    private fun updateTimeUnits(){
        duration.toComponents { hours, minutes, seconds, _ ->
            this@StudySessionTimerService.hours.value= hours.toInt().pad()
            this@StudySessionTimerService.minutes.value= minutes.pad()
            this@StudySessionTimerService.seconds.value= seconds.pad()
        }


    }

    private fun stopTimer(){

        if(this::timer.isInitialized){
            timer.cancel()
        }
        currentTimerState.value= TimerState.STOPPED


    }

    private fun cancelTimer(){
//        stopTimer()
        duration= Duration.ZERO
        updateTimeUnits()
        currentTimerState.value= TimerState.IDLE

    }

    private fun stopForegroundService(){
        notificationManager.cancel(
            NOTIFICATION_ID
        )
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    inner class StudySessionTimerBinder : Binder(){
        fun getService(): StudySessionTimerService = this@StudySessionTimerService
    }


}

enum class TimerState(){

    IDLE,
    STARTED,
    STOPPED,


}