
package network.ermis.ui.view.messages.background

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import network.ermis.core.models.Attachment
import network.ermis.ui.components.R
import network.ermis.ui.view.messages.MessageListItemStyle
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.utils.extensions.dpToPxPrecise
import network.ermis.ui.utils.extensions.isBottomPosition
import network.ermis.ui.utils.extension.hasLink

/**
 * Default drawer of background of message items.
 */
public open class MessageBackgroundFactoryImpl(private val style: MessageListItemStyle) : MessageBackgroundFactory {

    /**
     * Draws the background of plain text messages.
     *
     * @param context [Context].
     * @param data [MessageListItem.MessageItem].
     */
    override fun plainTextMessageBackground(context: Context, data: MessageListItem.MessageItem): Drawable {
        return defaultBackground(context, data)
    }

    /**
     * Draws the background of messages containing image attachments.
     *
     * @param context [Context].
     * @param data [MessageListItem.MessageItem].
     */
    override fun imageAttachmentMessageBackground(context: Context, data: MessageListItem.MessageItem): Drawable {
        return defaultBackground(context, data)
    }

    /**
     * Draws the background of deleted messages.
     *
     * @param context [Context].
     * @param data [MessageListItem.MessageItem].
     */
    override fun deletedMessageBackground(context: Context, data: MessageListItem.MessageItem): Drawable {
        return shapeAppearanceModel(context, DEFAULT_CORNER_RADIUS, 0F, data.isMine, data.isBottomPosition())
            .let(::MaterialShapeDrawable)
            .apply {
                setTint(
                    when (data.isTheirs) {
                        true -> style.messageDeletedBackgroundTheirs
                        else -> style.messageDeletedBackgroundMine
                    } ?: style.messageDeletedBackground,
                )
            }
    }

    /**
     * Draws the background of text and attachment messages.
     *
     * @param context [Context].
     * @param data [MessageListItem.MessageItem].
     */
    override fun textAndAttachmentMessageBackground(context: Context, data: MessageListItem.MessageItem): Drawable {
        return defaultBackground(context, data)
    }

    /**
     * Draws the background of messages containing file attachments.
     *
     * @param context [Context].
     * @param data [MessageListItem.MessageItem].
     */
    override fun fileAttachmentsMessageBackground(context: Context, data: MessageListItem.MessageItem): Drawable {
        return defaultBackground(context, data)
    }

    /**
     * Draws the background of messages containing links and no other types of attachments.
     *
     * @param context [Context].
     * @param data [MessageListItem.MessageItem].
     */
    override fun linkAttachmentMessageBackground(context: Context, data: MessageListItem.MessageItem): Drawable {
        return defaultBackground(context, data)
    }

    /**
     * Draws the default background of messages.
     *
     * @param context [Context].
     * @param data [MessageListItem.MessageItem].
     */
    private fun defaultBackground(context: Context, data: MessageListItem.MessageItem): Drawable {
        val shapeAppearanceModel =
            shapeAppearanceModel(context, DEFAULT_CORNER_RADIUS, 0F, data.isMine, data.isBottomPosition())

        return MaterialShapeDrawable(shapeAppearanceModel).apply {
            val hasLink = data.message.attachments.any(Attachment::hasLink)
            if (data.isMine) {
                paintStyle = Paint.Style.FILL_AND_STROKE
                setStrokeTint(style.messageStrokeColorMine)
                strokeWidth = style.messageStrokeWidthMine
                // for messages with links, we use a different background color than other messages by default.
                // however, if a user has specified a background color attribute, we use it for _all_ message backgrounds.
                val backgroundTintColor = if (hasLink) {
                    style.messageLinkBackgroundColorMine
                } else {
                    style.messageBackgroundColorMine ?: ContextCompat.getColor(
                        context,
                        MESSAGE_CURRENT_USER_BACKGROUND,
                    )
                }

                setTint(backgroundTintColor)
            } else {
                paintStyle = Paint.Style.FILL_AND_STROKE
                setStrokeTint(style.messageStrokeColorTheirs)
                strokeWidth = style.messageStrokeWidthTheirs

                val backgroundTintColor = if (hasLink) {
                    style.messageLinkBackgroundColorTheirs
                } else {
                    style.messageBackgroundColorTheirs ?: ContextCompat.getColor(
                        context,
                        MESSAGE_OTHER_USER_BACKGROUND,
                    )
                }

                setTint(backgroundTintColor)
            }
        }
    }

    /**
     * Draws the drawable to the used as background for Giphys.
     */
    override fun giphyAppearanceModel(context: Context): Drawable {
        return shapeAppearanceModel(
            context,
            DEFAULT_CORNER_RADIUS,
            SMALL_CARD_VIEW_CORNER_RADIUS,
            isMine = true,
            isBottomPosition = true,
        )
            .let(::MaterialShapeDrawable)
            .apply {
                setTint(ContextCompat.getColor(context, MESSAGE_OTHER_USER_BACKGROUND))
            }
    }

    public companion object {
        private val MESSAGE_OTHER_USER_BACKGROUND = R.color.ui_white
        private val MESSAGE_CURRENT_USER_BACKGROUND = R.color.ui_grey_gainsboro
        private val SMALL_CARD_VIEW_CORNER_RADIUS = 2.dpToPxPrecise()

        internal val DEFAULT_CORNER_RADIUS = 16.dpToPxPrecise()
    }

    private fun shapeAppearanceModel(
        context: Context,
        radius: Float,
        bottomEndCorner: Float,
        isMine: Boolean,
        isBottomPosition: Boolean,
    ): ShapeAppearanceModel {
        return ShapeAppearanceModelFactory.create(context, radius, bottomEndCorner, isMine, isBottomPosition)
    }
}
