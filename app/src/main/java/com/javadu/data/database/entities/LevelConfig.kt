package com.javadu.data.database.entities

data class LevelInfo(
    val level: Int,
    val currentXp: Int,
    val nextLevelXp: Int,
    val progress: Float,
    val tier: Tier,
    val title: String
) {
    val leveledUp: Boolean = progress >= 1.0f

    enum class Tier(val displayName: String, val colorHex: String) {
        BEGINNER("Новичок", "#7C7C7C"),
        APPRENTICE("Ученик", "#C4A484"),
        JUNIOR("Junior", "#4CAF50"),
        MID("Middle", "#2196F3"),
        SENIOR("Senior", "#9C27B0"),
        EXPERT("Эксперт", "#FF9800"),
        MASTER("Мастер", "#F44336"),
        GRANDMASTER("Grandmaster", "#FFD700")
    }
}

object LevelSystem {
    private const val BASE_XP = 100
    private const val GROWTH_FACTOR = 1.5f
    private const val LEVEL_CAP = 100

    fun getNextLevelThreshold(level: Int): Int {
        if (level >= LEVEL_CAP) return Int.MAX_VALUE
        return (BASE_XP * Math.pow(GROWTH_FACTOR.toDouble(), (level - 1).toDouble())).toInt()
    }

    fun getLevelInfo(totalXp: Int): LevelInfo {
        var level = 1
        var xpNeededForCurrentLevel = 0
        var xpForNextLevel = getNextLevelThreshold(1)
        var accumulatedXp = 0

        while (level < LEVEL_CAP && accumulatedXp + xpForNextLevel <= totalXp) {
            accumulatedXp += xpForNextLevel
            level++
            xpNeededForCurrentLevel = xpForNextLevel
            xpForNextLevel = getNextLevelThreshold(level)
        }

        val currentLevelThreshold = accumulatedXp
        val nextLevelThreshold = accumulatedXp + xpForNextLevel
        val progress = if (nextLevelThreshold > currentLevelThreshold) {
            ((totalXp - currentLevelThreshold).toFloat() / xpForNextLevel).coerceIn(0f, 1f)
        } else {
            1f
        }

        val tier = getTierForLevel(level)
        val title = getTitleForLevel(level, tier)

        return LevelInfo(
            level = level,
            currentXp = totalXp - currentLevelThreshold,
            nextLevelXp = xpForNextLevel,
            progress = progress,
            tier = tier,
            title = title
        )
    }

    private fun getTierForLevel(level: Int): LevelInfo.Tier {
        return when {
            level <= 5 -> LevelInfo.Tier.BEGINNER
            level <= 10 -> LevelInfo.Tier.APPRENTICE
            level <= 20 -> LevelInfo.Tier.JUNIOR
            level <= 40 -> LevelInfo.Tier.MID
            level <= 60 -> LevelInfo.Tier.SENIOR
            level <= 80 -> LevelInfo.Tier.EXPERT
            level <= 95 -> LevelInfo.Tier.MASTER
            else -> LevelInfo.Tier.GRANDMASTER
        }
    }

    private fun getTitleForLevel(level: Int, tier: LevelInfo.Tier): String {
        val tierTitles = mapOf(
            LevelInfo.Tier.BEGINNER to listOf("Новичок", "Начинающий", "Любитель", "Исследователь", "Открыватель"),
            LevelInfo.Tier.APPRENTICE to listOf("Ученик", "Практик", "Осваивающий", "Набирающийся опыта", "Развивающийся"),
            LevelInfo.Tier.JUNIOR to listOf("Junior QA", "Junior Java Dev", "Junior AQA", "Code Starter", "Java Novice"),
            LevelInfo.Tier.MID to listOf("Middle QA", "Middle Java", "AQA Specialist", "Test Pro", "Java Developer"),
            LevelInfo.Tier.SENIOR to listOf("Senior QA", "Senior Java Dev", "AQA Lead", "Test Master", "Java Expert"),
            LevelInfo.Tier.EXPERT to listOf("Expert QA", "Java Guru", "AQA Architect", "Test Legend", "Code Wizard"),
            LevelInfo.Tier.MASTER to listOf("Master QA", "Java Master", "AQA Master", "Test Sage", "Code Ninja"),
            LevelInfo.Tier.GRANDMASTER to listOf("Grandmaster", "Java Legend", "AQA God", "Test Titan", "Code Overlord")
        )

        val titles = tierTitles[tier] ?: listOf(tier.displayName)
        val index = ((level - 1) % titles.size).coerceIn(0, titles.size - 1)
        return titles[index]
    }

    fun getTierColor(tier: LevelInfo.Tier): String {
        return tier.colorHex
    }
}
