package network.ermis.client.utils.extensions

import network.ermis.core.errors.isPermanent
import network.ermis.core.models.Message
import io.getstream.result.Error
import network.ermis.core.models.SyncStatus
import java.util.Date

public fun Message.enrichWithCid(newCid: String): Message = copy(
    replyTo = replyTo?.enrichWithCid(newCid),
    cid = newCid,
)

/**
 * Updates a message that whose request (Edition/Delete/Reaction update...) has failed.
 *
 * @param error [Error].
 */
public fun Message.updateFailedMessage(error: Error): Message {
    return this.copy(
        syncStatus = if (error.isPermanent()) {
            SyncStatus.FAILED_PERMANENTLY
        } else {
            SyncStatus.SYNC_NEEDED
        },
        updatedLocallyAt = Date(),
    )
}

/**
 * Update the online state of a message.
 *
 * @param isOnline [Boolean].
 */
public fun Message.updateMessageOnlineState(isOnline: Boolean): Message {
    return this.copy(
        syncStatus = if (isOnline) SyncStatus.IN_PROGRESS else SyncStatus.SYNC_NEEDED,
        updatedLocallyAt = Date(),
    )
}

/**
 * @return when the message was created or throw an exception.
 */
public fun Message.getCreatedAtOrThrow(): Date {
    val created = getCreatedAtOrNull()
    return checkNotNull(created) { "a message needs to have a non null value for either createdAt or createdLocallyAt" }
}

/**
 * @return when the message was created or null.
 */
public fun Message.getCreatedAtOrNull(): Date? {
    return createdAt ?: createdLocallyAt
}

/**
 * @return when the message was created or `default`.
 */
public fun Message.getCreatedAtOrDefault(default: Date): Date {
    return getCreatedAtOrNull() ?: default
}
