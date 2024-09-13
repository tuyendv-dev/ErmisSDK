package network.ermis.client.persistance

import network.ermis.client.query.pagination.AnyChannelPaginationRequest
import network.ermis.core.models.Message
import network.ermis.core.models.SyncStatus
import java.util.Date

/**
 * Repository to read and write [Message] data.
 */

@Suppress("TooManyFunctions")
public interface MessageRepository {

    /**
     * Select messages for a channel in a desired page.
     *
     * @param cid String.
     * @param pagination [AnyChannelPaginationRequest]
     */
    public suspend fun selectMessagesForChannel(
        cid: String,
        pagination: AnyChannelPaginationRequest?,
    ): List<Message>

    /**
     * Select messages for a thread in a desired page.
     *
     * @param messageId String.
     * @param limit limit of messages
     */
    public suspend fun selectMessagesForThread(
        messageId: String,
        limit: Int,
    ): List<Message>

    /**
     * Selects messages by IDs.
     *
     * @param messageIds A list of [Message.id] as query specification.
     * @param forceCache A boolean flag that forces cache in repository and fetches data directly in database if passed
     * value is true.
     *
     * @return A list of messages found in repository.
     */
    public suspend fun selectMessages(messageIds: List<String>): List<Message>

    /**
     * Reads the message with passed ID.
     *
     * @param messageId String.
     */
    public suspend fun selectMessage(messageId: String): Message?

    /**
     * Inserts many messages.
     *
     * @param messages list of [Message]
     * @param cache Boolean.
     */
    public suspend fun insertMessages(messages: List<Message>)

    /**
     * Inserts a messages.
     *
     * @param message [Message]
     * @param cache Boolean.
     */
    public suspend fun insertMessage(message: Message)

    /**
     * Deletes all messages before a message with passed ID.
     *
     * @param cid of message - String.
     * @param hideMessagesBefore Boolean.
     */
    public suspend fun deleteChannelMessagesBefore(cid: String, hideMessagesBefore: Date)

    /**
     * Deletes all messages from a channel.
     *
     * @param cid of message - String.
     */
    public suspend fun deleteChannelMessages(cid: String)

    /**
     * Deletes message.
     *
     * @param message [Message]
     */
    public suspend fun deleteChannelMessage(message: Message)

    /**
     * Selects all message ids of a [SyncStatus]
     *
     * @param syncStatus [SyncStatus]
     */
    public suspend fun selectMessageIdsBySyncState(syncStatus: SyncStatus): List<String>

    /**
     * Selects all message of a [SyncStatus]
     *
     * @param syncStatus [SyncStatus]
     */
    public suspend fun selectMessageBySyncState(syncStatus: SyncStatus): List<Message>

    /**
     * Clear messages of this repository.
     */
    public suspend fun clear()
}
