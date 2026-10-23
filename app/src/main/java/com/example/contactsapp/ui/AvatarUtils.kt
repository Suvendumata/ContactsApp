package com.example.contactsapp.ui

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

object AvatarUtils {
    private val avatarColors = listOf(
        Color(0xFFE91E63), // Pink
        Color(0xFF9C27B0), // Purple
        Color(0xFF673AB7), // Deep Purple
        Color(0xFF3F51B5), // Indigo
        Color(0xFF2196F3), // Blue
        Color(0xFF009688), // Teal
        Color(0xFF4CAF50), // Green
        Color(0xFFFF9800), // Orange
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF795548), // Brown
    )

    fun getAvatarColor(name: String): Color {
        if (name.isBlank()) return avatarColors[0]
        val index = abs(name.hashCode()) % avatarColors.size
        return avatarColors[index]
    }

    fun getInitial(name: String): String {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return "?"
        return trimmed.first().uppercase()
    }
}
