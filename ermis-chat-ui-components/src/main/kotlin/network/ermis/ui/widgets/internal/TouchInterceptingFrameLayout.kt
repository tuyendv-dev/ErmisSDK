
package network.ermis.ui.widgets.internal

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import network.ermis.ui.utils.extensions.createStreamThemeWrapper

/**
 * A [FrameLayout] that consumes all touch screen motion events.
 */
internal class TouchInterceptingFrameLayout : FrameLayout {

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr,
    )

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }
}
