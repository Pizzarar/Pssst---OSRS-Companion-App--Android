package com.pssst.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pssst.theme.JetBrainsMonoFontFamily
import com.pssst.theme.OsrsFilterChip
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsProgressBar
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsSegmentedTabs
import com.pssst.theme.OsrsTextField
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.art.ArtIcons
import com.pssst.theme.art.BulletGlyph
import com.pssst.theme.art.EmptyStateKind
import com.pssst.theme.art.FrameWeight
import com.pssst.theme.art.GoldLeafText
import com.pssst.theme.art.OsrsEmptyState
import com.pssst.theme.art.PixelIcon
import com.pssst.theme.art.SpriteTones
import com.pssst.theme.art.maxedSkillCorners
import com.pssst.theme.osrsPanel
import com.pssst.theme.osrsSlot
import com.pssst.util.OsrsXpTable
import java.util.Locale

enum class ProgressionSubTab(val title: String) {
    SKILLS("Skills & XP"),
    QUESTS("Quests")
}

@Composable
fun ProgressionScreen(
    state: MainDashboardUiState,
    onQuestSearchQueryChanged: (String) -> Unit,
    onQuestFilterChanged: (QuestFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(ProgressionSubTab.SKILLS) }
    val tabs = ProgressionSubTab.entries

    Column(modifier = modifier.fillMaxSize()) {
        val completedCount = state.quests.count { it.state == QuestState.COMPLETED }
        OsrsSegmentedTabs(
            tabs = listOf(
                "Skills (Total: ${state.totalLevel})" to 0,
                "Quests ($completedCount/${state.quests.size})" to 0
            ),
            selectedIndex = tabs.indexOf(selectedTab),
            onTabSelected = { selectedTab = tabs[it] },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                ProgressionSubTab.SKILLS -> SkillsSubScreen(state)
                ProgressionSubTab.QUESTS -> QuestsSubScreen(
                    state = state,
                    onQuestSearchQueryChanged = onQuestSearchQueryChanged,
                    onQuestFilterChanged = onQuestFilterChanged
                )
            }
        }
    }
}

