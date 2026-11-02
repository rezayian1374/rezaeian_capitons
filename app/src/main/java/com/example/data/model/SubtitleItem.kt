package com.example.data.model

import java.util.UUID

data class SubtitleItem(
  val id: String = UUID.randomUUID().toString(),
  val startTimeMs: Long,
  val endTimeMs: Long,
  val text: String,
  val words: List<TimedWord> = emptyList()
) {
  val durationMs: Long
    get() = (endTimeMs - startTimeMs).coerceAtLeast(0)

  fun containsTime(timeMs: Long): Boolean {
    return timeMs in startTimeMs..endTimeMs
  }

  fun getActiveWord(timeMs: Long): TimedWord? {
    if (words.isEmpty()) return null
    return words.firstOrNull { timeMs in it.startMs..it.endMs }
  }
}
