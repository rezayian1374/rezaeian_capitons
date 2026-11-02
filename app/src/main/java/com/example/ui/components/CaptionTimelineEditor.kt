package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.generator.PersianCaptionEngine
import com.example.data.model.SubtitleItem
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCardElevated
import com.example.ui.theme.PersianCyan
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianPurplePrimary
import com.example.ui.theme.VazirmatnFontFamily

@Composable
fun CaptionTimelineEditor(
  subtitles: List<SubtitleItem>,
  currentPositionMs: Long,
  isPersianUi: Boolean,
  usePersianDigits: Boolean,
  onSeekTo: (positionMs: Long) -> Unit,
  onUpdateSubtitle: (id: String, text: String, startMs: Long, endMs: Long) -> Unit,
  onAddSubtitle: (startMs: Long, endMs: Long, text: String) -> Unit,
  onDeleteSubtitle: (id: String) -> Unit,
  onShiftAll: (offsetMs: Long) -> Unit,
  modifier: Modifier = Modifier
) {
  var editingItem by remember { mutableStateOf<SubtitleItem?>(null) }
  var showAddDialog by remember { mutableStateOf(false) }

  Column(modifier = modifier.fillMaxWidth()) {
    // Actions Header: Add caption, Time-shift buttons
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Button(
        onClick = { showAddDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = PersianPurplePrimary),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("add_caption_button")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isPersianUi) "افزودن کپشن" else "Add Caption",
          fontFamily = VazirmatnFontFamily,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        FilledTonalButton(
          onClick = { onShiftAll(-300L) },
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("shift_minus_button")
        ) {
          Text(
            text = if (isPersianUi) "-۰.۳ ثانیه" else "-0.3s",
            fontFamily = VazirmatnFontFamily,
            fontSize = 11.sp
          )
        }

        FilledTonalButton(
          onClick = { onShiftAll(300L) },
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("shift_plus_button")
        ) {
          Text(
            text = if (isPersianUi) "+۰.۳ ثانیه" else "+0.3s",
            fontFamily = VazirmatnFontFamily,
            fontSize = 11.sp
          )
        }
      }
    }

    if (subtitles.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = if (isPersianUi) "هنوز زیرنویسی ایجاد نشده است. روی دکمه هوش مصنوعی یا افزودن کلیک کنید."
          else "No subtitles yet. Click Generate with AI or Add Caption.",
          fontFamily = VazirmatnFontFamily,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        itemsIndexed(subtitles, key = { _, item -> item.id }) { index, item ->
          val isActive = currentPositionMs in item.startTimeMs..item.endTimeMs

          val cardBorderColor by animateColorAsState(
            targetValue = if (isActive) PersianPurplePrimary else DarkSurfaceBorder,
            label = "border_color"
          )

          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isActive) DarkSurfaceCardElevated else MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(if (isActive) 1.5.dp else 1.dp, cardBorderColor),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onSeekTo(item.startTimeMs) }
              .testTag("caption_item_$index")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Left: Index & Play to seek
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(44.dp)
              ) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = if (isActive) PersianPurplePrimary else MaterialTheme.colorScheme.surfaceVariant
                ) {
                  val indexStr = (index + 1).toString()
                  Text(
                    text = if (usePersianDigits) PersianCaptionEngine.toPersianDigits(indexStr) else indexStr,
                    fontFamily = VazirmatnFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.width(8.dp))

              // Middle: Text & Timing
              Column(modifier = Modifier.weight(1f)) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                  Text(
                    text = if (usePersianDigits) PersianCaptionEngine.toPersianDigits(item.text) else item.text,
                    fontFamily = VazirmatnFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isActive) PersianGold else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                  )
                }

                Spacer(modifier = Modifier.height(4.dp))

                val startFormatted = PersianCaptionEngine.formatTimeWithMillis(item.startTimeMs, isSrt = false)
                val endFormatted = PersianCaptionEngine.formatTimeWithMillis(item.endTimeMs, isSrt = false)
                val timingStr = "$startFormatted ➔ $endFormatted"

                Text(
                  text = if (usePersianDigits) PersianCaptionEngine.toPersianDigits(timingStr) else timingStr,
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              // Right: Action buttons (Edit & Delete)
              Row {
                IconButton(
                  onClick = { editingItem = item },
                  modifier = Modifier
                    .size(36.dp)
                    .testTag("edit_caption_$index")
                ) {
                  Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = PersianCyan,
                    modifier = Modifier.size(18.dp)
                  )
                }

                IconButton(
                  onClick = { onDeleteSubtitle(item.id) },
                  modifier = Modifier
                    .size(36.dp)
                    .testTag("delete_caption_$index")
                ) {
                  Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  // Edit Subtitle Dialog
  editingItem?.let { item ->
    var editText by remember { mutableStateOf(item.text) }
    var editStart by remember { mutableLongStateOf(item.startTimeMs) }
    var editEnd by remember { mutableLongStateOf(item.endTimeMs) }

    AlertDialog(
      onDismissRequest = { editingItem = null },
      title = {
        Text(
          text = if (isPersianUi) "ویرایش زیرنویس فارسی" else "Edit Persian Subtitle",
          fontFamily = VazirmatnFontFamily,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            OutlinedTextField(
              value = editText,
              onValueChange = { editText = it },
              label = { Text(if (isPersianUi) "متن زیرنویس" else "Subtitle Text", fontFamily = VazirmatnFontFamily) },
              modifier = Modifier.fillMaxWidth()
            )
          }

          // Timing adjustments
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (isPersianUi) "شروع: ${PersianCaptionEngine.formatTimeWithMillis(editStart)}"
                else "Start: ${PersianCaptionEngine.formatTimeWithMillis(editStart)}",
                fontSize = 12.sp
              )
              Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(onClick = { editStart = (editStart - 200).coerceAtLeast(0) }) {
                  Text("-۰.۲")
                }
                OutlinedButton(onClick = { editStart += 200 }) {
                  Text("+۰.۲")
                }
              }
            }

            Column {
              Text(
                text = if (isPersianUi) "پایان: ${PersianCaptionEngine.formatTimeWithMillis(editEnd)}"
                else "End: ${PersianCaptionEngine.formatTimeWithMillis(editEnd)}",
                fontSize = 12.sp
              )
              Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(onClick = { editEnd = (editEnd - 200).coerceAtLeast(editStart + 300) }) {
                  Text("-۰.۲")
                }
                OutlinedButton(onClick = { editEnd += 200 }) {
                  Text("+۰.۲")
                }
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateSubtitle(item.id, editText, editStart, editEnd)
            editingItem = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = PersianPurplePrimary)
        ) {
          Text(text = if (isPersianUi) "ذخیره" else "Save", fontFamily = VazirmatnFontFamily)
        }
      },
      dismissButton = {
        TextButton(onClick = { editingItem = null }) {
          Text(text = if (isPersianUi) "انصراف" else "Cancel", fontFamily = VazirmatnFontFamily)
        }
      }
    )
  }

  // Add New Subtitle Dialog
  if (showAddDialog) {
    var newText by remember { mutableStateOf("") }
    var newStart by remember { mutableLongStateOf(currentPositionMs) }
    var newEnd by remember { mutableLongStateOf(currentPositionMs + 3000L) }

    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      title = {
        Text(
          text = if (isPersianUi) "افزودن زیرنویس جدید" else "Add New Subtitle",
          fontFamily = VazirmatnFontFamily,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            OutlinedTextField(
              value = newText,
              onValueChange = { newText = it },
              label = { Text(if (isPersianUi) "متن زیرنویس فارسی" else "Persian Text", fontFamily = VazirmatnFontFamily) },
              modifier = Modifier.fillMaxWidth()
            )
          }

          Text(
            text = if (isPersianUi)
              "زمان: ${PersianCaptionEngine.formatTime(newStart)} تا ${PersianCaptionEngine.formatTime(newEnd)}"
            else "Time: ${PersianCaptionEngine.formatTime(newStart)} to ${PersianCaptionEngine.formatTime(newEnd)}",
            fontFamily = VazirmatnFontFamily,
            fontSize = 12.sp
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newText.isNotBlank()) {
              onAddSubtitle(newStart, newEnd, newText)
            }
            showAddDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = PersianPurplePrimary)
        ) {
          Text(text = if (isPersianUi) "افزودن" else "Add", fontFamily = VazirmatnFontFamily)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDialog = false }) {
          Text(text = if (isPersianUi) "انصراف" else "Cancel", fontFamily = VazirmatnFontFamily)
        }
      }
    )
  }
}
