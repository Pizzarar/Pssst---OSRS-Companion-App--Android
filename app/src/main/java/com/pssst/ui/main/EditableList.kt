package com.pssst.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pssst.data.local.ListLayouts
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.osrsPanel
import com.pssst.util.ListLayout

/**
 * A list the player can rearrange: Edit shows up/down and Hide on every row, hidden rows are listed under "Hidden" with
 * Show, and Reset goes back to the default order. The arrangement is saved per [listId] on the phone.
 */
@Composable
fun <T> EditableList(
    listId: String,
    items: List<T>,
    key: (T) -> String,
    label: (T) -> String,
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp,
    header: (LazyListScope.() -> Unit)? = null,
    itemContent: @Composable (T) -> Unit
) {
    val context = LocalContext.current
    val layout by ListLayouts.flow(context, listId).collectAsState()
    val arranged = ListLayout.arrange(items, key, layout)
    var editing by rememberSaveable(listId) { mutableStateOf(false) }
    val visibleKeys = arranged.visible.map(key)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        header?.invoke(this)
        item(key = "__edit_bar") {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (!editing && arranged.hidden.isNotEmpty()) {
                    Text(
                        text = "${arranged.hidden.size} hidden",
                        style = MaterialTheme.typography.labelSmall,
                        color = OsrsTextMuted
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (editing) {
                    TextButton(onClick = { ListLayouts.update(context, listId) { ListLayout() } }) {
                        Text("Reset", color = OsrsTextMuted)
                    }
                }
                TextButton(onClick = { editing = !editing }) {
                    Text(if (editing) "Done" else "Edit list", color = OsrsGold, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        items(arranged.visible, key = { "v_" + key(it) }) { entry ->
            if (!editing) {
                itemContent(entry)
            } else {
                val k = key(entry)
                val index = visibleKeys.indexOf(k)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) { itemContent(entry) }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TextButton(
                            enabled = index > 0,
                            onClick = { ListLayouts.update(context, listId) { it.move(k, -1, visibleKeys) } }
                        ) { Text("▲", color = if (index > 0) OsrsGold else OsrsTextMuted) }
                        TextButton(
                            enabled = index < visibleKeys.size - 1,
                            onClick = { ListLayouts.update(context, listId) { it.move(k, 1, visibleKeys) } }
                        ) { Text("▼", color = if (index < visibleKeys.size - 1) OsrsGold else OsrsTextMuted) }
                        TextButton(onClick = { ListLayouts.update(context, listId) { it.hide(k) } }) {
                            Text("Hide", color = OsrsTextMuted)
                        }
                    }
                }
            }
        }
        if (editing && arranged.hidden.isNotEmpty()) {
            item(key = "__hidden_title") {
                Text(
                    text = "Hidden",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = OsrsGold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(arranged.hidden, key = { "h_" + key(it) }) { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .osrsPanel()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label(entry),
                        style = MaterialTheme.typography.bodyMedium,
                        color = OsrsTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { ListLayouts.update(context, listId) { it.show(key(entry)) } }) {
                        Text("Show", color = OsrsGold)
                    }
                }
            }
        }
        if (arranged.visible.isEmpty() && !editing) {
            item(key = "__all_hidden") {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Everything here is hidden. Tap Edit list to show rows again.", color = OsrsTextMuted)
                }
            }
        }
    }
}
