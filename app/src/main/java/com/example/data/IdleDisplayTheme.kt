package com.example.data

import androidx.compose.ui.graphics.Color

enum class IdleDisplayTheme(
    val id: String,
    val displayName: String,
    val iconEmoji: String,
    val accentColor: Color,
    val surfaceColor: Color,
    val dimTextColor: Color,
    val subtleBorderColor: Color,
    val backgroundColor: Color = Color(0xFF000000)
) {
    RED_NIGHT(
        id = "red_night",
        displayName = "Sleep Red (Zero Blue Light)",
        iconEmoji = "🔴",
        accentColor = Color(0xFFFF5252),
        surfaceColor = Color(0xFF180A0A),
        dimTextColor = Color(0xFF9E5C5C),
        subtleBorderColor = Color(0xFF3B1515)
    ),
    AMBER(
        id = "amber",
        displayName = "Amber Candlelight",
        iconEmoji = "🍯",
        accentColor = Color(0xFFFFB74D),
        surfaceColor = Color(0xFF16120B),
        dimTextColor = Color(0xFF94816C),
        subtleBorderColor = Color(0xFF382A16)
    ),
    MINT(
        id = "mint",
        displayName = "Moonlight Mint",
        iconEmoji = "🌿",
        accentColor = Color(0xFF81C784),
        surfaceColor = Color(0xFF0C140E),
        dimTextColor = Color(0xFF67856C),
        subtleBorderColor = Color(0xFF16301A)
    ),
    LAVENDER(
        id = "lavender",
        displayName = "Starlight Lavender",
        iconEmoji = "💜",
        accentColor = Color(0xFFCE93D8),
        surfaceColor = Color(0xFF140D17),
        dimTextColor = Color(0xFF8B6C91),
        subtleBorderColor = Color(0xFF331A38)
    ),
    MONOCHROME(
        id = "monochrome",
        displayName = "Clean Minimalist",
        iconEmoji = "⚪",
        accentColor = Color(0xFFE2E2E2),
        surfaceColor = Color(0xFF121212),
        dimTextColor = Color(0xFF7A7A7A),
        subtleBorderColor = Color(0xFF2E2E2E)
    )
}
