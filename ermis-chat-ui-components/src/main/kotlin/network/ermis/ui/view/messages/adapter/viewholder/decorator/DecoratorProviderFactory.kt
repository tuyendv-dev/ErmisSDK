package network.ermis.ui.view.messages.adapter.viewholder.decorator

import network.ermis.core.models.Channel
import network.ermis.ui.common.helper.DateFormatter
import network.ermis.ui.common.state.messages.list.DeletedMessageVisibility
import network.ermis.ui.view.MessageListView
import network.ermis.ui.view.messages.MessageListViewStyle
import network.ermis.ui.view.messages.adapter.internal.MessageListItemDecoratorProvider
import network.ermis.ui.view.messages.background.MessageBackgroundFactory

/**
 * A factory responsible for creating [DecoratorProvider]s
 * to be used in [io.getstream.chat.android.ui.feature.messages.list.MessageListView].
 */
public interface DecoratorProviderFactory {

    /**
     * Creates a new [DecoratorProvider] for the given [channel].
     */
    public fun createDecoratorProvider(
        channel: Channel,
        dateFormatter: DateFormatter,
        messageListViewStyle: MessageListViewStyle,
        showAvatarPredicate: MessageListView.ShowAvatarPredicate,
        messageBackgroundFactory: MessageBackgroundFactory,
        deletedMessageVisibility: () -> DeletedMessageVisibility,
        getLanguageDisplayName: (code: String) -> String,
    ): DecoratorProvider

    public companion object {

        /**
         * Creates the default [DecoratorProviderFactory].
         */
        @JvmStatic
        public fun defaultFactory(
            predicate: (Decorator) -> Boolean = { true },
        ): DecoratorProviderFactory = object : DecoratorProviderFactory {
            override fun createDecoratorProvider(
                channel: Channel,
                dateFormatter: DateFormatter,
                messageListViewStyle: MessageListViewStyle,
                showAvatarPredicate: MessageListView.ShowAvatarPredicate,
                messageBackgroundFactory: MessageBackgroundFactory,
                deletedMessageVisibility: () -> DeletedMessageVisibility,
                getLanguageDisplayName: (code: String) -> String,
            ): DecoratorProvider {
                return MessageListItemDecoratorProvider(
                    channel,
                    dateFormatter,
                    messageListViewStyle,
                    showAvatarPredicate,
                    messageBackgroundFactory,
                    deletedMessageVisibility,
                    getLanguageDisplayName,
                    predicate,
                )
            }
        }
    }
}

/**
 * Combines two [DecoratorProviderFactory]s into a single [DecoratorProviderFactory].
 */
public operator fun DecoratorProviderFactory.plus(
    other: DecoratorProviderFactory,
): DecoratorProviderFactory = object : DecoratorProviderFactory {
    override fun createDecoratorProvider(
        channel: Channel,
        dateFormatter: DateFormatter,
        messageListViewStyle: MessageListViewStyle,
        showAvatarPredicate: MessageListView.ShowAvatarPredicate,
        messageBackgroundFactory: MessageBackgroundFactory,
        deletedMessageVisibility: () -> DeletedMessageVisibility,
        getLanguageDisplayName: (code: String) -> String,
    ): DecoratorProvider {
        return this@plus.createDecoratorProvider(
            channel,
            dateFormatter,
            messageListViewStyle,
            showAvatarPredicate,
            messageBackgroundFactory,
            deletedMessageVisibility,
            getLanguageDisplayName,
        ) + other.createDecoratorProvider(
            channel,
            dateFormatter,
            messageListViewStyle,
            showAvatarPredicate,
            messageBackgroundFactory,
            deletedMessageVisibility,
            getLanguageDisplayName,
        )
    }
}
