package git.shin.animevsub.ui.components.player.settings

import androidx.compose.runtime.Composable

@Composable
fun FlowRow(spacing: androidx.compose.ui.unit.Dp, content: @Composable () -> Unit) {
  androidx.compose.ui.layout.Layout(content = content) { measurables, constraints ->
    val gap = spacing.roundToPx()
    val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0)) }
    var rowWidth = 0
    val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
    var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
    placeables.forEach { placeable ->
      if (rowWidth + placeable.width + gap > constraints.maxWidth && currentRow.isNotEmpty()) {
        rows.add(currentRow); rowWidth = 0; currentRow = mutableListOf()
      }
      currentRow.add(placeable); rowWidth += placeable.width + gap
    }
    if (currentRow.isNotEmpty()) rows.add(currentRow)
    val totalHeight = rows.sumOf { row -> row.maxOf { it.height } + gap }
    layout(constraints.maxWidth, totalHeight) {
      var y = 0
      rows.forEach { row ->
        var x = 0
        val maxHeight = row.maxOf { it.height }
        row.forEach { placeable -> placeable.placeRelative(x, y); x += placeable.width + gap }
        y += maxHeight + gap
      }
    }
  }
}
