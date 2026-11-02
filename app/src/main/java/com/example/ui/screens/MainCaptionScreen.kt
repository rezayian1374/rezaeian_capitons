package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AiSettingsDialog
import com.example.ui.components.CaptionTimelineEditor
import com.example.ui.components.ExportShareDialog
import com.example.ui.components.ProjectsDialog
import com.example.ui.components.StyleCustomizerSheet
import com.example.ui.components.VideoPlayerWithCaptions
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.PersianCyan
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianPurpleDark
import com.example.ui.theme.PersianPurplePrimary
import com.example.ui.theme.VazirmatnFontFamily
import com.example.ui.viewmodel.CaptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCaptionScreen(
  viewModel: CaptionViewModel,
  modifier: Modifier = Modifier
) {
  val isPersianUi by viewModel.isPersianUi.collectAsState()
  val videoUri by viewModel.videoUri.collectAsState()
  val videoTitle by viewModel.videoTitle.collectAsState()
  val isPlaying by viewModel.isPlaying.collectAsState()
  val currentPositionMs by viewModel.currentPositionMs.collectAsState()
  val durationMs by viewModel.durationMs.collectAsState()
  val playbackSpeed by viewModel.playbackSpeed.collectAsState()
  val subtitles by viewModel.subtitles.collectAsState()
  val captionStyle by viewModel.captionStyle.collectAsState()
  val isGenerating by viewModel.isGenerating.collectAsState()
  val generationMessage by viewModel.generationMessage.collectAsState()
  val customApiKey by viewModel.customApiKey.collectAsState()
  val savedProjects by viewModel.savedProjects.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Timeline, 1: Style
  var showExportDialog by remember { mutableStateOf(false) }
  var showProjectsDialog by remember { mutableStateOf(false) }
  var showSettingsDialog by remember { mutableStateOf(false) }

  // Video Picker from Gallery
  val videoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      viewModel.setVideoUri(uri, if (isPersianUi) "ویدیوی انتخابی از گالری" else "Gallery Video")
    }
  }

  val layoutDirection = if (isPersianUi) LayoutDirection.Rtl else LayoutDirection.Ltr

  CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(
                    Brush.linearGradient(listOf(PersianPurplePrimary, PersianCyan))
                  ),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "ک",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = if (isPersianUi) "کپشن‌نویس ویدیوی فارسی" else "Persian Video Captions",
                  fontFamily = VazirmatnFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp
                )
                Text(
                  text = videoTitle,
                  fontFamily = VazirmatnFontFamily,
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1
                )
              }
            }
          },
          actions = {
            // Language Switch
            IconButton(
              onClick = { viewModel.toggleUiLanguage() },
              modifier = Modifier.testTag("toggle_language_button")
            ) {
              Icon(
                Icons.Default.Language,
                contentDescription = "Language",
                tint = PersianCyan
              )
            }

            // AI Settings
            IconButton(
              onClick = { showSettingsDialog = true },
              modifier = Modifier.testTag("ai_settings_button")
            ) {
              Icon(
                Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurface
              )
            }

            // Projects
            IconButton(
              onClick = { showProjectsDialog = true },
              modifier = Modifier.testTag("projects_button")
            ) {
              Icon(
                Icons.Default.Bookmark,
                contentDescription = "Projects",
                tint = PersianGold
              )
            }

            // Export / Share
            IconButton(
              onClick = { showExportDialog = true },
              modifier = Modifier.testTag("export_button")
            ) {
              Icon(
                Icons.Default.Share,
                contentDescription = "Export",
                tint = PersianPurplePrimary
              )
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkSurfaceCard
          )
        )
      },
      containerColor = DarkSurface,
      modifier = modifier.fillMaxSize()
    ) { innerPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(horizontal = 12.dp, vertical = 6.dp)
          .widthIn(max = 700.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // 1. Video Player Container
        VideoPlayerWithCaptions(
          videoUri = videoUri,
          isPlaying = isPlaying,
          currentPositionMs = currentPositionMs,
          durationMs = durationMs,
          playbackSpeed = playbackSpeed,
          subtitles = subtitles,
          captionStyle = captionStyle,
          isPersianUi = isPersianUi,
          onPlaybackStateChanged = { playing, pos, dur ->
            viewModel.updatePlaybackState(playing, pos, dur)
          },
          onSeekTo = { pos ->
            viewModel.updatePlaybackState(isPlaying, pos, null)
          }
        )

        // 2. Video Selector Bar: Pick from device or switch sample videos
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Pick Custom Video
            OutlinedButton(
              onClick = {
                videoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                )
              },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("pick_video_button")
            ) {
              Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isPersianUi) "انتخاب ویدیو" else "Pick Video",
                fontFamily = VazirmatnFontFamily,
                fontSize = 12.sp
              )
            }

            // Quick Samples
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              FilledTonalButton(
                onClick = { viewModel.loadSampleVideo(1) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("sample_video_1_button")
              ) {
                Text(
                  text = if (isPersianUi) "نمونه ۱ (تکنولوژی)" else "Sample 1",
                  fontFamily = VazirmatnFontFamily,
                  fontSize = 11.sp
                )
              }

              FilledTonalButton(
                onClick = { viewModel.loadSampleVideo(2) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("sample_video_2_button")
              ) {
                Text(
                  text = if (isPersianUi) "نمونه ۲ (پادکست)" else "Sample 2",
                  fontFamily = VazirmatnFontFamily,
                  fontSize = 11.sp
                )
              }
            }
          }
        }

        // 3. AI Automatic Caption Generation Trigger Button
        Button(
          onClick = { viewModel.generateCaptions() },
          enabled = !isGenerating,
          colors = ButtonDefaults.buttonColors(
            containerColor = PersianPurplePrimary,
            disabledContainerColor = PersianPurpleDark
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("ai_generate_captions_button")
        ) {
          if (isGenerating) {
            CircularProgressIndicator(
              color = Color.White,
              modifier = Modifier.size(20.dp),
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isPersianUi) "در حال استخراج گفتار و ایجاد زیرنویس..." else "Generating Persian Captions...",
              fontFamily = VazirmatnFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          } else {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PersianGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isPersianUi) "تولید خودکار زیرنویس فارسی با هوش مصنوعی" else "Auto-Generate Persian Subtitles (AI)",
              fontFamily = VazirmatnFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }
        }

        // Status or Error Message Banner
        val currentMsg = generationMessage
        AnimatedVisibility(
          visible = currentMsg != null,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          if (currentMsg != null) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = PersianPurpleDark,
              border = androidx.compose.foundation.BorderStroke(1.dp, PersianCyan.copy(alpha = 0.5f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = currentMsg,
                fontFamily = VazirmatnFontFamily,
                fontSize = 12.sp,
                color = PersianCyan,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }

        // 4. Tab Selector (Timeline vs Style)
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = DarkSurfaceCard,
          contentColor = PersianPurplePrimary,
          modifier = Modifier.clip(RoundedCornerShape(10.dp))
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Subtitles, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (isPersianUi) "ویرایشگر زیرنویس (${subtitles.size})" else "Timeline (${subtitles.size})",
                  fontFamily = VazirmatnFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }
            },
            modifier = Modifier.testTag("tab_timeline")
          )

          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (isPersianUi) "شخصی‌سازی استایل" else "Style & Design",
                  fontFamily = VazirmatnFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }
            },
            modifier = Modifier.testTag("tab_style")
          )
        }

        // 5. Active Tab View
        Box(modifier = Modifier.weight(1f)) {
          when (selectedTab) {
            0 -> CaptionTimelineEditor(
              subtitles = subtitles,
              currentPositionMs = currentPositionMs,
              isPersianUi = isPersianUi,
              usePersianDigits = captionStyle.isPersianNumerals,
              onSeekTo = { pos -> viewModel.updatePlaybackState(isPlaying, pos, null) },
              onUpdateSubtitle = { id, text, start, end -> viewModel.updateSubtitle(id, text, start, end) },
              onAddSubtitle = { start, end, text -> viewModel.addSubtitle(start, end, text) },
              onDeleteSubtitle = { id -> viewModel.deleteSubtitle(id) },
              onShiftAll = { offset -> viewModel.shiftAllSubtitles(offset) }
            )

            1 -> StyleCustomizerSheet(
              style = captionStyle,
              isPersianUi = isPersianUi,
              onUpdateStyle = { fontSize, textColor, highlightColor, bgColor, pos, effect, persianNums ->
                viewModel.updateStyle(fontSize, textColor, highlightColor, bgColor, pos, effect, persianNums)
              }
            )
          }
        }
      }
    }

    // Export & Share Dialog
    if (showExportDialog) {
      ExportShareDialog(
        subtitles = subtitles,
        usePersianDigits = captionStyle.isPersianNumerals,
        isPersianUi = isPersianUi,
        onDismiss = { showExportDialog = false }
      )
    }

    // Projects Dialog
    if (showProjectsDialog) {
      ProjectsDialog(
        projects = savedProjects,
        currentVideoTitle = videoTitle,
        isPersianUi = isPersianUi,
        onSaveCurrent = { name -> viewModel.saveProject(name) },
        onLoadProject = { proj -> viewModel.loadProject(proj) },
        onDeleteProject = { proj -> viewModel.deleteProject(proj) },
        onDismiss = { showProjectsDialog = false }
      )
    }

    // AI & API Key Settings Dialog
    if (showSettingsDialog) {
      AiSettingsDialog(
        currentApiKey = customApiKey,
        isPersianUi = isPersianUi,
        onSaveApiKey = { key -> viewModel.setApiKey(key) },
        onDismiss = { showSettingsDialog = false }
      )
    }
  }
}
