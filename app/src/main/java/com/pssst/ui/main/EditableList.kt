package com.pssst.ui.main

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.pssst.data.local.ListLayouts
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.osrsPanel
import com.pssst.util.ListLayout
import kotlin.math.roundToInt

private val EDIT_ROW_HEIGHT = 52.dp

/**
 * A list the player can arrange. Edit list turns it into a compact checklist: the checkbox shows or hides a row, and
 * dragging the ≡ handle moves it. Reset goes back to the default. The arrangement is saved per [listId] on the phone.
 */
@Composable
fun <T> EditableList(
    listId: String,
    items: List<T>,
    key: (T) -> String,
    label: (T) -> String,
    modifier: Modifier = Modifier,
    editing: Boolean,
    spacing: Dp = 8.dp,
    header: (LazyListScope.() -> Unit)? = null,
    itemContent: @Composable (T) -> Unit
) {
    val context = LocalContext.current
    val layout by ListLayouts.flow(context, listId).collectAsState()
    val arranged = ListLayout.arrange(items, key, layout)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        header?.invoke(this)
        if (editing) {
            item(key = "__editor") {
                ReorderChecklist(
                    onReset = { ListLayouts.update(context, listId) { ListLayout() } },
                    rows = arranged.all.map { key(it) to label(it) },
                    hidden = layout.hidden,
                    onShownChanged = { k, shown -> ListLayouts.update(context, listId) { it.setShown(k, shown) } },
                    onReordered = { keys -> ListLayouts.update(context, listId) { it.withOrder(keys) } }
                )
            }
        } else {
            items(arranged.visible, key = { "v_" + key(it) }) { entry -> itemContent(entry) }
            if (arranged.visible.isEmpty() && arranged.hidden.isNotEmpty()) {
                item(key = "__all_hidden") {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Everything here is hidden. Tap the pencil at the top to show rows again.", color = OsrsTextMuted)
                    }
                }
            } else if (arranged.hidden.isNotEmpty()) {
                item(key = "__hidden_note") {
                    Text(
                        text = "${arranged.hidden.size} hidden · tap the pencil to change",
                        style = MaterialTheme.typography.labelSmall,
                        color = OsrsTextMuted,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/** Compact rows: drag handle, checkbox, name. Dragging reorders live and saves when the finger lifts. */
@Composable
private fun ReorderChecklist(
    onReset: () -> Unit,
    rows: List<Pair<String, String>>,
    hidden: Set<String>,
    onShownChanged: (String, Boolean) -> Unit,
    onReordered: (List<String>) -> Unit
) {
    val labels = rows.toMap()
    val keys = rows.map { it.first }
    var order by remember(keys) { mutableStateOf(keys) }
    var dragging by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val rowPx = with(LocalDensity.current) { EDIT_ROW_HEIGHT.toPx() }

    Column(modifier = Modifier.fillMaxWidth().osrsPanel().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Tick to show, drag ≡ to move", style = MaterialTheme.typography.labelSmall, color = OsrsTextMuted,
                modifier = Modifier.weight(1f))
            TextButton(onClick = onReset) { Text("Reset", color = OsrsTextMuted) }
        }
        order.forEach { k ->
            key(k) {
                val isDragged = k == dragging
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(EDIT_ROW_HEIGHT)
                        .zIndex(if (isDragged) 1f else 0f)
                        .graphicsLayer {
                            translationY = if (isDragged) dragOffset else 0f
                            shadowElevation = if (isDragged) 8f else 0f
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .pointerInput(k) {
                                detectDragGestures(
                                    onDragStart = { dragging = k; dragOffset = 0f },
                                    onDrag = { change, amount ->
                                        change.consume()
                                        dragOffset += amount.y
                                        val from = order.indexOf(k)
                                        val to = (from + (dragOffset / rowPx).roundToInt()).coerceIn(0, order.lastIndex)
                                        if (to != from) {
                                            order = order.toMutableList().apply { removeAt(from); add(to, k) }
                                            dragOffset -= (to - from) * rowPx
                                        }
                                    },
                                    onDragEnd = { dragging = null; dragOffset = 0f; onReordered(order) },
                                    onDragCancel = { dragging = null; dragOffset = 0f; onReordered(order) }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("≡", style = MaterialTheme.typography.titleLarge, color = if (isDragged) OsrsGold else OsrsTextMuted)
                    }
                    Checkbox(
                        checked = k !in hidden,
                        onCheckedChange = { onShownChanged(k, it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = OsrsGold,
                            uncheckedColor = OsrsBorderOutline,
                            checkmarkColor = Color.Black
                        )
                    )
                    Text(
                        text = labels[k].orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (k in hidden) OsrsTextMuted else OsrsTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            }
        }
    }
}
