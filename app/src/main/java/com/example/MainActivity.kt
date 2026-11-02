package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.screens.MainCaptionScreen
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.PersianCaptionsTheme
import com.example.ui.viewmodel.CaptionViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: CaptionViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      PersianCaptionsTheme(darkTheme = true) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = DarkSurface
        ) {
          MainCaptionScreen(viewModel = viewModel)
        }
      }
    }
  }
}
