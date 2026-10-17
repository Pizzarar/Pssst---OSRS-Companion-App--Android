package com.pssst.util

object OsrsXpTable {
    val SKILL_NAMES = listOf(
        "Attack", "Hitpoints", "Mining",
        "Strength", "Agility", "Smithing",
        "Defence", "Herblore", "Fishing",
        "Ranged", "Thieving", "Cooking",
        "Prayer", "Crafting", "Firemaking",
        "Magic", "Fletching", "Woodcutting",
        "Runecraft", "Slayer", "Farming",
        "Construction", "Hunter"
    )

    private val XP_TABLE: LongArray = LongArray(127).apply {
        this[1] = 0L
        var points = 0.0
        for (lvl in 1..126) {
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0))
            if (lvl + 1 <= 126) {
                this[lvl + 1] = Math.floor(points / 4.0).toLong()
            }
        }
    }

    fun xpForLevel(level: Int): Long {
        if (level <= 1) return 0L
        if (level >= 126) return XP_TABLE[126]
        return XP_TABLE[level]
    }

    fun levelForXp(xp: Long): Int {
        if (xp <= 0L) return 1
        for (lvl in 126 downTo 1) {
            if (xp >= XP_TABLE[lvl]) return lvl
        }
        return 1
    }

    fun progressToNextLevel(xp: Long, currentLevel: Int): Float {
        val targetLvl = (currentLevel + 1).coerceAtMost(126)
        if (targetLvl == currentLevel) return 1.0f
        val currentLvlXp = xpForLevel(currentLevel)
        val nextLvlXp = xpForLevel(targetLvl)
        if (nextLvlXp <= currentLvlXp) return 1.0f
        val progress = (xp - currentLvlXp).toFloat() / (nextLvlXp - currentLvlXp).toFloat()
        return progress.coerceIn(0.0f, 1.0f)
    }

    fun xpRemainingToNextLevel(xp: Long, currentLevel: Int): Long {
        val targetLvl = (currentLevel + 1).coerceAtMost(126)
        if (targetLvl == currentLevel) return 0L
        val nextLvlXp = xpForLevel(targetLvl)
        return (nextLvlXp - xp).coerceAtLeast(0L)
    }
}
