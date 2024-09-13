
package network.ermis.ui.view.messages.adapter.viewholder.decorator.impl

import android.content.res.ColorStateList
import android.view.ViewGroup
import androidx.core.view.isVisible
import network.ermis.ui.common.state.messages.list.CancelGiphy
import network.ermis.ui.common.state.messages.list.SendGiphy
import network.ermis.ui.common.state.messages.list.ShuffleGiphy
import network.ermis.ui.common.utils.GiphyInfoType
import network.ermis.ui.common.utils.extensions.imagePreviewUrl
import network.ermis.ui.common.utils.giphyInfo
import network.ermis.ui.components.databinding.ItemMessageGiphyBinding
import network.ermis.ui.view.messages.GiphyViewHolderStyle
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemPayloadDiff
import network.ermis.ui.view.messages.adapter.MessageListListeners
import network.ermis.ui.view.messages.adapter.internal.DecoratedBaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.streamThemeInflater
import network.ermis.ui.utils.load

public class GiphyViewHolder internal constructor(
    parent: ViewGroup,
    decorators: List<Decorator>,
    listeners: MessageListListeners?,
    public val style: GiphyViewHolderStyle,
    public val binding: ItemMessageGiphyBinding = ItemMessageGiphyBinding.inflate(
        parent.streamThemeInflater,
        parent,
        false,
    ),
) : DecoratedBaseMessageItemViewHolder<MessageListItem.MessageItem>(binding.root, decorators) {

    init {
        binding.run {
            listeners?.let { container ->
                cancelButton.setOnClickListener {
                    container.giphySendListener.onGiphySend(CancelGiphy(data.message))
                }
                shuffleButton.setOnClickListener {
                    container.giphySendListener.onGiphySend(ShuffleGiphy(data.message))
                }
                sendButton.setOnClickListener {
                    container.giphySendListener.onGiphySend(SendGiphy(data.message))
                }
            }
        }
    }

    override fun bindData(data: MessageListItem.MessageItem, diff: MessageListItemPayloadDiff?) {
        super.bindData(data, diff)

        applyStyle()

        data.message
            .attachments
            .firstOrNull()
            ?.let {
                val url = it.giphyInfo(GiphyInfoType.FIXED_HEIGHT)?.url ?: it.let {
                    it.imagePreviewUrl ?: it.titleLink ?: it.ogUrl
                } ?: return

                binding.giphyPreview.load(
                    data = url,
                    onStart = {
                        binding.loadingProgressBar.isVisible = true
                    },
                    onComplete = {
                        binding.loadingProgressBar.isVisible = false
                    },
                )
            }

        binding.giphyQueryTextView.text = data.message
            .text
            .replace(GIPHY_PREFIX, "")
    }

    private fun applyStyle() {
        binding.apply {
            cardView.backgroundTintList = ColorStateList.valueOf(style.cardBackgroundColor)
            cardView.elevation = style.cardElevation

            horizontalDivider.setBackgroundColor(style.cardButtonDividerColor)
            verticalDivider1.setBackgroundColor(style.cardButtonDividerColor)
            verticalDivider2.setBackgroundColor(style.cardButtonDividerColor)

            giphyIconImageView.setImageDrawable(style.giphyIcon)

            giphyLabelTextView.setTextStyle(style.labelTextStyle)
            giphyQueryTextView.setTextStyle(style.queryTextStyle)
            cancelButton.setTextStyle(style.cancelButtonTextStyle)
            shuffleButton.setTextStyle(style.shuffleButtonTextStyle)
            sendButton.setTextStyle(style.sendButtonTextStyle)
        }
    }

    private companion object {
        private const val GIPHY_PREFIX = "/giphy "
    }
}
