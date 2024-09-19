package net.av.vchess.android.ui.layout

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.setPadding
import net.av.vchess.android.TileViewModel


/**
 * TODO: document your custom view class.
 */
class TileView : FrameLayout, TileViewModel.ChangesListener {

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
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to avoid multiple callbacks
                viewTreeObserver.removeOnGlobalLayoutListener(this)

                setPadding((width * 0.10).toInt())
            }
        })
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