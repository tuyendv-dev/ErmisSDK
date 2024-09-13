
package network.ermis.ui.view.messages.adapter

import network.ermis.ui.view.MessageListView.AttachmentClickListener
import network.ermis.ui.view.MessageListView.AttachmentDownloadClickListener
import network.ermis.ui.view.MessageListView.GiphySendListener
import network.ermis.ui.view.MessageListView.LinkClickListener
import network.ermis.ui.view.MessageListView.MessageClickListener
import network.ermis.ui.view.MessageListView.MessageLongClickListener
import network.ermis.ui.view.MessageListView.MessageRetryListener
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
import network.ermis.ui.view.MessageListView.ReactionViewClickListener
import network.ermis.ui.view.MessageListView.ThreadClickListener
import network.ermis.ui.view.MessageListView.UserClickListener

@Deprecated(
    message = "Use MessageListListeners instead",
    replaceWith = ReplaceWith("MessageListListeners"),
    level = DeprecationLevel.WARNING,
)
public sealed interface MessageListListenerContainer {
    public val messageClickListener: MessageClickListener
    public val messageLongClickListener: MessageLongClickListener
    public val messageRetryListener: MessageRetryListener
    public val threadClickListener: ThreadClickListener
    public val attachmentClickListener: AttachmentClickListener
    public val attachmentDownloadClickListener: AttachmentDownloadClickListener
    public val reactionViewClickListener: ReactionViewClickListener
    public val userClickListener: UserClickListener
    public val giphySendListener: GiphySendListener
    public val linkClickListener: LinkClickListener
}

public sealed interface MessageListListeners {
    public val messageClickListener: OnMessageClickListener
    public val messageLongClickListener: OnMessageLongClickListener
    public val messageRetryListener: OnMessageRetryListener
    public val threadClickListener: OnThreadClickListener
    public val attachmentClickListener: OnAttachmentClickListener
    public val attachmentDownloadClickListener: OnAttachmentDownloadClickListener
    public val reactionViewClickListener: OnReactionViewClickListener
    public val userClickListener: OnUserClickListener
    public val giphySendListener: OnGiphySendListener
    public val linkClickListener: OnLinkClickListener
}

@Deprecated(
    message = "Remove once MessageListListenerContainer is deleted",
    level = DeprecationLevel.WARNING,
)
internal class MessageListListenersAdapter(
    private val adaptee: MessageListListeners,
) : MessageListListenerContainer {
    override val messageClickListener: MessageClickListener = MessageClickListener {
        adaptee.messageClickListener.onMessageClick(it)
    }
    override val messageLongClickListener: MessageLongClickListener = MessageLongClickListener {
        adaptee.messageLongClickListener.onMessageLongClick(it)
    }
    override val messageRetryListener: MessageRetryListener = MessageRetryListener {
        adaptee.messageRetryListener.onRetryMessage(it)
    }
    override val threadClickListener: ThreadClickListener = ThreadClickListener {
        adaptee.threadClickListener.onThreadClick(it)
    }
    override val attachmentClickListener: AttachmentClickListener = AttachmentClickListener { message, attachment ->
        adaptee.attachmentClickListener.onAttachmentClick(message, attachment)
    }
    override val attachmentDownloadClickListener: AttachmentDownloadClickListener = AttachmentDownloadClickListener {
        adaptee.attachmentDownloadClickListener.onAttachmentDownloadClick(it)
    }
    override val reactionViewClickListener: ReactionViewClickListener = ReactionViewClickListener {
        adaptee.reactionViewClickListener.onReactionViewClick(it)
    }
    override val userClickListener: UserClickListener = UserClickListener {
        adaptee.userClickListener.onUserClick(it)
    }
    override val giphySendListener: GiphySendListener = GiphySendListener {
        adaptee.giphySendListener.onGiphySend(it)
    }
    override val linkClickListener: LinkClickListener = LinkClickListener {
        adaptee.linkClickListener.onLinkClick(it)
    }
}
