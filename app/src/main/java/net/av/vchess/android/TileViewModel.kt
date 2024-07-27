package net.av.vchess.android

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import net.av.vchess.game.data.Tile
import net.av.vchess.R
import net.av.vchess.game.data.turn.CaptureAction
import net.av.vchess.game.data.turn.MoveAction
import net.av.vchess.game.data.turn.TurnInfo
import net.av.vchess.reusables.PlayerColor


class TileViewModel(private val tile: Tile, private val board: BoardViewModel) :
    Tile.ChangesListener {
    init {
        tile.addChangesListener(this)
    }

    @ColorRes
    var borderColor: Int = defaultColor()
        private set(value) {
            field = value
            listener?.onBorderColorChanged(value)
        }

    @ColorRes
    var bodyColor: Int = defaultColor()
        private set(value) {
            field = value
            listener?.onBodyColorChanged(value)
        }

    var pieceImage: Drawable? = null
        private set(value) {
            field = value
            listener?.onImageChanged(value)
        }

    val turnsInfo: ArrayList<TurnInfo> = arrayListOf()
    fun addTurn(turn: TurnInfo) {
        turnsInfo.add(turn)
        refresh()
    }

    fun clearTurns() {
        turnsInfo.clear()
        refresh()
    }

    @ColorRes
    fun defaultColor(): Int {
        if (isDark()) {
            return R.color.tile_dark
        } else {
            return R.color.tile_light
        }
    }

    fun isDark(): Boolean {
        return (tile.location.x + tile.location.y) % 2 == 0
    }

    fun onClick() {
        //todoy
        if (turnsInfo.size == 1) {
            board.game.performTurn(turnsInfo[0])
            board.cleanTargets()
            board.unselectAll()

            return
        }
        if (tile.piece != null) {
            if (tile.piece!!.color == board.game.currentTurn) {
                if (board.selectedTile == this) {
                    board.unselectAll()

                    return
                } else {
                    select()

                    return
                }
            }
        }
    }

    private fun select() {
        //first make the previous selected tile unselected:
        board.unselectAll()

        if (tile.piece != null) {
            board.selectedTile = this

            //TEST
            val turns = board.game.gameRuler.getLegalTurnsFrom(tile.piece!!.listUnfilteredPossibleTurns())
            for (turn in turns) {
                for (action in turn.actions) {
                    if (action is MoveAction) {
                        board.getTile(action.destination).addTurn(turn)
                        break
                    }
                    if (action is CaptureAction) {
                        board.getTile(action.origin).addTurn(turn)
                        break
                    }

                    //ToDo: add support for more Actions
                }
            }
            //TEST
        }
    }

    @ColorRes
    private fun targetColor(): Int {
        return if (isDark()) {
            R.color.tile_target_dark
        } else {
            R.color.tile_target_light
        }
    }

    fun refresh() {
        //determine body color:
        if (turnsInfo.isNotEmpty()) {
            bodyColor = targetColor()
        } else if (board.selectedTile == this) {
            bodyColor = R.color.tile_selected
        } else {
            bodyColor = defaultColor()
        }

        //determine border color:
        if (tile.ruledTraversability == Tile.Traversability.Blocked) {
            borderColor = R.color.tile_blocked
        }
        if (tile.ruledTraversability == Tile.Traversability.OnlyPass) {
            borderColor = R.color.tile_only_pass
        }
        if (tile.ruledTraversability == Tile.Traversability.Peaceful) {
            borderColor = R.color.tile_peaceful
        }
        if (tile.ruledTraversability == Tile.Traversability.Normal) {
            //if traversability is normal, the border color should match body color:
            borderColor = bodyColor
        }

        if (tile.piece == null) {
            pieceImage = null
        } else {
            val colorLetter = if (tile.piece!!.color == PlayerColor.Black) "b" else "w"
            val typeName = tile.piece!!.javaClass.simpleName.lowercase()
            val imageFileName = "${colorLetter}_${typeName}.png"

            val drawable = getDrawableFromAsset(App.appContext, imageFileName)

            pieceImage = drawable
        }
    }

    var listener: ChangesListener? = null

    interface ChangesListener {
        fun onBorderColorChanged(@ColorRes colorId: Int)
        fun onBodyColorChanged(@ColorRes colorId: Int)
        fun onImageChanged(drawable: Drawable?)
    }

    fun getDrawableFromAsset(context: Context, fileName: String): Drawable? {
        return try {
            val assetManager = context.assets
            val inputStream = assetManager.open(fileName)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            BitmapDrawable(context.resources, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onTraversabilityChanged() {
        refresh()
    }

    override fun onPieceChanged() {
        refresh()
    }
}