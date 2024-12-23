package com.example.studybuddy

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.studybuddy.domain.model.Sessions
import com.example.studybuddy.domain.model.Task
import com.example.studybuddy.ui.theme.NavGraphs

import com.example.studybuddy.ui.theme.Session.SessionScreen
import com.example.studybuddy.ui.theme.Session.StudySessionTimerService
import com.example.studybuddy.ui.theme.StudyBuddyTheme
//import com.example.studybuddy.ui.theme.Task.TaskScreen
import com.example.studybuddy.ui.theme.dashboard.DashBoardScreen
import com.example.studybuddy.ui.theme.destinations.SessionScreenRouteDestination

import com.example.studybuddy.ui.theme.subject.SubjectScreen
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var isBound by mutableStateOf(false)

    private lateinit var timerService: StudySessionTimerService

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {

            val binder = p1 as StudySessionTimerService.StudySessionTimerBinder
            timerService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, StudySessionTimerService::class.java).also { intent ->
            bindService(intent, connection, BIND_AUTO_CREATE)

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {

            if (isBound) {

                StudyBuddyTheme {

                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        dependenciesContainerBuilder = {
                            dependency(SessionScreenRouteDestination){
                                timerService
                            }

                        }
//                        dependenciesContainerBuilder = {
//                            dependency(SessionScreenRouteDestination) {
//                                timerService
//                            }
//                        }


                    )


                }
            }
        }
        requestPermissions()

    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
    }

    private fun requestPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }


    }

}


