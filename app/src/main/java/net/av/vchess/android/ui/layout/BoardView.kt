package net.av.vchess.android.ui.layout

import android.content.Context
import android.content.res.Configuration
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.core.view.setPadding
import net.av.vchess.android.viewmodels.UnresponsiveBoardViewModel
import net.av.vchess.android.viewmodels.UnresponsiveTileViewModel
import net.av.vchess.game.data.Board
import net.av.vchess.reusables.PlayerColor
import kotlin.math.max
import kotlin.properties.Delegates

class BoardView(
    context: Context,
    private val board: Board,
    val boardViewModel: UnresponsiveBoardViewModel
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

                val model: UnresponsiveTileViewModel =
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

                val boardSize: Int
                val orientation = resources.configuration.orientation

                boardSize = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    width
                } else {
                    height
                }

                val numberOfCells = max(board.height, board.width)
                tileSize = boardSize / numberOfCells

                for (x in 0.until(board.width)) {
                    for (y in 0.until(board.height)) {
                        val tile = tiles[x][y]
                        val layoutParams = tile.layoutParams as GridLayout.LayoutParams
                        layoutParams.width = tileSize
                        layoutParams.height = tileSize
                        tile.setPadding((tileSize * 0.10).toInt())
                    }
                }
            }
        })
    }

    var pointOfView: PlayerColor = PlayerColor.White
        set(value) {
            field = value
            for (x in 0.until(board.width)) {
                for (y in 0.until(board.height)) {
                    val tile = tiles[x][y]
                    val layoutParams = tile.layoutParams as GridLayout.LayoutParams
                    if (field == PlayerColor.White) {
                        layoutParams.rowSpec = GridLayout.spec(board.height - 1 - y)
                        layoutParams.columnSpec = GridLayout.spec(x)
                    } else {
                        layoutParams.rowSpec = GridLayout.spec(y)
                        layoutParams.columnSpec = GridLayout.spec(board.width - 1 - x)
                    }
                }
            }
        }
}