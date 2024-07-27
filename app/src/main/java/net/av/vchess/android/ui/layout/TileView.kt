package net.av.vchess.android.ui.layout

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import net.av.vchess.android.TileViewModel


/**
 * TODO: document your custom view class.
 */
class TileView : FrameLayout, TileViewModel.ChangesListener {

    private val border: View = this
    private lateinit var body: ImageView

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        body = ImageView(context)
        body
        addView(body)
        setPadding(18)
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