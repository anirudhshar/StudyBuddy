package com.example.studybuddy.util

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

enum class Priority(val title:String, val color: Color,val value:Int){
    LOW("Low",Color.Green,1),
    MEDIUM("Medium",Color.Yellow,2),
    HIGH("High",Color.Red,3);

    companion object{
        fun fromInt(value: Int)= values().firstOrNull{
            it.value == value
        } ?: MEDIUM
    }



}

fun Long?.changeMillisToDateString():String{

    val date: LocalDate = this?.let{

        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    } ?:LocalDate.now()
    return date.format(java.time.format.DateTimeFormatter.ofPattern("dd MM yyyy"))
}

fun Long.toHours():Float{

    val hours = this.toFloat()/3600f
    return "%.2f".format(hours).toFloat()

}

fun Int.pad():String{
    return this.toString().padStart(2,'0')
}

sealed class SnackBarEvent{

    data class ShowSnackbar(
        val message:String,
        val duration: SnackbarDuration = SnackbarDuration.Short
    ) : SnackBarEvent()

    data object NavigateUp : SnackBarEvent()
}