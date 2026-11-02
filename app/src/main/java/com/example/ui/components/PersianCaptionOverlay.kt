package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.generator.PersianCaptionEngine
import com.example.data.model.CaptionEffect
import com.example.data.model.CaptionPosition
import com.example.data.model.CaptionStyle
import com.example.data.model.SubtitleItem
import com.example.ui.theme.VazirmatnFontFamily

@Composable
fun BoxScope.PersianCaptionOverlay(
  activeSubtitle: SubtitleItem?,
  currentPositionMs: Long,
  style: CaptionStyle,
  modifier: Modifier = Modifier
) {
  val alignment = when (style.position) {
    CaptionPosition.TOP -> Alignment.TopCenter
    CaptionPosition.CENTER -> Alignment.Center
    CaptionPosition.BOTTOM -> Alignment.BottomCenter
  }

  val textColor = remember(style.textColorHex) {
    runCatching { Color(android.graphics.Color.parseColor(style.textColorHex)) }.getOrDefault(Color.White)
  }

  val highlightColor = remember(style.highlightColorHex) {
    runCatching { Color(android.graphics.Color.parseColor(style.highlightColorHex)) }.getOrDefault(Color(0xFFFACC15))
  }

  val backgroundColor = remember(style.backgroundColorHex) {
    runCatching { Color(android.graphics.Color.parseColor(style.backgroundColorHex)) }.getOrDefault(Color(0xB3000000))
  }

  AnimatedVisibility(
    visible = activeSubtitle != null,
    enter = fadeIn(),
    exit = fadeOut(),
    modifier = modifier
      .align(alignment)
      .padding(horizontal = 16.dp, vertical = 20.dp)
      .testTag("caption_overlay")
  ) {
    if (activeSubtitle == null) return@AnimatedVisibility

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
      val rawText = if (style.isPersianNumerals) {
        PersianCaptionEngine.toPersianDigits(activeSubtitle.text)
      } else {
        activeSubtitle.text
      }

      val annotatedText = remember(rawText, currentPositionMs, style.effect) {
        if (style.effect == CaptionEffect.REEL_HIGHLIGHT && activeSubtitle.words.isNotEmpty()) {
          buildAnnotatedString {
            activeSubtitle.words.forEachIndexed { index, timedWord ->
              val isSpoken = currentPositionMs in timedWord.startMs..timedWord.endMs
              val displayWord = if (style.isPersianNumerals) {
                PersianCaptionEngine.toPersianDigits(timedWord.word)
              } else {
                timedWord.word
              }

              if (isSpoken) {
                withStyle(
                  SpanStyle(
                    color = highlightColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (style.fontSizeSp + 2).sp
                  )
                ) {
                  append(displayWord)
                }
              } else {
                withStyle(
                  SpanStyle(
                    color = textColor,
                    fontWeight = if (style.bold) FontWeight.Bold else FontWeight.Normal
                  )
                ) {
                  append(displayWord)
                }
              }

              if (index < activeSubtitle.words.lastIndex) {
                append(" ")
              }
            }
          }
        } else {
          buildAnnotatedString {
            withStyle(
              SpanStyle(
                color = textColor,
                fontWeight = if (style.bold) FontWeight.Bold else FontWeight.Normal
              )
            ) {
              append(rawText)
            }
          }
        }
      }

      val boxModifier = when (style.effect) {
        CaptionEffect.CLASSIC_BOX -> Modifier
          .background(backgroundColor, RoundedCornerShape(8.dp))
          .padding(horizontal = 14.dp, vertical = 8.dp)

        CaptionEffect.REEL_HIGHLIGHT -> Modifier
          .background(backgroundColor, RoundedCornerShape(12.dp))
          .padding(horizontal = 16.dp, vertical = 8.dp)

        CaptionEffect.NEON_GLOW -> Modifier
          .shadow(8.dp, RoundedCornerShape(10.dp), spotColor = highlightColor)
          .background(Color(0xE60F0E26), RoundedCornerShape(10.dp))
          .border(1.5.dp, highlightColor, RoundedCornerShape(10.dp))
          .padding(horizontal = 14.dp, vertical = 8.dp)

        CaptionEffect.MINIMAL_SHADOW -> Modifier
          .padding(horizontal = 8.dp, vertical = 4.dp)
      }

      Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = annotatedText,
          fontFamily = VazirmatnFontFamily,
          fontSize = style.fontSizeSp.sp,
          textAlign = TextAlign.Center,
          lineHeight = (style.fontSizeSp * 1.35f).sp,
          modifier = Modifier.fillMaxWidth(0.92f)
        )
      }
    }
  }
}
