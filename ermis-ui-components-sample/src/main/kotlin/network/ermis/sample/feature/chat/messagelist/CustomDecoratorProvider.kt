package network.ermis.sample.feature.chat.messagelist

import network.ermis.core.models.Channel
import network.ermis.ui.common.helper.DateFormatter
import network.ermis.ui.common.state.messages.list.DeletedMessageVisibility
import network.ermis.ui.view.MessageListView
import network.ermis.ui.view.messages.MessageListViewStyle
import network.ermis.ui.view.messages.adapter.viewholder.decorator.DecoratorProvider
import network.ermis.ui.view.messages.adapter.viewholder.decorator.DecoratorProviderFactory
import network.ermis.ui.view.messages.background.MessageBackgroundFactory

class CustomDecoratorProviderFactory : DecoratorProviderFactory {
    override fun createDecoratorProvider(
        channel: Channel,
        dateFormatter: DateFormatter,
        messageListViewStyle: MessageListViewStyle,
        showAvatarPredicate: MessageListView.ShowAvatarPredicate,
        messageBackgroundFactory: MessageBackgroundFactory,
        deletedMessageVisibility: () -> DeletedMessageVisibility,
        getLanguageDisplayName: (code: String) -> String,
    ): DecoratorProvider = CustomDecoratorProvider(
        channel,
        dateFormatter,
        messageListViewStyle,
        showAvatarPredicate,
        messageBackgroundFactory,
        deletedMessageVisibility,
        getLanguageDisplayName,
    )
}

class CustomDecoratorProvider(
    channel: Channel,
    dateFormatter: DateFormatter,
    messageListViewStyle: MessageListViewStyle,
    showAvatarPredicate: MessageListView.ShowAvatarPredicate,
    messageBackgroundFactory: MessageBackgroundFactory,
    deletedMessageVisibility: () -> DeletedMessageVisibility,
    getLanguageDisplayName: (code: String) -> String,
) : DecoratorProvider {
    override val decorators by lazy {
        listOf(ForwardedDecorator())
    }
}
