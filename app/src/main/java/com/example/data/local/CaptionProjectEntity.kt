package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "caption_projects")
data class CaptionProjectEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val videoUri: String,
  val durationMs: Long,
  val captionsJson: String,
  val styleJson: String,
  val language: String = "fa",
  val createdAt: Long = System.currentTimeMillis()
)
