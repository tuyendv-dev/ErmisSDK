package network.ermis.client.api.models.identifier

import network.ermis.client.ErmisClient
import network.ermis.client.api.models.QueryChannelRequest
import network.ermis.client.api.models.QueryChannelsRequest
import network.ermis.client.api.models.SendActionRequest
import network.ermis.core.models.Device
import network.ermis.core.models.FilterObject
import network.ermis.core.models.Member
import network.ermis.core.models.Message
import network.ermis.core.models.Reaction
import network.ermis.core.models.querysort.QuerySorter

/**
 * Identifier for a [ErmisClient.queryChannel] call.
 */
@Suppress("FunctionName", "MagicNumber")
internal fun QueryChannelIdentifier(
    channelType: String,
    channelId: String,
    request: QueryChannelRequest,
): Int {
    var result = "QueryChannel".hashCode()
    result = 31 * result + channelType.hashCode()
    result = 31 * result + channelId.hashCode()
    result = 31 * result + request.hashCode()
    return result
}

/**
 * Identifier for a [ErmisClient.queryChannels] call.
 */
@Suppress("FunctionName", "MagicNumber")
internal fun QueryChannelsIdentifier(
    request: QueryChannelsRequest,
): Int {
    var result = "QueryChannels".hashCode()
    result = 31 * result + request.hashCode()
    return result
}

/**
 * Identifier for a [ErmisClient.queryMembers] call.
 */
@Suppress("FunctionName", "MagicNumber", "LongParameterList")
internal fun QueryMembersIdentifier(
    channelType: String,
    channelId: String,
    offset: Int,
    limit: Int,
    filter: FilterObject,
    sort: QuerySorter<Member>,
    members: List<Member> = emptyList(),
): Int {
    var result = "QueryMembers".hashCode()
    result = 31 * result + channelType.hashCode()
    result = 31 * result + channelId.hashCode()
    result = 31 * result + offset.hashCode()
    result = 31 * result + limit.hashCode()
    result = 31 * result + filter.hashCode()
    result = 31 * result + sort.hashCode()
    result = 31 * result + members.hashCode()
    return result
}

/**
 * Identifier for a [ErmisClient.deleteReaction] call.
 */
@Suppress("FunctionName", "MagicNumber")
internal fun DeleteReactionIdentifier(
    messageId: String,
    reactionType: String,
    cid: String?,
): Int {
    var result = "DeleteReaction".hashCode()
    result = 31 * result + messageId.hashCode()
    result = 31 * result + reactionType.hashCode()
    result = 31 * result + (cid?.hashCode() ?: 0)
    return result
}

/**
 * Identifier for a [ErmisClient.sendReaction] call.
 */
@Suppress("FunctionName", "MagicNumber")
internal fun SendReactionIdentifier(
    reaction: Reaction,
    enforceUnique: Boolean,
    cid: String?,
): Int {
    var result = "SendReaction".hashCode()
    result = 31 * result + reaction.hashCode()
    result = 31 * result + enforceUnique.hashCode()
    result = 31 * result + cid.hashCode()
    return result
}

/**
 * Identifier for a [ErmisClient.getReplies] call.
 */
@Suppress("FunctionName", "MagicNumber")
internal fun GetRepliesIdentifier(
    messageId: String,
    limit: Int,
): Int {
    var result = "GetReplies".hashCode()
    result = 31 * result + messageId.hashCode()
    result = 31 * result + limit.hashCode()
    return result
}

/**
 * Identifier for a [ErmisClient.getRepliesMore] call.
 */
@Suppress("FunctionName", "MagicNumber")
internal fun GetRepliesMoreIdentifier(
    messageId: String,
    firstId: String,
    limit: Int,
): Int {
    var result = "GetRepliesMore".hashCode()
    result = 31 * result + messageId.hashCode()
    result = 31 * result + firstId.hashCode()
    result = 31 * result + limit.hashCode()
    return result
}

/**
 * Identifier for a [ErmisClient.sendGiphy] call.
 */
@Suppress("FunctionName", "MagicNumber")
internal fun SendGiphyIdentifier(
    request: SendActionRequest,
): Int {
    var result = "SendGiphy".hashCode()
    result = 31 * result + request.hashCode()
    return result
}

/**
 * Identifier for a [ErmisClient.shuffleGiphy] call.
 */
