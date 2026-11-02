package com.example.ui.components

import android.media.MediaPlayer
import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.generator.PersianCaptionEngine
import com.example.data.model.CaptionStyle
import com.example.data.model.SubtitleItem
import com.example.ui.theme.PersianCyan
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianPurplePrimary
import com.example.ui.theme.VazirmatnFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun VideoPlayerWithCaptions(
  videoUri: Uri,
  isPlaying: Boolean,
  currentPositionMs: Long,
  durationMs: Long,
  playbackSpeed: Float,
  subtitles: List<SubtitleItem>,
  captionStyle: CaptionStyle,
  isPersianUi: Boolean,
  onPlaybackStateChanged: (isPlaying: Boolean, positionMs: Long, durationMs: Long?) -> Unit,
  onSeekTo: (positionMs: Long) -> Unit,
  modifier: Modifier = Modifier
) {
  var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
  var isVideoReady by remember { mutableStateOf(false) }
  var showControls by remember { mutableStateOf(true) }
  var isScrubbing by remember { mutableStateOf(false) }
  var scrubPositionMs by remember { mutableLongStateOf(0L) }

  // Find active subtitle
  val effectivePosition = if (isScrubbing) scrubPositionMs else currentPositionMs
  val activeSubtitle = remember(subtitles, effectivePosition) {
    subtitles.firstOrNull { it.containsTime(effectivePosition) }
  }

  // Periodic position poller when video is playing
  LaunchedEffect(isPlaying, isVideoReady) {
    while (isActive && isPlaying && isVideoReady) {
      videoViewRef?.let { vv ->
        if (vv.isPlaying) {
          val pos = vv.currentPosition.toLong()
          val dur = vv.duration.toLong().takeIf { it > 0 }
          onPlaybackStateChanged(true, pos, dur)
        }
      }
      delay(50L) // 20 updates per second for smooth captions and scrubbers
    }
  }

  // Handle speed changes if supported on SDK
  LaunchedEffect(playbackSpeed, videoViewRef) {
    videoViewRef?.let { vv ->
      try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
          // VideoView doesn't directly expose MediaPlayer, but when prepared, reflection or MediaPlayer.playbackParams
        }
      } catch (_: Exception) {}
    }
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(16f / 9f)
      .clip(RoundedCornerShape(16.dp))
      .background(Color.Black)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {
        showControls = !showControls
      }
      .testTag("video_player_container")
  ) {
    // Android VideoView
    AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = { ctx ->
        VideoView(ctx).apply {
          setOnPreparedListener { mp ->
            isVideoReady = true
            val dur = mp.duration.toLong().coerceAtLeast(1000L)
            onPlaybackStateChanged(false, 0L, dur)
            mp.isLooping = false
            mp.setOnCompletionListener {
              onPlaybackStateChanged(false, dur, dur)
            }
          }
          setOnErrorListener { _, what, extra ->
            isVideoReady = false
            true
          }
          setVideoURI(videoUri)
          videoViewRef = this
        }
      },
      update = { vv ->
        videoViewRef = vv
      }
    )

    // Re-bind URI when it changes
    LaunchedEffect(videoUri) {
      isVideoReady = false
      videoViewRef?.apply {
        setVideoURI(videoUri)
        seekTo(0)
      }
    }

    // Persian Subtitles Overlay
    PersianCaptionOverlay(
      activeSubtitle = activeSubtitle,
      currentPositionMs = effectivePosition,
      style = captionStyle,
      modifier = Modifier.fillMaxSize()
    )

    // Loading indicator when video is not ready
    if (!isVideoReady) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator(
          color = PersianPurplePrimary,
          modifier = Modifier.size(48.dp)
        )
      }
    }

    // Top Header Gradient Overlay with title and status
    AnimatedVisibility(
      visible = showControls,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.align(Alignment.TopCenter)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
            )
          )
          .padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = PersianPurplePrimary.copy(alpha = 0.25f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(
              text = if (isPersianUi) "زیرنویس خودکار فارسی" else "Persian Auto-Caption",
              fontFamily = VazirmatnFontFamily,
              color = PersianCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }

          Text(
            text = PersianCaptionEngine.formatTime(effectivePosition, captionStyle.isPersianNumerals) + " / " +
              PersianCaptionEngine.formatTime(durationMs, captionStyle.isPersianNumerals),
            color = Color.White,
            fontFamily = VazirmatnFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Center Big Play/Pause Button
    AnimatedVisibility(
      visible = showControls && isVideoReady,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.align(Alignment.Center)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
      ) {
        // Rewind 5s
        IconButton(
          onClick = {
            val newPos = (currentPositionMs - 5000L).coerceAtLeast(0L)
            videoViewRef?.seekTo(newPos.toInt())
            onSeekTo(newPos)
          },
          modifier = Modifier
            .size(44.dp)
            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            .testTag("rewind_5s_button")
        ) {
          Icon(
            imageVector = Icons.Default.FastRewind,
            contentDescription = if (isPersianUi) "۵ ثانیه عقب" else "Rewind 5s",
            tint = Color.White
          )
        }

        // Play / Pause
        IconButton(
          onClick = {
            videoViewRef?.let { vv ->
              if (vv.isPlaying) {
                vv.pause()
                onPlaybackStateChanged(false, vv.currentPosition.toLong(), null)
              } else {
                vv.start()
                onPlaybackStateChanged(true, vv.currentPosition.toLong(), null)
              }
            }
          },
          modifier = Modifier
            .size(60.dp)
            .background(PersianPurplePrimary, CircleShape)
            .testTag("play_pause_button")
        ) {
          Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = Color.White,
            modifier = Modifier.size(34.dp)
          )
        }

        // Forward 5s
        IconButton(
          onClick = {
            val newPos = (currentPositionMs + 5000L).coerceAtMost(durationMs)
            videoViewRef?.seekTo(newPos.toInt())
            onSeekTo(newPos)
          },
          modifier = Modifier
            .size(44.dp)
            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            .testTag("forward_5s_button")
        ) {
          Icon(
            imageVector = Icons.Default.FastForward,
            contentDescription = if (isPersianUi) "۵ ثانیه جلو" else "Forward 5s",
            tint = Color.White
          )
        }
      }
    }

    // Bottom Controls Scrubber
    AnimatedVisibility(
      visible = showControls,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.align(Alignment.BottomCenter)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
            )
          )
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        val sliderValue = if (durationMs > 0) {
          (effectivePosition.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f

        Slider(
          value = sliderValue,
          onValueChange = { frac ->
            isScrubbing = true
            scrubPositionMs = (frac * durationMs).toLong()
          },
          onValueChangeFinished = {
            isScrubbing = false
            videoViewRef?.seekTo(scrubPositionMs.toInt())
            onSeekTo(scrubPositionMs)
          },
          colors = SliderDefaults.colors(
            thumbColor = PersianCyan,
            activeTrackColor = PersianPurplePrimary,
            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .testTag("video_timeline_slider")
        )
      }
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      videoViewRef?.stopPlayback()
    }
  }
}
