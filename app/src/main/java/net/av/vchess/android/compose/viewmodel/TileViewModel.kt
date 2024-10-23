package net.av.vchess.android.compose.viewmodel

import net.av.vchess.game.data.Tile

open class TileViewModel(
    val tile: Tile,
    open val board: BoardViewModel
) : Tile.ChangesListener {

    fun refresh() {
        listener?.onChanged()
    }

    var listener: ChangesListener? = null


    interface ChangesListener {
        fun onChanged()
    }

    override fun onTraversabilityChanged() {
        refresh()
    }

    override fun onPieceChanged() {
        refresh()
    }
}