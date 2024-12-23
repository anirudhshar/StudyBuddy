package com.example.studybuddy.ui.theme.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DeleteSubjectDialog(
    isOpen: Boolean,
    title: String,
    bodyText: String,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit

) {


    if(isOpen){
        AlertDialog(title = {
            Text(text = title)
        },
            text = {
                Text(text = bodyText)

            },
            onDismissRequest = onDismissRequest

            ,
            dismissButton = {
                TextButton(onClick =  onDismissRequest ) {
                    Text(text = "Cancel")


                }
            },
            confirmButton = {
                TextButton(onClick =  onConfirmButtonClick, ){
                    Text(text = "Delete")


                }
            })

    }

}