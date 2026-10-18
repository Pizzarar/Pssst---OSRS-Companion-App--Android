package com.pssst.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.pssst.theme.OsrsYellow
import com.pssst.theme.osrsPanel

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

@Composable
fun SkillCard(skill: SkillUiModel, modifier: Modifier = Modifier) {
    val bitmap = remember(skill.itemId, skill.iconBase64) {
        if (skill.itemId > 0) ItemIconCache.getOrCreate(skill.itemId, skill.iconBase64) else null
    }

    Box(
        modifier = modifier
            .osrsPanel(showRivets = false)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Row 1: 24dp Skill Icon on left, 20sp Level on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = skill.name,
                        filterQuality = FilterQuality.None,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.size(24.dp))
                }
                Text(
                    text = "${skill.level}",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (skill.level >= 99) OsrsHerbGreen else OsrsYellow
                )
            }

            // Row 2: Full-width Skill Name
            Text(
                text = skill.name,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = OsrsTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Row 3: Standard height (8dp) progress bar
            OsrsProgressBar(
                progress = skill.progress,
                color = if (skill.level >= 99) OsrsHerbGreen else OsrsGold,
                thin = false
            )

            // Row 4: Formatted XP on left, % or MAX on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formatXp(skill.xp)} XP",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    color = OsrsTextMuted,
                    maxLines = 1
                )
                Text(
                    text = if (skill.level >= 99) "MAX" else "${(skill.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (skill.level >= 99) OsrsHerbGreen else OsrsTextMuted,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun TotalSkillCard(
    totalLevel: Int,
    totalXp: Long,
    modifier: Modifier = Modifier
) {
    val maxTotalLevel = 2277
    val isMaxed = totalLevel >= maxTotalLevel
    Box(
        modifier = modifier
            .osrsPanel(showRivets = false)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StarVectorIcon(
                    tint = OsrsGold,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "$totalLevel",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isMaxed) OsrsHerbGreen else OsrsYellow
                )
            }

            Text(
                text = "Total Level",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = OsrsGold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formatXp(totalXp)} XP",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OsrsTextPrimary,
                    maxLines = 1
                )
                Text(
                    text = "/ $maxTotalLevel",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    color = OsrsTextMuted,
                    maxLines = 1
                )
            }
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
                Text(
                    text = "No quest data synced yet.\nIt appears after you log in with Pssst Companion running.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OsrsTextMuted
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
        QuestState.IN_PROGRESS -> Pair("In Progress", OsrsYellow)
        QuestState.NOT_STARTED -> Pair("Not Started", OsrsRed)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = quest.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = statusColor
            )

            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall,
                color = statusColor
            )
        }
    }
}
