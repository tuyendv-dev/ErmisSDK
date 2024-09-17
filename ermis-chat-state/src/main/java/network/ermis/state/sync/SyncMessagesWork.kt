package network.ermis.state.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import io.getstream.log.StreamLog
import network.ermis.client.ErmisClient
import network.ermis.client.utils.extensions.cidToTypeAndId
import network.ermis.client.utils.internal.validateCid
import network.ermis.state.extensions.logic
import network.ermis.state.plugin.internal.StatePlugin

@Suppress("TooGenericExceptionCaught")
internal class SyncMessagesWork(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val cid = inputData.getString(DATA_CID)!!
        val client = ErmisClient.instance()

        StreamLog.i(TAG) { "[doWork] cid: $cid" }

        return try {
            val (type, id) = validateCid(cid).cidToTypeAndId()

            client.logic.channel(type, id) // Adds this channel to logic - Now it is an active channel

            val syncManager = client.resolveDependency<StatePlugin, SyncHistoryManager>()
            syncManager.sync()

            Result.success()
        } catch (e: Throwable) {
            StreamLog.e(TAG) { "[doWork] failed: $e" }
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "Chat:SyncMessagesWork"
        private const val DATA_CID = "DATA_CID"
        private const val SYNC_MESSAGES_WORK_NAME = "SYNC_MESSAGES_WORK_NAME"

        fun start(context: Context, cid: String) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncMessagesWork = OneTimeWorkRequestBuilder<SyncMessagesWork>()
                .setConstraints(constraints)
                .setInputData(workDataOf(DATA_CID to cid))
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    SYNC_MESSAGES_WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    syncMessagesWork,
                )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(SYNC_MESSAGES_WORK_NAME)
        }
    }
}
