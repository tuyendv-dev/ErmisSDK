package network.ermis.client.persistance.noop

import network.ermis.client.persistance.MessageRepository
import network.ermis.client.query.pagination.AnyChannelPaginationRequest
import network.ermis.core.models.Message
import network.ermis.core.models.SyncStatus
import java.util.Date

/**
 * No-Op MessageRepository.
 */

@Suppress("TooManyFunctions")
internal object NoOpMessageRepository : MessageRepository {
    override suspend fun selectMessages(messageIds: List<String>): List<Message> = emptyList()
    override suspend fun selectMessage(messageId: String): Message? = null
    override suspend fun insertMessages(messages: List<Message>) { /* No-Op */ }
    override suspend fun insertMessage(message: Message) { /* No-Op */ }
    override suspend fun deleteChannelMessagesBefore(cid: String, hideMessagesBefore: Date) { /* No-Op */ }
    override suspend fun deleteChannelMessages(cid: String) { /* No-Op */ }
    override suspend fun deleteChannelMessage(message: Message) { /* No-Op */ }
    override suspend fun selectMessageIdsBySyncState(syncStatus: SyncStatus): List<String> = emptyList()
    override suspend fun selectMessageBySyncState(syncStatus: SyncStatus): List<Message> = emptyList()
    override suspend fun clear() { /* No-Op */ }
    override suspend fun selectMessagesForChannel(
        cid: String,
        pagination: AnyChannelPaginationRequest?,
    ): List<Message> = emptyList()
    override suspend fun selectMessagesForThread(messageId: String, limit: Int): List<Message> = emptyList()
}
