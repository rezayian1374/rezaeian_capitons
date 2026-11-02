package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.ui.theme.PersianCyan
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianPurplePrimary
import com.example.ui.theme.VazirmatnFontFamily

@Composable
fun AiSettingsDialog(
  currentApiKey: String,
  isPersianUi: Boolean,
  onSaveApiKey: (key: String) -> Unit,
  onDismiss: () -> Unit
) {
  var apiKeyInput by remember { mutableStateOf(currentApiKey) }
  val buildKeyPresent = remember {
    runCatching { BuildConfig.GEMINI_API_KEY }.getOrNull()?.let {
      it.isNotBlank() && it != "MY_GEMINI_API_KEY"
    } == true
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PersianGold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isPersianUi) "تنظیمات هوش مصنوعی و کلید API" else "AI & Gemini API Settings",
          fontFamily = VazirmatnFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp
        )
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = if (isPersianUi)
                "این اپلیکیشن از مدل‌های پیشرفته جمینای برای تبدیل گفتار به زیرنویس فارسی با زمان‌بندی دقیق استفاده می‌کند. در صورت عدم ثبت کلید، موتور هوشمند محلی فعال خواهد بود."
              else
                "This app uses advanced Gemini AI models to transcribe speech to Persian subtitles with precise timestamps. If no key is set, the smart offline acoustic engine is active.",
              fontFamily = VazirmatnFontFamily,
              fontSize = 12.sp,
              lineHeight = 18.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (buildKeyPresent) {
              Spacer(modifier = Modifier.height(8.dp))
              Surface(
                color = PersianCyan.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = if (isPersianUi) "✓ کلید اختصاصی AI Studio فعال است" else "✓ AI Studio API Key Active",
                  fontFamily = VazirmatnFontFamily,
                  color = PersianCyan,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }

        OutlinedTextField(
          value = apiKeyInput,
          onValueChange = { apiKeyInput = it },
          label = { Text(if (isPersianUi) "کلید Gemini API (اختیاری)" else "Gemini API Key (Optional)", fontFamily = VazirmatnFontFamily) },
          leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = PersianPurplePrimary) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("api_key_input")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSaveApiKey(apiKeyInput)
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = PersianPurplePrimary),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("save_api_key_button")
      ) {
        Text(if (isPersianUi) "تأیید و ذخیره" else "Save Key", fontFamily = VazirmatnFontFamily)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isPersianUi) "بستن" else "Close", fontFamily = VazirmatnFontFamily)
      }
    }
  )
}
