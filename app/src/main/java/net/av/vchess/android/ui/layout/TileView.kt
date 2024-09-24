package net.av.vchess.android.ui.layout

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import net.av.vchess.android.UnresponsiveTileViewModel


/**
 * TODO: document your custom view class.
 */
class TileView : FrameLayout, UnresponsiveTileViewModel.ChangesListener {

    private val border: View = this
    private lateinit var body: ImageView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private fun init() {
        body = ImageView(context)
        body
        addView(body)
    }

    override fun onBorderColorChanged(colorId: Int) {
        border.setBackgroundColor(context.getColor(colorId))
    }

    override fun onBodyColorChanged(colorId: Int) {
        body.setBackgroundColor(context.getColor(colorId))
    }

    override fun onImageChanged(drawable: Drawable?) {
            body.setImageDrawable(drawable)
    }
}