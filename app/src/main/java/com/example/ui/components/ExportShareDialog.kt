package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.generator.PersianCaptionEngine
import com.example.data.model.SubtitleItem
import com.example.ui.theme.DarkSurfaceCardElevated
import com.example.ui.theme.PersianCyan
import com.example.ui.theme.PersianPurplePrimary
import com.example.ui.theme.VazirmatnFontFamily

@Composable
fun ExportShareDialog(
  subtitles: List<SubtitleItem>,
  usePersianDigits: Boolean,
  isPersianUi: Boolean,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableIntStateOf(0) } // 0: SRT, 1: VTT, 2: TXT

  val srtContent = remember(subtitles, usePersianDigits) {
    PersianCaptionEngine.exportToSrt(subtitles, usePersianDigits)
  }
  val vttContent = remember(subtitles) {
    PersianCaptionEngine.exportToVtt(subtitles)
  }
  val txtContent = remember(subtitles) {
    PersianCaptionEngine.exportToPlainText(subtitles)
  }

  val currentContent = when (selectedTab) {
    0 -> srtContent
    1 -> vttContent
    else -> txtContent
  }

  val formatName = when (selectedTab) {
    0 -> "SRT (SubRip)"
    1 -> "VTT (WebVTT)"
    else -> "TXT (متن)"
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (isPersianUi) "خروجی و اشتراک‌گذاری زیرنویس" else "Export & Share Subtitles",
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp
      )
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("SRT", fontWeight = FontWeight.Bold) }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("VTT", fontWeight = FontWeight.Bold) }
          )
          Tab(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            text = { Text(if (isPersianUi) "متن کامل" else "Transcript", fontFamily = VazirmatnFontFamily) }
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content Preview Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 240.dp)
            .background(DarkSurfaceCardElevated, RoundedCornerShape(8.dp))
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
        ) {
          CompositionLocalProvider(LocalLayoutDirection provides if (selectedTab == 2) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            Text(
              text = currentContent.ifBlank { if (isPersianUi) "محتوایی برای نمایش وجود ندارد" else "No content" },
              fontFamily = if (selectedTab == 2) VazirmatnFontFamily else FontFamily.Monospace,
              fontSize = 12.sp,
              color = Color.White,
              lineHeight = 18.sp
            )
          }
        }
      }
    },
    confirmButton = {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Copy to clipboard
        OutlinedButton(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Persian Subtitle", currentContent)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(
              context,
              if (isPersianUi) "متن $formatName در کلیپ‌بورد کپی شد" else "Copied $formatName to clipboard",
              Toast.LENGTH_SHORT
            ).show()
          },
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("copy_subtitle_button")
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(if (isPersianUi) "کپی" else "Copy", fontFamily = VazirmatnFontFamily)
        }

        // Share via Android Intent
        Button(
          onClick = {
            val sendIntent: Intent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(Intent.EXTRA_TEXT, currentContent)
              type = "text/plain"
            }
            val shareIntent = Intent.createChooser(
              sendIntent,
              if (isPersianUi) "ارسال زیرنویس فارسی" else "Share Persian Subtitle"
            )
            context.startActivity(shareIntent)
          },
          colors = ButtonDefaults.buttonColors(containerColor = PersianPurplePrimary),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("share_subtitle_button")
        ) {
          Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(if (isPersianUi) "اشتراک‌گذاری" else "Share", fontFamily = VazirmatnFontFamily)
        }
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isPersianUi) "بستن" else "Close", fontFamily = VazirmatnFontFamily)
      }
    }
  )
}
