package com.example

import com.example.data.generator.PersianCaptionEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testPersianDigitsConversion() {
    val input = "12:34"
    val persian = PersianCaptionEngine.toPersianDigits(input)
    assertEquals("۱۲:۳۴", persian)
  }

  @Test
  fun testCreateSubtitleWithTimedWords() {
    val subtitle = PersianCaptionEngine.createSubtitle(
      startMs = 1000,
      endMs = 4000,
      text = "سلام دوستان عزیز"
    )
    assertEquals(3, subtitle.words.size)
    assertEquals("سلام", subtitle.words[0].word)
    assertEquals(1000L, subtitle.words[0].startMs)
    assertEquals("عزیز", subtitle.words[2].word)
    assertEquals(4000L, subtitle.words[2].endMs)
  }

  @Test
  fun testSrtExportFormat() {
    val subtitles = listOf(
      PersianCaptionEngine.createSubtitle(1000, 3000, "تست زیرنویس فارسی")
    )
    val srt = PersianCaptionEngine.exportToSrt(subtitles, usePersianDigits = false)
    assertTrue(srt.contains("1"))
    assertTrue(srt.contains("00:00:01,000 --> 00:00:03,000"))
    assertTrue(srt.contains("تست زیرنویس فارسی"))
  }

  @Test
  fun testVttExportFormat() {
    val subtitles = listOf(
      PersianCaptionEngine.createSubtitle(500, 2500, "سلام دنیا")
    )
    val vtt = PersianCaptionEngine.exportToVtt(subtitles)
    assertTrue(vtt.startsWith("WEBVTT"))
    assertTrue(vtt.contains("00:00:00.500 --> 00:00:02.500"))
  }
}
