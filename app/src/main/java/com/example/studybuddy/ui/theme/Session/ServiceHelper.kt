package com.example.studybuddy.ui.theme.Session

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.studybuddy.MainActivity
import com.example.studybuddy.util.Constants
import com.example.studybuddy.util.Constants.CLICK_REQUEST_CODE

object ServiceHelper {

    fun clickPendingIntent(context: Context): PendingIntent {
        val deepLinkIntent =
            Intent(     // this function and deepLinkIntent is used to navigate from clicking the notification to a screen
                Intent.ACTION_VIEW,
                "studybuddy//dashboard/Session".toUri(),
                context,
                MainActivity::class.java
            )
        return TaskStackBuilder.create(context).run {   //used to maintain heirarchy so that back navigation can be used
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(
                CLICK_REQUEST_CODE,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

    }

    fun triggerForegroundService(context: Context, action: String) {

        Intent(
            context,
            StudySessionTimerService::class.java

        ).apply {
            this.action = action
            context.startService(this)
        }

    }
}


