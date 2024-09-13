
package network.ermis.ui.view.messages.background

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import network.ermis.ui.components.R
import network.ermis.ui.utils.extensions.dpToPxPrecise
import network.ermis.ui.utils.extensions.isRtlLayout

/**
 * Class that creates the default version of ShapeAppearanceModel used in the background of messages, attachments, pictures...
 */
internal object ShapeAppearanceModelFactory {

    private val CORNER_SIZE_FILE_PX = 16.dpToPxPrecise()
    private val CORNER_SIZE_AUDIO_PX = 14.dpToPxPrecise()
    private val STROKE_WIDTH_PX = 1.dpToPxPrecise()

    /**
     * Creates the ShapeAppearanceModel.
     *
     * @param context [Context].
     * @param defaultCornerRadius The corner radius of all corners with the exception of one.
     * @param differentCornerRadius The corner radius of one of the corners accordingly with the logic on the method.
     * @param isMine Whether the message is from the current user or not. Used to position the differentCorner.
     * @param isBottomPosition Whether the message the bottom position or not. Used to position the differentCorner.
     */
    fun create(
        context: Context,
        defaultCornerRadius: Float,
        differentCornerRadius: Float,
        isMine: Boolean,
        isBottomPosition: Boolean,
    ): ShapeAppearanceModel {
        return ShapeAppearanceModel.builder()
            .setAllCornerSizes(defaultCornerRadius)
            .apply {
                if (isBottomPosition) {
                    val isRtl = context.isRtlLayout

                    when {
                        !isRtl && isMine -> setBottomRightCornerSize(differentCornerRadius)

                        !isRtl && !isMine -> setBottomLeftCornerSize(differentCornerRadius)

                        isRtl && isMine -> setBottomLeftCornerSize(differentCornerRadius)

                        isRtl && !isMine -> setBottomRightCornerSize(differentCornerRadius)
                    }
                }
            }
            .build()
    }

    fun fileBackground(context: Context): MaterialShapeDrawable = ShapeAppearanceModel.builder()
        .setAllCornerSizes(CORNER_SIZE_FILE_PX)
        .build()
        .let(::MaterialShapeDrawable)
        .apply {
            setStroke(
                STROKE_WIDTH_PX,
                ContextCompat.getColor(context, R.color.ui_grey_whisper),
            )
            setTint(ContextCompat.getColor(context, R.color.ui_white))
        }

    fun audioBackground(context: Context): MaterialShapeDrawable = ShapeAppearanceModel.builder()
        .setAllCornerSizes(CORNER_SIZE_AUDIO_PX)
        .build()
        .let(::MaterialShapeDrawable)
        .apply {
            setStroke(
                STROKE_WIDTH_PX,
                ContextCompat.getColor(context, R.color.ui_grey_whisper),
            )
            setTint(ContextCompat.getColor(context, R.color.ui_white))
        }
}
