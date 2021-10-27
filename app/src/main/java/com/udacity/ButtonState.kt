package com.udacity

// Describes the state of the custom button
sealed class ButtonState {
    object Clicked : ButtonState()      // Button is clicked
    object Loading : ButtonState()      // Downloading is in progress
    object Completed : ButtonState()    // Downloading is finished
}