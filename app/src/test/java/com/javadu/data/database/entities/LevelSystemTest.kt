package com.javadu.data.database.entities

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Фиксирует текущее поведение системы уровней.
 *
 * Формула: порог уровня N = 100 * 1.5^(N-1), суммарный XP накапливается.
 * Проверки ограничены диапазоном XP, достижимым в Int без переполнения
 * (уровни 1..39). Начиная с ~1.47 млрд XP (порог 40 уровня) сумма в Int
 * переполняется и игрок прыгает на 100 уровень — известный баг,
 * см. отчёт этапа 0.
 */
class LevelSystemTest {

    /** Суммарный XP, необходимый, чтобы достичь уровня [level]. */
    private fun xpToReach(level: Int): Int {
        var total = 0L
        for (l in 1 until level) total += LevelSystem.getNextLevelThreshold(l)
        require(total <= Int.MAX_VALUE) { "XP для уровня $level не помещается в Int" }
        return total.toInt()
    }

    /** Пороги уровней растут в 1.5 раза. */
    @Test
    fun levelThresholdsGrowByFactorOfOneAndHalf() {
        assertEquals(100, LevelSystem.getNextLevelThreshold(1))
        assertEquals(150, LevelSystem.getNextLevelThreshold(2))
        assertEquals(225, LevelSystem.getNextLevelThreshold(3))
        assertEquals(337, LevelSystem.getNextLevelThreshold(4))
    }

    /** На максимальном уровне порог бесконечен. */
    @Test
    fun thresholdIsInfiniteAtMaxLevel() {
        assertEquals(Int.MAX_VALUE, LevelSystem.getNextLevelThreshold(100))
    }

    /** Новый игрок с нулевым XP — 1 уровень, тир «Новичок». */
    @Test
    fun newPlayerWithZeroXpIsLevelOne() {
        val info = LevelSystem.getLevelInfo(0)
        assertEquals(1, info.level)
        assertEquals(0, info.currentXp)
        assertEquals(100, info.nextLevelXp)
        assertEquals(0f, info.progress, 0.0001f)
        assertEquals(LevelInfo.Tier.BEGINNER, info.tier)
        assertEquals("Новичок", info.title)
    }

    /** За 1 XP до порога уровень не повышается. */
    @Test
    fun levelDoesNotIncreaseOneXpBeforeThreshold() {
        val info = LevelSystem.getLevelInfo(99)
        assertEquals(1, info.level)
        assertEquals(99, info.currentXp)
        assertEquals(0.99f, info.progress, 0.0001f)
    }

    /** Ровно на пороге уровень повышается, остаток XP обнуляется. */
    @Test
    fun levelIncreasesExactlyAtThresholdAndResetsCurrentXp() {
        val info = LevelSystem.getLevelInfo(100)
        assertEquals(2, info.level)
        assertEquals(0, info.currentXp)
        assertEquals(150, info.nextLevelXp)
        assertEquals("Начинающий", info.title)
    }

    /** XP накапливается между уровнями. */
    @Test
    fun xpAccumulatesAcrossLevels() {
        assertEquals(2, LevelSystem.getLevelInfo(249).level)
        assertEquals(3, LevelSystem.getLevelInfo(250).level)
        assertEquals(3, LevelSystem.getLevelInfo(474).level)
        assertEquals(4, LevelSystem.getLevelInfo(475).level)
    }

    /** xpToReach согласован с getLevelInfo на всём достижимом диапазоне (уровни 2..39). */
    @Test
    fun xpToReachMatchesGetLevelInfoInReachableRange() {
        for (level in 2..39) {
            val xp = xpToReach(level)
            assertEquals("ровно $xp XP", level, LevelSystem.getLevelInfo(xp).level)
            assertEquals("$xp - 1 XP", level - 1, LevelSystem.getLevelInfo(xp - 1).level)
        }
    }

    /** Границы тиров: уровень, на котором меняется тир. */
    @Test
    fun tierBoundaries() {
        val expected = mapOf(
            5 to LevelInfo.Tier.BEGINNER,
            6 to LevelInfo.Tier.APPRENTICE,
            10 to LevelInfo.Tier.APPRENTICE,
            11 to LevelInfo.Tier.JUNIOR,
            20 to LevelInfo.Tier.JUNIOR,
            21 to LevelInfo.Tier.MID,
            39 to LevelInfo.Tier.MID
        )
        expected.forEach { (level, tier) ->
            assertEquals("уровень $level", tier, LevelSystem.getLevelInfo(xpToReach(level)).tier)
        }
    }

    /** Титулы чередуются внутри тира. */
    @Test
    fun titlesRotateWithinTier() {
        assertEquals("Ученик", LevelSystem.getLevelInfo(xpToReach(6)).title)
        assertEquals("Junior QA", LevelSystem.getLevelInfo(xpToReach(11)).title)
    }

    /** Инварианты прогресса: уровень не убывает, progress в [0,1], currentXp < nextLevelXp. */
    @Test
    fun progressInvariantsHoldInReachableRange() {
        var previousLevel = 0
        var xp = 0
        while (xp <= 1_000_000_000) {
            val info = LevelSystem.getLevelInfo(xp)
            assertTrue("уровень не убывает при $xp XP", info.level >= previousLevel)
            assertTrue("progress в [0,1] при $xp XP", info.progress in 0f..1f)
            assertTrue("currentXp в [0, nextLevelXp) при $xp XP",
                info.currentXp in 0 until info.nextLevelXp)
            previousLevel = info.level
            xp += 997_013
        }
    }
}
