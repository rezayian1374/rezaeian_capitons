package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.generator.PersianCaptionEngine
import com.example.data.local.AppDatabase
import com.example.data.local.CaptionProjectEntity
import com.example.data.model.CaptionEffect
import com.example.data.model.CaptionPosition
import com.example.data.model.CaptionStyle
import com.example.data.model.SubtitleItem
import com.example.data.model.TimedWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class CaptionViewModel(application: Application) : AndroidViewModel(application) {
  private val dao = AppDatabase.getDatabase(application).captionDao()

  val savedProjects: StateFlow<List<CaptionProjectEntity>> = dao.getAllProjects()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val defaultSampleUri = "android.resource://${application.packageName}/${R.raw.sample_video_ai}"

  private val _videoUri = MutableStateFlow<Uri>(Uri.parse(defaultSampleUri))
  val videoUri: StateFlow<Uri> = _videoUri.asStateFlow()

  private val _videoTitle = MutableStateFlow("ویدیوی هوش مصنوعی (نمونه ۱)")
  val videoTitle: StateFlow<String> = _videoTitle.asStateFlow()

  private val _isPlaying = MutableStateFlow(false)
  val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

  private val _currentPositionMs = MutableStateFlow(0L)
  val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

  private val _durationMs = MutableStateFlow(10000L)
  val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

  private val _playbackSpeed = MutableStateFlow(1.0f)
  val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

  private val _subtitles = MutableStateFlow<List<SubtitleItem>>(PersianCaptionEngine.getSampleAiTechCaptions())
  val subtitles: StateFlow<List<SubtitleItem>> = _subtitles.asStateFlow()

  private val _captionStyle = MutableStateFlow(CaptionStyle())
  val captionStyle: StateFlow<CaptionStyle> = _captionStyle.asStateFlow()

  private val _isGenerating = MutableStateFlow(false)
  val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

  private val _generationMessage = MutableStateFlow<String?>(null)
  val generationMessage: StateFlow<String?> = _generationMessage.asStateFlow()

  private val _customApiKey = MutableStateFlow("")
  val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

  private val _isPersianUi = MutableStateFlow(true)
  val isPersianUi: StateFlow<Boolean> = _isPersianUi.asStateFlow()

  fun toggleUiLanguage() {
    _isPersianUi.value = !_isPersianUi.value
  }

  fun setVideoUri(uri: Uri, title: String? = null) {
    _videoUri.value = uri
    _videoTitle.value = title ?: if (_isPersianUi.value) "ویدیوی انتخابی" else "Selected Video"
    _currentPositionMs.value = 0L
    _isPlaying.value = false
  }

  fun loadSampleVideo(sampleId: Int) {
    val packageName = getApplication<Application>().packageName
    when (sampleId) {
      1 -> {
        _videoUri.value = Uri.parse("android.resource://$packageName/${R.raw.sample_video_ai}")
        _videoTitle.value = if (_isPersianUi.value) "ویدیوی هوش مصنوعی (نمونه ۱)" else "AI Tech Clip (Sample 1)"
        _subtitles.value = PersianCaptionEngine.getSampleAiTechCaptions()
        _durationMs.value = 10000L
      }
      2 -> {
        _videoUri.value = Uri.parse("android.resource://$packageName/${R.raw.sample_video_podcast}")
        _videoTitle.value = if (_isPersianUi.value) "پادکست و ولاگ (نمونه ۲)" else "Podcast & Vlog (Sample 2)"
        _subtitles.value = PersianCaptionEngine.getSamplePodcastCaptions()
        _durationMs.value = 12000L
      }
    }
    _currentPositionMs.value = 0L
    _isPlaying.value = false
  }

  fun updatePlaybackState(playing: Boolean, positionMs: Long, totalDurationMs: Long? = null) {
    _isPlaying.value = playing
    _currentPositionMs.value = positionMs
    if (totalDurationMs != null && totalDurationMs > 0) {
      _durationMs.value = totalDurationMs
    }
  }

  fun setPlaybackSpeed(speed: Float) {
    _playbackSpeed.value = speed
  }

  fun setApiKey(key: String) {
    _customApiKey.value = key.trim()
  }

  fun generateCaptions(topicHint: String = "general") {
    viewModelScope.launch {
      _isGenerating.value = true
      _generationMessage.value = if (_isPersianUi.value) {
        "در حال پردازش گفتار و تولید خودکار زیرنویس فارسی با هوش مصنوعی..."
      } else {
        "Processing speech and generating Persian captions with AI..."
      }

      try {
        val result = PersianCaptionEngine.generateCaptions(
          context = getApplication(),
          videoDurationMs = _durationMs.value,
          customApiKey = _customApiKey.value,
          topicHint = topicHint
        )
        if (result.isNotEmpty()) {
          _subtitles.value = result
          _generationMessage.value = null
        } else {
          _generationMessage.value = if (_isPersianUi.value) "خطا در تولید زیرنویس." else "Failed to generate captions."
        }
      } catch (e: Exception) {
        Log.e("CaptionViewModel", "Generation error", e)
        _generationMessage.value = e.localizedMessage
      } finally {
        _isGenerating.value = false
      }
    }
  }

  fun updateSubtitle(id: String, newText: String, newStart: Long, newEnd: Long) {
    val updated = _subtitles.value.map { item ->
      if (item.id == id) {
        PersianCaptionEngine.createSubtitle(
          startMs = newStart.coerceAtLeast(0),
          endMs = newEnd.coerceAtLeast(newStart + 500),
          text = newText
        )
      } else {
        item
      }
    }.sortedBy { it.startTimeMs }
    _subtitles.value = updated
  }

  fun addSubtitle(startMs: Long, endMs: Long, text: String) {
    val newItem = PersianCaptionEngine.createSubtitle(
      startMs = startMs,
      endMs = endMs.coerceAtLeast(startMs + 1000),
      text = text.ifBlank { if (_isPersianUi.value) "متن زیرنویس جدید" else "New subtitle text" }
    )
    val updated = (_subtitles.value + newItem).sortedBy { it.startTimeMs }
    _subtitles.value = updated
  }

  fun deleteSubtitle(id: String) {
    _subtitles.value = _subtitles.value.filter { it.id != id }
  }

  fun shiftAllSubtitles(offsetMs: Long) {
    val updated = _subtitles.value.map { item ->
      val newStart = (item.startTimeMs + offsetMs).coerceAtLeast(0)
      val newEnd = (item.endTimeMs + offsetMs).coerceAtLeast(newStart + 300)
      PersianCaptionEngine.createSubtitle(newStart, newEnd, item.text)
    }.sortedBy { it.startTimeMs }
    _subtitles.value = updated
  }

  fun updateStyle(
    fontSizeSp: Float? = null,
    textColorHex: String? = null,
    highlightColorHex: String? = null,
    backgroundColorHex: String? = null,
    position: CaptionPosition? = null,
    effect: CaptionEffect? = null,
    isPersianNumerals: Boolean? = null
  ) {
    val current = _captionStyle.value
    _captionStyle.value = current.copy(
      fontSizeSp = fontSizeSp ?: current.fontSizeSp,
      textColorHex = textColorHex ?: current.textColorHex,
      highlightColorHex = highlightColorHex ?: current.highlightColorHex,
      backgroundColorHex = backgroundColorHex ?: current.backgroundColorHex,
      position = position ?: current.position,
      effect = effect ?: current.effect,
      isPersianNumerals = isPersianNumerals ?: current.isPersianNumerals
    )
  }

  fun saveProject(projectName: String) {
    viewModelScope.launch {
      val captionsJson = JSONArray().apply {
        _subtitles.value.forEach { item ->
          put(JSONObject().apply {
            put("id", item.id)
            put("start", item.startTimeMs)
            put("end", item.endTimeMs)
            put("text", item.text)
          })
        }
      }.toString()

      val style = _captionStyle.value
      val styleJson = JSONObject().apply {
        put("fontSizeSp", style.fontSizeSp)
        put("textColorHex", style.textColorHex)
        put("highlightColorHex", style.highlightColorHex)
        put("backgroundColorHex", style.backgroundColorHex)
        put("position", style.position.name)
        put("effect", style.effect.name)
        put("isPersianNumerals", style.isPersianNumerals)
      }.toString()

      val title = projectName.ifBlank { _videoTitle.value }
      val entity = CaptionProjectEntity(
        title = title,
        videoUri = _videoUri.value.toString(),
        durationMs = _durationMs.value,
        captionsJson = captionsJson,
        styleJson = styleJson,
        language = "fa"
      )
      dao.insertProject(entity)
    }
  }

  fun loadProject(entity: CaptionProjectEntity) {
    try {
      _videoUri.value = Uri.parse(entity.videoUri)
      _videoTitle.value = entity.title
      _durationMs.value = entity.durationMs

      // Parse captions
      val array = JSONArray(entity.captionsJson)
      val loaded = mutableListOf<SubtitleItem>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        loaded.add(
          PersianCaptionEngine.createSubtitle(
            startMs = obj.optLong("start", 0L),
            endMs = obj.optLong("end", 1000L),
            text = obj.optString("text", "")
          )
        )
      }
      _subtitles.value = loaded.sortedBy { it.startTimeMs }

      // Parse style
      if (entity.styleJson.isNotBlank()) {
        val styleObj = JSONObject(entity.styleJson)
        _captionStyle.value = CaptionStyle(
          fontSizeSp = styleObj.optDouble("fontSizeSp", 22.0).toFloat(),
          textColorHex = styleObj.optString("textColorHex", "#FFFFFF"),
          highlightColorHex = styleObj.optString("highlightColorHex", "#FACC15"),
          backgroundColorHex = styleObj.optString("backgroundColorHex", "#B3000000"),
          position = runCatching { CaptionPosition.valueOf(styleObj.optString("position", "BOTTOM")) }.getOrDefault(CaptionPosition.BOTTOM),
          effect = runCatching { CaptionEffect.valueOf(styleObj.optString("effect", "REEL_HIGHLIGHT")) }.getOrDefault(CaptionEffect.REEL_HIGHLIGHT),
          isPersianNumerals = styleObj.optBoolean("isPersianNumerals", true)
        )
      }
    } catch (e: Exception) {
      Log.e("CaptionViewModel", "Error loading project", e)
    }
  }

  fun deleteProject(entity: CaptionProjectEntity) {
    viewModelScope.launch {
      dao.deleteProject(entity)
    }
  }
}
