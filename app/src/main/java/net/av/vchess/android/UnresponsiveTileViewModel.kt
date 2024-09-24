package net.av.vchess.android

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import net.av.vchess.R
import net.av.vchess.game.data.Tile
import net.av.vchess.reusables.PlayerColor

open class UnresponsiveTileViewModel(
    val tile: Tile,
    open val board: UnresponsiveBoardViewModel
) : Tile.ChangesListener {
    companion object {
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
    }

    @ColorRes
    var borderColor: Int = defaultColor()
        set(value) {
            field = value
            listener?.onBorderColorChanged(value)
        }

    @ColorRes
    var bodyColor: Int = defaultColor()
        set(value) {
            field = value
            listener?.onBodyColorChanged(value)
        }

    private var pieceImage: Drawable? = null
        private set(value) {
            field = value
            listener?.onImageChanged(value)
        }

    @ColorRes
    fun defaultColor(): Int {
        return if (isDark()) {
            R.color.tile_dark
        } else {
            R.color.tile_light
        }
    }

    fun isDark(): Boolean {
        return (tile.location.x + tile.location.y) % 2 == 0
    }

    open fun refresh() {
        //determine body color:
        bodyColor = defaultColor()

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

    override fun onTraversabilityChanged() {
        refresh()
    }

    override fun onPieceChanged() {
        refresh()
    }

    open fun onClick() {}


    var listener: ChangesListener? = null


    interface ChangesListener {
        fun onBorderColorChanged(@ColorRes colorId: Int)
        fun onBodyColorChanged(@ColorRes colorId: Int)
        fun onImageChanged(drawable: Drawable?)
    }

}