@Suppress("FunctionName", "MagicNumber")
internal fun ShuffleGiphyIdentifier(
    request: SendActionRequest,
): Int {
    var result = "ShuffleGiphy".hashCode()
    result = 31 * result + request.hashCode()
    return result
}

/**
 * Identifier for a [ErmisClient.deleteMessage] call.
 */
@Suppress("FunctionName", "MagicNumber")
internal fun DeleteMessageIdentifier(
    messageId: String,
    hard: Boolean,
): Int {
    var result = "DeleteMessage".hashCode()
    result = 31 * result + messageId.hashCode()
    result = 31 * result + hard.hashCode()
    return result
}

/**
 * Identifier for [ErmisClient.keystroke] and [ErmisClient.stopTyping] calls.
 */
@Suppress("FunctionName", "MagicNumber")
internal fun SendEventIdentifier(
    eventType: String,
    channelType: String,
    channelId: String,
    parentId: String?,
): Int {
    var result = "SendEvent".hashCode()
    result = 31 * result + eventType.hashCode()
    result = 31 * result + channelType.hashCode()
    result = 31 * result + channelId.hashCode()
    result = 31 * result + parentId.hashCode()
    return result
}

/**
 * Identifier for a [ErmisClient.updateMessage] call.
 */
@Suppress("FunctionName", "MagicNumber")
internal fun UpdateMessageIdentifier(
    message: Message,
): Int {
    var result = "UpdateMessage".hashCode()
    result = 31 * result + message.hashCode()
    return result
}

/**
 * Identifier for a [ErmisClient.hideChannel] call.
 */
@Suppress("FunctionName", "MagicNumber")
internal fun HideChannelIdentifier(
    channelType: String,
    channelId: String,
    clearHistory: Boolean,
): Int {
    var result = "HideChannel".hashCode()
    result = 31 * result + channelType.hashCode()
    result = 31 * result + channelId.hashCode()
    result = 31 * result + clearHistory.hashCode()
    return result
}

/**
 * Identifier for a [ErmisClient.getMessage] call.
 */
@Suppress("FunctionName", "MagicNumber")
internal fun GetMessageIdentifier(
    messageId: String,
): Int {
    var result = "GetMessage".hashCode()
    result = 31 * result + messageId.hashCode()

    return result
}

/**
 * Identifier for a [ErmisClient.markAllRead] call.
 */
@Suppress("FunctionName", "FunctionOnlyReturningConstant")
internal fun MarkAllReadIdentifier(): Int {
    return "MarkAllRead".hashCode()
}

/**
 * Identifier for a [ErmisClient.hideChannel] call.
 */
@Suppress("FunctionName", "MagicNumber")
internal fun MarkReadIdentifier(
    channelType: String,
    channelId: String,
): Int {
    var result = "MarkRead".hashCode()
    result = 31 * result + channelType.hashCode()
    result = 31 * result + channelId.hashCode()
    return result
}

/**
 * Identifier for a [ErmisClient.sendMessage] call.
 */
@Suppress("FunctionName", "MagicNumber")
internal fun SendMessageIdentifier(
    channelType: String,
    channelId: String,
    messageId: String,
): Int {
    var result = "SendMessage".hashCode()
    result = 31 * result + channelType.hashCode()
    result = 31 * result + channelId.hashCode()
    result = 31 * result + messageId.hashCode()
    return result
}

/**
 * Identifier for a [ErmisClient.getDevices] call.
 */
@Suppress("FunctionName", "FunctionOnlyReturningConstant")
internal fun GetDevicesIdentifier(): Int {
    return "GetDevices".hashCode()
}

/**
 * Identifier for a [ErmisClient.addDevice] call.
 */
@Suppress("FunctionName", "FunctionOnlyReturningConstant")
internal fun AddDeviceIdentifier(
    device: Device,
): Int {
    var result = "AddDevice".hashCode()
    result = 31 * result + device.hashCode()
    return result
}

/**
 * Identifier for a [ErmisClient.deleteDevice] call.
 */
@Suppress("FunctionName", "FunctionOnlyReturningConstant")
internal fun DeleteDeviceIdentifier(
    device: Device,
): Int {
    var result = "DeleteDevice".hashCode()
    result = 31 * result + device.hashCode()
    return result
}