@Composable
fun SkillsSubScreen(state: MainDashboardUiState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // 8 rows x 3 columns = 24 cells (23 skills + Total card in cell 24) filling the entire tab height
        for (row in 0 until 8) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    if (index < 23) {
                        val skill = state.skills.getOrNull(index)
                        if (skill != null) {
                            SkillCard(
                                skill = skill,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    } else {
                        TotalSkillCard(
                            totalLevel = state.totalLevel,
                            totalXp = state.totalXp,
                            combatLevel = combatLevel(state.skills),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Standard OSRS combat level formula computed from the 7 combat skills.
 */
fun combatLevel(skills: List<SkillUiModel>): Int {
    if (skills.isEmpty()) return 3
    fun levelOf(name: String, default: Int = 1): Int =
        skills.find { it.name.equals(name, ignoreCase = true) }?.level ?: default

    val defence = levelOf("Defence", 1)
    val hitpoints = levelOf("Hitpoints", 10)
    val prayer = levelOf("Prayer", 1)
    val attack = levelOf("Attack", 1)
    val strength = levelOf("Strength", 1)
    val ranged = levelOf("Ranged", 1)
    val magic = levelOf("Magic", 1)

    val base = 0.25 * (defence + hitpoints + (prayer / 2))
    val melee = 0.325 * (attack + strength)
    val range = 0.325 * ((ranged * 3) / 2)
    val mage = 0.325 * ((magic * 3) / 2)
    return kotlin.math.floor(base + maxOf(melee, range, mage)).toInt().coerceAtLeast(3)
}

private fun formatCompactBracketXp(xp: Long): String {
    return when {
        xp >= 1_000_000_000L -> String.format(Locale.US, "%.2fB", xp / 1_000_000_000.0)
        xp >= 1_000_000L -> String.format(Locale.US, "%.1fM", xp / 1_000_000.0)
        xp >= 10_000L -> "${xp / 1_000L}K"
        xp >= 1_000L -> String.format(Locale.US, "%.1fK", xp / 1_000.0)
        else -> xp.toString()
    }
}

/**
 * Top section of a skill tile: recessed icon well on the left, and a stacked column on the right
 * with the level centered at top-middle and the XP metric ("177K / 337K" or "18.1M xp") below it.
 */
@Composable
private fun SkillHeaderRow(
    modifier: Modifier,
    level: String,
    levelColor: androidx.compose.ui.graphics.Color,
    midPrimary: String,
    midSecondary: String,
    midPrimaryColor: androidx.compose.ui.graphics.Color,
    hasIcon: Boolean,
    icon: @Composable (inner: androidx.compose.ui.unit.Dp) -> Unit
) {
    androidx.compose.foundation.layout.BoxWithConstraints(modifier = modifier) {
        val density = androidx.compose.ui.platform.LocalDensity.current
        val gap = 4.dp
        val wellSize = (maxHeight * 0.92f).coerceIn(26.dp, 52.dp)
        val availWidth = (maxWidth - wellSize - gap).coerceAtLeast(24.dp)

        val levelDp = minOf(
            maxHeight * 0.46f,
            availWidth / (level.length.coerceAtLeast(2) * 0.64f)
        ).coerceIn(11.dp, 16.dp)

        val totalMidChars = (midPrimary.length + midSecondary.length).coerceAtLeast(1)
        val midDp = minOf(
            maxHeight * 0.42f,
            availWidth / (totalMidChars * 0.63f)
        ).coerceIn(9.dp, 13.5.dp)

        val levelSp = with(density) { levelDp.toSp() }
        val midSp = with(density) { midDp.toSp() }
        val tight = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = false)

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(wellSize)
                    .osrsSlot(hasItem = hasIcon, cornerRadius = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                icon(wellSize - 4.dp)
            }
            Spacer(modifier = Modifier.width(gap))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = level,
                    style = MaterialTheme.typography.titleMedium.copy(platformStyle = tight),
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = levelSp,
                    lineHeight = levelSp,
                    fontWeight = FontWeight.ExtraBold,
                    color = levelColor,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Visible
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = midPrimaryColor,
                                fontWeight = FontWeight.ExtraBold
                            )
                        ) {
                            append(midPrimary)
                        }
                        withStyle(
                            SpanStyle(
                                color = OsrsTextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(midSecondary)
                        }
                    },
                    style = MaterialTheme.typography.labelSmall.copy(platformStyle = tight),
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = midSp,
                    lineHeight = midSp,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Visible
                )
            }
        }
    }
}

@Composable
fun SkillCard(skill: SkillUiModel, modifier: Modifier = Modifier) {
    val bitmap = remember(skill.itemId, skill.iconBase64) {
        if (skill.itemId > 0) ItemIconCache.getOrCreate(skill.itemId, skill.iconBase64) else null
    }
    val isMaxed = skill.level >= 99
    val levelLabel = if (isMaxed && skill.virtualLevel > 99) {
        "99 (${skill.virtualLevel})"
    } else {
        "${skill.level}"
    }
    val bracketXp = (OsrsXpTable.xpForLevel(skill.level + 1) - OsrsXpTable.xpForLevel(skill.level)).coerceAtLeast(1L)

    Box(
        modifier = modifier
            .osrsPanel(weight = FrameWeight.LIGHT)
            .maxedSkillCorners(enabled = isMaxed)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Row 1: icon well on left; stacked top-middle level + middle XP row on right
            SkillHeaderRow(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                level = levelLabel,
                levelColor = if (isMaxed) OsrsHerbGreen else OsrsGold,
                midPrimary = if (isMaxed) formatXp(skill.xp) else formatCompactBracketXp(skill.xpRemaining),
                midSecondary = if (isMaxed) " xp" else "/${formatCompactBracketXp(bracketXp)}",
                midPrimaryColor = if (isMaxed) OsrsHerbGreen else OsrsTextPrimary,
                hasIcon = bitmap != null
            ) { inner ->
                if (bitmap != null) {
                    com.pssst.theme.art.PixelItemImage(
                        bitmap = bitmap,
                        contentDescription = skill.name,
                        modifier = Modifier.size(inner)
                    )
                }
            }

            // Row 2: Skill name + % to next level (MAX in green at 99)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = skill.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OsrsTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isMaxed) "MAX" else "${(skill.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isMaxed) OsrsHerbGreen else OsrsTextMuted,
                    maxLines = 1
                )
            }

            // Row 3: full-width 8dp bar
            OsrsProgressBar(
                progress = if (isMaxed) 1f else skill.progress,
                color = if (isMaxed) OsrsHerbGreen else OsrsGold,
                height = 8.dp
            )
        }
    }
}

