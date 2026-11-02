package com.example.data.model

enum class CaptionPosition(val titleFa: String, val titleEn: String) {
  BOTTOM("پایین صفحه", "Bottom"),
  CENTER("وسط صفحه", "Center"),
  TOP("بالای صفحه", "Top")
}

enum class CaptionEffect(val titleFa: String, val titleEn: String) {
  REEL_HIGHLIGHT("هایلایت کلمه به کلمه (ریلز)", "Word Karaoke Highlight"),
  CLASSIC_BOX("کادر مشکی کلاسیک", "Classic Background Box"),
  MINIMAL_SHADOW("سایه نرم مینیمال", "Soft Drop Shadow"),
  NEON_GLOW("نئون درخشان مدرن", "Neon Glow")
}

data class CaptionStyle(
  val fontSizeSp: Float = 22f,
  val textColorHex: String = "#FFFFFF",
  val highlightColorHex: String = "#FACC15",
  val backgroundColorHex: String = "#B3000000",
  val position: CaptionPosition = CaptionPosition.BOTTOM,
  val effect: CaptionEffect = CaptionEffect.REEL_HIGHLIGHT,
  val isPersianNumerals: Boolean = true,
  val bold: Boolean = true
)
