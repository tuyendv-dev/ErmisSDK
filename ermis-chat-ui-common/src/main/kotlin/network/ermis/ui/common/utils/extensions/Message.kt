package network.ermis.ui.common.utils.extensions

import network.ermis.client.ErmisClient
import network.ermis.core.models.Message
import network.ermis.core.models.User

/**
 * @return if the message was sent by current user.
 */
public fun Message.isMine(chatClient: ErmisClient): Boolean = chatClient.clientState.user.value?.id == user.id

public fun Message.isMine(currentUser: User?): Boolean = currentUser?.id == user.id

/**
 * @return if the message failed at moderation or not.
 */
@Deprecated(
    message = "Use the one from stream-chat-android-client",
    replaceWith = ReplaceWith(
        expression = "isModerationFailed(currentUserId)",
        imports = ["io.getstream.chat.android.client.utils.message.isModerationError"],
    ),
    level = DeprecationLevel.WARNING,
)
public fun Message.isModerationFailed(currentUser: User?): Boolean = isMine(currentUser) &&
    (type == Message.TYPE_ERROR && moderationDetails?.action == network.ermis.core.models.MessageModerationAction.bounce)
