package com.example.ui.components

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.generator.PersianCaptionEngine
import com.example.data.local.CaptionProjectEntity
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.PersianCyan
import com.example.ui.theme.PersianPurplePrimary
import com.example.ui.theme.VazirmatnFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectsDialog(
  projects: List<CaptionProjectEntity>,
  currentVideoTitle: String,
  isPersianUi: Boolean,
  onSaveCurrent: (projectName: String) -> Unit,
  onLoadProject: (project: CaptionProjectEntity) -> Unit,
  onDeleteProject: (project: CaptionProjectEntity) -> Unit,
  onDismiss: () -> Unit
) {
  var projectNameInput by remember { mutableStateOf(currentVideoTitle) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Bookmark, contentDescription = null, tint = PersianCyan)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isPersianUi) "پروژه‌ها و تاریخچه زیرنویس" else "Subtitle Projects & History",
          fontFamily = VazirmatnFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp
        )
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Save current project input
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = if (isPersianUi) "ذخیره پروژه فعلی:" else "Save Current Project:",
              fontFamily = VazirmatnFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = projectNameInput,
              onValueChange = { projectNameInput = it },
              label = { Text(if (isPersianUi) "نام پروژه" else "Project Name", fontFamily = VazirmatnFontFamily) },
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Button(
              onClick = {
                onSaveCurrent(projectNameInput)
              },
              colors = ButtonDefaults.buttonColors(containerColor = PersianPurplePrimary),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .align(Alignment.End)
                .testTag("confirm_save_project_button")
            ) {
              Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(if (isPersianUi) "ذخیره پروژه" else "Save Project", fontFamily = VazirmatnFontFamily)
            }
          }
        }

        Text(
          text = if (isPersianUi) "پروژه‌های قبلی (${projects.size}):" else "Saved Projects (${projects.size}):",
          fontFamily = VazirmatnFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )

        if (projects.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (isPersianUi) "هنوز پروژه‌ای ذخیره نشده است." else "No saved projects yet.",
              fontFamily = VazirmatnFontFamily,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(max = 220.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(projects, key = { it.id }) { proj ->
              Card(
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    onLoadProject(proj)
                    onDismiss()
                  }
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = proj.title,
                      fontFamily = VazirmatnFontFamily,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp
                    )
                    val dateFormatted = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(proj.createdAt))
                    Text(
                      text = "${PersianCaptionEngine.formatTime(proj.durationMs)} • $dateFormatted",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }

                  IconButton(
                    onClick = { onDeleteProject(proj) },
                    modifier = Modifier.size(32.dp)
                  ) {
                    Icon(
                      Icons.Default.Delete,
                      contentDescription = "Delete",
                      tint = MaterialTheme.colorScheme.error,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isPersianUi) "بستن" else "Close", fontFamily = VazirmatnFontFamily)
      }
    }
  )
}
