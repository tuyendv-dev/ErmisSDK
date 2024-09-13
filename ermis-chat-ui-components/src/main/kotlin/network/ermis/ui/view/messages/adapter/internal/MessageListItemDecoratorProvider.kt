
package network.ermis.ui.view.messages.adapter.internal

import network.ermis.core.models.Channel
import network.ermis.ui.common.helper.DateFormatter
import network.ermis.ui.common.state.messages.list.DeletedMessageVisibility
import network.ermis.ui.common.utils.extensions.isDirectMessaging
import network.ermis.ui.view.MessageListView
import network.ermis.ui.view.messages.MessageListViewStyle
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.DecoratorProvider
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.AvatarDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.BackgroundDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.FailedIndicatorDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.FootnoteDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.GapDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.MaxPossibleWidthDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.MessageContainerMarginDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.PinIndicatorDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.ReactionsDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.ReplyDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.TextDecorator
import network.ermis.ui.view.messages.background.MessageBackgroundFactory
import network.ermis.ui.utils.extensions.isCurrentUserBanned

/**
 * Provides all decorators that will be used in MessageListView items.
 *
 * @param channel [Channel].
 * @param dateFormatter [DateFormatter]. Formats the dates in the messages.
 * @param messageListViewStyle [MessageListViewStyle] The style of the MessageListView and its items.
 * @param showAvatarPredicate [MessageListView.ShowAvatarPredicate] Checks if should show the avatar or not accordingly with the provided logic.
 * @param messageBackgroundFactory [MessageBackgroundFactory] Factory that customizes the background of messages.
 * @param deletedMessageVisibility [DeletedMessageVisibility] Used to hide or show the the deleted message accordingly to the logic provided.
 */
@Suppress("LongParameterList")
internal class MessageListItemDecoratorProvider(
    channel: Channel,
    dateFormatter: DateFormatter,
    messageListViewStyle: MessageListViewStyle,
    showAvatarPredicate: MessageListView.ShowAvatarPredicate,
    messageBackgroundFactory: MessageBackgroundFactory,
    deletedMessageVisibility: () -> DeletedMessageVisibility,
    getLanguageDisplayName: (code: String) -> String,
    decoratorPredicate: (Decorator) -> Boolean,
) : DecoratorProvider {

    override val decorators: List<Decorator> by lazy {
        listOfNotNull<Decorator>(
            BackgroundDecorator(messageBackgroundFactory),
            TextDecorator(messageListViewStyle.itemStyle),
            GapDecorator(),
            MaxPossibleWidthDecorator(messageListViewStyle.itemStyle),
            MessageContainerMarginDecorator(messageListViewStyle.itemStyle),
            AvatarDecorator(showAvatarPredicate),
            FailedIndicatorDecorator(messageListViewStyle.itemStyle) { channel.isCurrentUserBanned() },
            ReactionsDecorator(messageListViewStyle.itemStyle).takeIf { messageListViewStyle.reactionsEnabled },
            ReplyDecorator(messageListViewStyle.replyMessageStyle),
            FootnoteDecorator(
                dateFormatter,
                { channel.isDirectMessaging() },
                { channel.config.isThreadEnabled },
                messageListViewStyle,
                deletedMessageVisibility,
                getLanguageDisplayName,
            ),
            PinIndicatorDecorator(messageListViewStyle.itemStyle).takeIf { messageListViewStyle.pinMessageEnabled },
        ).filter(decoratorPredicate)
    }
}
