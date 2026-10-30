package com.javadu.data.database.entities

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Фиксирует текущее поведение системы уровней.
 *
 * Формула: порог уровня N = 100 * 1.5^(N-1), суммарный XP накапливается.
 * XP хранится в Int, поэтому максимально достижимый уровень — 40
 * (порог 40 уровня ≈ 1.47 млрд XP). Расчёт внутри идёт в Long без переполнения.
 */
class LevelSystemTest {

    private companion object {
        /** Шаг перебора XP: простое число, чтобы не попадать только на «круглые» значения. */
        const val STEP = 9_999_991
    }

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

    /** xpToReach согласован с getLevelInfo для всех достижимых уровней (2..40). */
    @Test
    fun xpToReachMatchesGetLevelInfoForAllReachableLevels() {
        for (level in 2..40) {
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
            40 to LevelInfo.Tier.MID
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

    /** Инварианты прогресса на всём диапазоне Int: уровень не убывает, progress в [0,1], currentXp < nextLevelXp. */
    @Test
    fun progressInvariantsHoldInReachableRange() {
        var previousLevel = 0
        var xp = 0
        while (xp in 0..Int.MAX_VALUE - STEP) {
            val info = LevelSystem.getLevelInfo(xp)
            assertTrue("уровень не убывает при $xp XP", info.level >= previousLevel)
            assertTrue("progress в [0,1] при $xp XP", info.progress in 0f..1f)
            assertTrue("currentXp в [0, nextLevelXp) при $xp XP",
                info.currentXp in 0 until info.nextLevelXp)
            previousLevel = info.level
            xp += STEP
        }
    }

    /** Регрессия: при Int.MAX_VALUE XP сумма порогов не переполняется и уровень не прыгает на 100. */
    @Test
    fun maxIntXpDoesNotOverflow() {
        val info = LevelSystem.getLevelInfo(Int.MAX_VALUE)
        assertEquals(40, info.level)
        assertEquals(LevelInfo.Tier.MID, info.tier)
        assertEquals(Int.MAX_VALUE - xpToReach(40), info.currentXp)
        assertEquals(LevelSystem.getNextLevelThreshold(40), info.nextLevelXp)
        assertTrue(info.progress in 0f..1f)
    }

    /** Регрессия: порог 40 уровня (≈1.47 млрд XP) раньше давал 100 уровень и тир GRANDMASTER. */
    @Test
    fun level40ThresholdGivesLevel40() {
        val info = LevelSystem.getLevelInfo(xpToReach(40))
        assertEquals(40, info.level)
        assertEquals(0, info.currentXp)
    }

    /** Отрицательный XP считается нулём: 1 уровень и неотрицательный currentXp. */
    @Test
    fun negativeXpIsTreatedAsZero() {
        val info = LevelSystem.getLevelInfo(-50)
        assertEquals(1, info.level)
        assertEquals(0, info.currentXp)
        assertEquals(0f, info.progress, 0.0001f)
    }

    /** Порог, не помещающийся в Int (с 43 уровня), ограничивается Int.MAX_VALUE. */
    @Test
    fun thresholdAboveIntRangeIsCappedAtIntMax() {
        assertTrue(LevelSystem.getNextLevelThreshold(42) < Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, LevelSystem.getNextLevelThreshold(43))
    }
}