@Composable
fun TotalSkillCard(
    totalLevel: Int,
    totalXp: Long,
    modifier: Modifier = Modifier,
    combatLevel: Int = 3
) {
    val maxTotalLevel = 2277
    val isMaxed = totalLevel >= maxTotalLevel
    Box(
        modifier = modifier
            .osrsPanel(weight = FrameWeight.HEAVY)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Row 1: star well, total level at top-middle, combat level below
            SkillHeaderRow(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                level = "$totalLevel",
                levelColor = if (isMaxed) OsrsHerbGreen else OsrsGold,
                midPrimary = "$combatLevel",
                midSecondary = " cmb",
                midPrimaryColor = OsrsTextPrimary,
                hasIcon = true
            ) { inner ->
                PixelIcon(
                    sprite = ArtIcons.ProgressStar,
                    tones = SpriteTones.GOLD,
                    modifier = Modifier.size(inner * 0.7f)
                )
            }

            // Row 2: "Total" on left, total XP on right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GoldLeafText(
                    text = "Total",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp,
                        lineHeight = 14.sp
                    ),
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${formatXp(totalXp)} xp",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = OsrsTextMuted,
                    maxLines = 1
                )
            }

            // Row 3: full-width 8dp bar toward 2277
            OsrsProgressBar(
                progress = (totalLevel.toFloat() / maxTotalLevel.toFloat()).coerceIn(0f, 1f),
                color = if (isMaxed) OsrsHerbGreen else OsrsGold,
                height = 8.dp
            )
        }
    }
}

@Composable
fun QuestsSubScreen(
    state: MainDashboardUiState,
    onQuestSearchQueryChanged: (String) -> Unit,
    onQuestFilterChanged: (QuestFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Input
        OsrsTextField(
            value = state.questSearchQuery,
            onValueChange = onQuestSearchQueryChanged,
            label = "Search Quests",
            placeholder = "e.g. Dragon Slayer or Desert Treasure",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            QuestFilter.entries.forEach { filter ->
                val isSelected = state.questFilter == filter
                val count = when (filter) {
                    QuestFilter.ALL -> state.quests.size
                    QuestFilter.COMPLETED -> state.quests.count { it.state == QuestState.COMPLETED }
                    QuestFilter.IN_PROGRESS -> state.quests.count { it.state == QuestState.IN_PROGRESS }
                    QuestFilter.NOT_STARTED -> state.quests.count { it.state == QuestState.NOT_STARTED }
                }
                OsrsFilterChip(
                    text = "${filter.label} ($count)",
                    selected = isSelected,
                    onClick = { onQuestFilterChanged(filter) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quest list
        if (state.quests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                OsrsEmptyState(
                    kind = EmptyStateKind.QUEST_SCROLL,
                    message = "No quest data synced yet.\nIt appears after you log in with Pssst Companion running."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(state.filteredQuests, key = { it.name }) { quest ->
                    QuestCard(quest)
                }
            }
        }
    }
}

@Composable
fun QuestCard(quest: QuestUiModel, modifier: Modifier = Modifier) {
    val (statusText, statusColor) = when (quest.state) {
        QuestState.COMPLETED -> Pair("Completed", OsrsHerbGreen)
        QuestState.IN_PROGRESS -> Pair("In Progress", OsrsGold)
        QuestState.NOT_STARTED -> Pair("Not Started", OsrsRed)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel(weight = FrameWeight.LIGHT)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                BulletGlyph(size = 6.dp, color = statusColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = quest.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall,
                color = statusColor
            )
        }
    }
}
