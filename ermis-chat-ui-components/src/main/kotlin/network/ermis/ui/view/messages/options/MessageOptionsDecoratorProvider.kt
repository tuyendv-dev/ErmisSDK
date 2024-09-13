
package network.ermis.ui.view.messages.options

import network.ermis.ui.view.messages.MessageListItemStyle
import network.ermis.ui.view.MessageListView
import network.ermis.ui.view.messages.MessageReplyStyle
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.DecoratorProvider
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.AvatarDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.BackgroundDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.MaxPossibleWidthDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.MessageContainerMarginDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.ReplyDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.TextDecorator
import network.ermis.ui.view.messages.background.MessageBackgroundFactory

internal class MessageOptionsDecoratorProvider(
    messageListItemStyle: MessageListItemStyle,
    messageReplyStyle: MessageReplyStyle,
    messageBackgroundFactory: MessageBackgroundFactory,
    showAvatarPredicate: MessageListView.ShowAvatarPredicate,
) : DecoratorProvider {

    private val messageOptionsDecorators = listOf<Decorator>(
        BackgroundDecorator(messageBackgroundFactory),
        TextDecorator(messageListItemStyle),
        MaxPossibleWidthDecorator(messageListItemStyle),
        MessageContainerMarginDecorator(messageListItemStyle),
        AvatarDecorator(showAvatarPredicate),
        ReplyDecorator(messageReplyStyle),
    )

    override val decorators: List<Decorator> = messageOptionsDecorators
}
