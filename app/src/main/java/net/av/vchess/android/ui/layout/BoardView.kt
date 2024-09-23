package net.av.vchess.android.ui.layout

import android.content.Context
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.core.view.setPadding
import net.av.vchess.android.SimpleBoardViewModel
import net.av.vchess.android.SimpleTileViewModel
import net.av.vchess.game.data.Board
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

class BoardView(
    context: Context,
    private val board: Board,
    val boardViewModel: SimpleBoardViewModel
) : FrameLayout(context) {
    val tiles: ArrayList<ArrayList<TileView>> = arrayListOf()
    var tileSize by Delegates.notNull<Int>()

    init {
        val gridLayout = GridLayout(context)
        gridLayout.columnCount = board.width
        gridLayout.rowCount = board.height
        gridLayout.layoutDirection = LAYOUT_DIRECTION_LTR
        addView(gridLayout)

        for (x in 0.until(board.height)) {
            tiles.add(arrayListOf())
            for (y in 0.until(board.width)) {
                val tile = TileView(context)

                gridLayout.addView(tile)

                val model: SimpleTileViewModel =
                    boardViewModel.getTile(x, y)
                model.listener = tile
                model.refresh()

                tile.setOnClickListener {
                    boardViewModel.getTile(x, y).onClick()
                }
                tiles[x].add(tile)
            }
        }
        setTileSize()
    }


    private fun setTileSize() {
        // Wait for the parent view to be laid out before calculating the tile size
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)

                val boardWidth = min(width, height)
                val numberOfCells = max(board.height, board.width)
                tileSize = boardWidth / numberOfCells

                for (x in 0.until(board.width)) {
                    for (y in 0.until(board.height)) {
                        val tile = tiles[x][y]
                        val layoutParams = GridLayout.LayoutParams()
                        layoutParams.width = tileSize
                        layoutParams.height = tileSize
                        layoutParams.rowSpec = GridLayout.spec(board.height - 1 - y)
                        layoutParams.columnSpec = GridLayout.spec(x)
                        tile.layoutParams = layoutParams
                        tile.setPadding((tileSize * 0.10).toInt())
                    }
                }
            }
        })
    }
}