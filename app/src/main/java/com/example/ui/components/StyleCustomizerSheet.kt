package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CaptionEffect
import com.example.data.model.CaptionPosition
import com.example.data.model.CaptionStyle
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.PersianCyan
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianPurplePrimary
import com.example.ui.theme.VazirmatnFontFamily

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StyleCustomizerSheet(
  style: CaptionStyle,
  isPersianUi: Boolean,
  onUpdateStyle: (
    fontSizeSp: Float?,
    textColorHex: String?,
    highlightColorHex: String?,
    backgroundColorHex: String?,
    position: CaptionPosition?,
    effect: CaptionEffect?,
    isPersianNumerals: Boolean?
  ) -> Unit,
  modifier: Modifier = Modifier
) {
  val textColors = listOf("#FFFFFF", "#FACC15", "#38BDF8", "#4ADE80", "#FB7185", "#E2E8F0")
  val highlightColors = listOf("#FACC15", "#06B6D4", "#A855F7", "#22C55E", "#EC4899", "#F97316")

  Column(
    modifier = modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Caption Effect Style
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = if (isPersianUi) "سبک نمایش زیرنویس" else "Subtitle Style Effect",
          fontFamily = VazirmatnFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          CaptionEffect.entries.forEach { effect ->
            val selected = style.effect == effect
            FilterChip(
              selected = selected,
              onClick = { onUpdateStyle(null, null, null, null, null, effect, null) },
              label = {
                Text(
                  text = if (isPersianUi) effect.titleFa else effect.titleEn,
                  fontFamily = VazirmatnFontFamily,
                  fontSize = 12.sp
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PersianPurplePrimary.copy(alpha = 0.2f),
                selectedLabelColor = PersianCyan
              )
            )
          }
        }
      }
    }

    // 2. Position on screen
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = if (isPersianUi) "محل قرارگیری در تصویر" else "Screen Position",
          fontFamily = VazirmatnFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          CaptionPosition.entries.forEach { pos ->
            val selected = style.position == pos
            FilterChip(
              selected = selected,
              onClick = { onUpdateStyle(null, null, null, null, pos, null, null) },
              label = {
                Text(
                  text = if (isPersianUi) pos.titleFa else pos.titleEn,
                  fontFamily = VazirmatnFontFamily,
                  fontSize = 12.sp
                )
              },
              modifier = Modifier.weight(1f),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PersianPurplePrimary.copy(alpha = 0.2f),
                selectedLabelColor = PersianCyan
              )
            )
          }
        }
      }
    }

    // 3. Font Size Slider
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = if (isPersianUi) "اندازه قلم فارسی" else "Font Size",
            fontFamily = VazirmatnFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Text(
            text = "${style.fontSizeSp.toInt()} sp",
            fontWeight = FontWeight.Bold,
            color = PersianCyan
          )
        }
        Slider(
          value = style.fontSizeSp,
          onValueChange = { onUpdateStyle(it, null, null, null, null, null, null) },
          valueRange = 16f..34f,
          steps = 9,
          colors = SliderDefaults.colors(
            thumbColor = PersianCyan,
            activeTrackColor = PersianPurplePrimary
          ),
          modifier = Modifier.testTag("font_size_slider")
        )
      }
    }

    // 4. Color Palette Picker
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Text Color
        Text(
          text = if (isPersianUi) "رنگ اصلی متن" else "Text Color",
          fontFamily = VazirmatnFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          textColors.forEach { hex ->
            val color = Color(android.graphics.Color.parseColor(hex))
            val isSelected = style.textColorHex.equals(hex, ignoreCase = true)
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                  width = if (isSelected) 3.dp else 1.dp,
                  color = if (isSelected) PersianPurplePrimary else Color.Gray,
                  shape = CircleShape
                )
                .clickable { onUpdateStyle(null, hex, null, null, null, null, null) }
            )
          }
        }

        // Highlight Word Color
        Text(
          text = if (isPersianUi) "رنگ هایلایت کلمه فعال (کارائوکه)" else "Active Word Highlight Color",
          fontFamily = VazirmatnFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          highlightColors.forEach { hex ->
            val color = Color(android.graphics.Color.parseColor(hex))
            val isSelected = style.highlightColorHex.equals(hex, ignoreCase = true)
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                  width = if (isSelected) 3.dp else 1.dp,
                  color = if (isSelected) Color.White else Color.Gray,
                  shape = CircleShape
                )
                .clickable { onUpdateStyle(null, null, hex, null, null, null, null) }
            )
          }
        }
      }
    }

    // 5. Persian Numerals Option
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (isPersianUi) "نمایش اعداد به فارسی (۰ تا ۹)" else "Persian Numerals (۰ to ۹)",
            fontFamily = VazirmatnFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Text(
            text = if (isPersianUi) "تبدیل خودکار ارقام و زمان‌بندی به فارسی" else "Format numbers and time in Persian script",
            fontFamily = VazirmatnFontFamily,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Switch(
          checked = style.isPersianNumerals,
          onCheckedChange = { onUpdateStyle(null, null, null, null, null, null, it) },
          colors = SwitchDefaults.colors(checkedThumbColor = PersianCyan, checkedTrackColor = PersianPurplePrimary),
          modifier = Modifier.testTag("persian_numerals_switch")
        )
      }
    }
  }
}
