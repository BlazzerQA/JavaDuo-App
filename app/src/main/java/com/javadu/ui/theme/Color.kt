package com.javadu.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// БАЗОВЫЕ И СИСТЕМНЫЕ ЦВЕТА (МАТРИЦА)
// ==========================================
val JavaGreen = Color(0xFF00FF41)
val JavaGreenDark = Color(0xFF00CC33)
val JavaGreenLight = Color(0xFF33FF66)

// ==========================================
// ТЕМНАЯ ТЕМА ПРИЛОЖЕНИЯ (Dark Theme)
// ==========================================
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E2E)
val DarkSurfaceVariant = Color(0xFF2A2A3E)

// Светлые тексты для темной темы
val TextPrimaryDark = Color(0xFFE2E8F0) // Объединили с TextPrimary (идеальный матовый геймерский цвет)
val TextSecondaryDark = Color(0xFF8B949E)

// ==========================================
// СВЕТЛАЯ ТЕМА ПРИЛОЖЕНИЯ (Light Theme)
// ==========================================
val LightBackground = Color(0xFFF6F8FA)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFE1E4E8)

// Темные тексты для светлой темы
val TextPrimaryLight = Color(0xFF24292F)
val TextSecondaryLight = Color(0xFF57606A)

// ==========================================
// ИГРОВОЙ ИНТЕРФЕЙС И КАРТОЧКИ (RPG Elements)
// ==========================================
val GoldColor = Color(0xFFD6B06A)
val DarkGoldBorder = Color(0xFF4E3E28)

// Специфичные цвета для профиля и карточек
val EnemyCardColor = Color(0xFF2A1B3D)
val AvatarFrameBackground = Color(0xFF10141B)
val ShieldBackground = Color(0xFF0A0D12)
val ProgressTrackBackground = Color(0xFF13100B)

// Тексты внутри игровых карточек
val TextCardSecondary = Color.White.copy(alpha = 0.6f) // Переименовал для ясности, что это для карточек

// ==========================================
// СТАТУСЫ И БАФФЫ (Gameplay Statuses)
// ==========================================
val SuccessGreen = Color(0xFF2EA043)
val BuffGreen = JavaGreen // ССЫЛКА НА ДУБЛИКАТ: теперь использует тот же цвет, что и JavaGreen
val ErrorRed = Color(0xFFDA3633)
val WarningYellow = Color(0xFFD29922)