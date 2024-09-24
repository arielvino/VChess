package net.av.vchess.android.ui.layout

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import net.av.vchess.R
import net.av.vchess.network.data.GameInformerData

class GameOfferView(private val context: Context, data: GameInformerData) : FrameLayout(context) {
    init {
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.HORIZONTAL
        layout.background = context.getDrawable(R.drawable.game_offer_background)
        layout.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layout.setOnClickListener { performClick() }
        layout.setPadding(15, 15, 15, 15)
        addView(layout)

        val nameLabel = TextView(context)
        nameLabel.text = context.getString(R.string.lobby_name, data.gameName)
        nameLabel.setOnClickListener { performClick() }
        nameLabel.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        (nameLabel.layoutParams as LinearLayout.LayoutParams).weight = 1f
        layout.addView(nameLabel)

        val colorLabel = ImageView(context)
        colorLabel.setOnClickListener { performClick() }
        colorLabel.scaleType = ImageView.ScaleType.CENTER_INSIDE
        when (data.recipientColor) {
            HostGameDialog.MyColorSetting.White ->
                colorLabel.setImageResource(R.drawable.w_pawn)

            HostGameDialog.MyColorSetting.Black ->
                colorLabel.setImageResource(R.drawable.b_pawn)

            HostGameDialog.MyColorSetting.Random ->
                colorLabel.setImageResource(R.drawable.random_symbol)
        }
        colorLabel.layoutParams = LinearLayout.LayoutParams(mmToPx(10f), mmToPx(10f))
        layout.addView(colorLabel)
    }

    private fun mmToPx(mm: Float): Int {
        val displayMetrics = context.resources.displayMetrics
        val physicalScreenWidthMm = displayMetrics.widthPixels / (displayMetrics.xdpi / 25.4f)
        val physicalScreenHeightMm = displayMetrics.heightPixels / (displayMetrics.ydpi / 25.4f)
        return when {
            displayMetrics.widthPixels > displayMetrics.heightPixels -> {
                (mm / physicalScreenWidthMm * displayMetrics.widthPixels).toInt()
            }
            else -> {
                (mm / physicalScreenHeightMm * displayMetrics.heightPixels).toInt()
            }
        }
    }
}