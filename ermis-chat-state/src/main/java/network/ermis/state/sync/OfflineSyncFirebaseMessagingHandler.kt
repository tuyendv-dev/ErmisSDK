package network.ermis.state.sync

import android.content.Context
import io.getstream.log.taggedLogger

internal class OfflineSyncFirebaseMessagingHandler {
    private val logger by taggedLogger("Chat:OfflineSyncFirebaseMessagingHandler")

    fun syncMessages(context: Context, cid: String) {
        logger.d { "Starting the sync" }

        SyncMessagesWork.start(context, cid)
    }

    fun cancel(context: Context) {
        SyncMessagesWork.cancel(context)
    }
}