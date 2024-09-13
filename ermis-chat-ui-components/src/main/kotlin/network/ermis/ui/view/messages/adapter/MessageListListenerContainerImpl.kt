
package network.ermis.ui.view.messages.adapter

import network.ermis.ui.view.MessageListView.OnAttachmentClickListener
import network.ermis.ui.view.MessageListView.OnAttachmentDownloadClickListener
import network.ermis.ui.view.MessageListView.OnGiphySendListener
import network.ermis.ui.view.MessageListView.OnLinkClickListener
import network.ermis.ui.view.MessageListView.OnMessageClickListener
import network.ermis.ui.view.MessageListView.OnMessageLongClickListener
import network.ermis.ui.view.MessageListView.OnMessageRetryListener
import network.ermis.ui.view.MessageListView.OnReactionViewClickListener
import network.ermis.ui.view.MessageListView.OnThreadClickListener
import network.ermis.ui.view.MessageListView.OnUserClickListener
import network.ermis.ui.utils.ListenerDelegate

internal class MessageListListenerContainerImpl(
    messageClickListener: OnMessageClickListener = OnMessageClickListener(EmptyFunctions.ONE_PARAM),
    messageLongClickListener: OnMessageLongClickListener = OnMessageLongClickListener(EmptyFunctions.ONE_PARAM),
    messageRetryListener: OnMessageRetryListener = OnMessageRetryListener(EmptyFunctions.ONE_PARAM),
    threadClickListener: OnThreadClickListener = OnThreadClickListener(EmptyFunctions.ONE_PARAM),
    attachmentClickListener: OnAttachmentClickListener = OnAttachmentClickListener(EmptyFunctions.TWO_PARAM),
    attachmentDownloadClickListener: OnAttachmentDownloadClickListener = OnAttachmentDownloadClickListener(
        EmptyFunctions.ONE_PARAM
    ),
    reactionViewClickListener: OnReactionViewClickListener = OnReactionViewClickListener(EmptyFunctions.ONE_PARAM),
    userClickListener: OnUserClickListener = OnUserClickListener(EmptyFunctions.ONE_PARAM),
    giphySendListener: OnGiphySendListener = OnGiphySendListener(EmptyFunctions.ONE_PARAM),
    linkClickListener: OnLinkClickListener = OnLinkClickListener(EmptyFunctions.ONE_PARAM),
) : MessageListListeners {
    private object EmptyFunctions {
        val ONE_PARAM: (Any) -> Boolean = { _ -> false }
        val TWO_PARAM: (Any, Any) -> Boolean = { _, _ -> false }
    }

    override var messageClickListener: OnMessageClickListener by ListenerDelegate(
        messageClickListener,
    ) { realListener ->
        OnMessageClickListener { message ->
            realListener().onMessageClick(message)
        }
    }

    override var messageLongClickListener: OnMessageLongClickListener by ListenerDelegate(
        messageLongClickListener,
    ) { realListener ->
        OnMessageLongClickListener { message ->
            realListener().onMessageLongClick(message)
        }
    }

    override var messageRetryListener: OnMessageRetryListener by ListenerDelegate(
        messageRetryListener,
    ) { realListener ->
        OnMessageRetryListener { message ->
            realListener().onRetryMessage(message)
        }
    }

    override var threadClickListener: OnThreadClickListener by ListenerDelegate(
        threadClickListener,
    ) { realListener ->
        OnThreadClickListener { message ->
            realListener().onThreadClick(message)
        }
    }

    override var attachmentClickListener: OnAttachmentClickListener by ListenerDelegate(
        attachmentClickListener,
    ) { realListener ->
        OnAttachmentClickListener { message, attachment ->
            realListener().onAttachmentClick(message, attachment)
        }
    }

    override var attachmentDownloadClickListener: OnAttachmentDownloadClickListener by ListenerDelegate(
        attachmentDownloadClickListener,
    ) { realListener ->
        OnAttachmentDownloadClickListener { attachment ->
            realListener().onAttachmentDownloadClick(attachment)
        }
    }

    override var reactionViewClickListener: OnReactionViewClickListener by ListenerDelegate(
        reactionViewClickListener,
    ) { realListener ->
        OnReactionViewClickListener { message ->
            realListener().onReactionViewClick(message)
        }
    }

    override var userClickListener: OnUserClickListener by ListenerDelegate(
        userClickListener,
    ) { realListener ->
        OnUserClickListener { user ->
            realListener().onUserClick(user)
        }
    }

    override var giphySendListener: OnGiphySendListener by ListenerDelegate(
        giphySendListener,
    ) { realListener ->
        OnGiphySendListener { action ->
            realListener().onGiphySend(action)
        }
    }

    override var linkClickListener: OnLinkClickListener by ListenerDelegate(
        linkClickListener,
    ) { realListener ->
        OnLinkClickListener { url ->
            realListener().onLinkClick(url)
        }
    }
}
