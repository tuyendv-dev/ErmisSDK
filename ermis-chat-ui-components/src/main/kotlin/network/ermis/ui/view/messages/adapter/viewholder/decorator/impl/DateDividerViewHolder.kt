
package network.ermis.ui.view.messages.adapter.viewholder.decorator.impl

import android.text.format.DateUtils
import android.view.ViewGroup
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import network.ermis.ui.components.databinding.ItemDateDividerBinding
import network.ermis.ui.view.messages.MessageListItemStyle
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemPayloadDiff
import network.ermis.ui.view.messages.adapter.internal.DecoratedBaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.dpToPxPrecise
import network.ermis.ui.utils.extensions.streamThemeInflater

/**
 * Represents the date divider holder.
 */
public class DateDividerViewHolder internal constructor(
    parent: ViewGroup,
    decorators: List<Decorator>,
    public val style: MessageListItemStyle,
    public val binding: ItemDateDividerBinding = ItemDateDividerBinding.inflate(
        parent.streamThemeInflater,
        parent,
        false,
    ),
) : DecoratedBaseMessageItemViewHolder<MessageListItem.DateSeparatorItem>(binding.root, decorators) {

    override fun bindData(data: MessageListItem.DateSeparatorItem, diff: MessageListItemPayloadDiff?) {
        super.bindData(data, diff)

        binding.dateLabel.text =
            DateUtils.getRelativeTimeSpanString(
                data.date.time,
                System.currentTimeMillis(),
                DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE,
            )

        binding.dateLabel.setTextStyle(style.textStyleDateSeparator)
        binding.dateLabel.background = ShapeAppearanceModel.Builder().setAllCornerSizes(DEFAULT_CORNER_RADIUS).build()
            .let(::MaterialShapeDrawable).apply { setTint(style.dateSeparatorBackgroundColor) }
    }

    private companion object {
        private val DEFAULT_CORNER_RADIUS = 16.dpToPxPrecise()
    }
}
