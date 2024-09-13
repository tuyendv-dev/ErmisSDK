package network.ermis.client.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.getstream.log.taggedLogger
import network.ermis.client.ErmisClient
import network.ermis.client.api.mapping.toDomain
import network.ermis.client.api.model.dto.MemberUpdatedEventDto
import network.ermis.client.api.model.dto.MessageUpdatedEventDto
import network.ermis.client.api.model.dto.NewMessageEventDto
import network.ermis.client.api.model.dto.NotificationAddedToChannelEventDto
import network.ermis.client.api.model.dto.ReactionNewEventDto
import network.ermis.client.parser.adapters.AttachmentDtoAdapter
import network.ermis.client.parser.adapters.DateAdapter
import network.ermis.client.parser.adapters.DownstreamChannelDtoAdapter
import network.ermis.client.parser.adapters.DownstreamMessageDtoAdapter
import network.ermis.client.parser.adapters.DownstreamModerationDetailsDtoAdapter
import network.ermis.client.parser.adapters.DownstreamReactionDtoAdapter
import network.ermis.client.parser.adapters.DownstreamUserDtoAdapter
import network.ermis.client.parser.adapters.EventAdapterFactory
import network.ermis.client.parser.adapters.ExactDateAdapter
import network.ermis.client.parser.adapters.UpstreamChannelDtoAdapter
import network.ermis.client.parser.adapters.UpstreamMessageDtoAdapter
import network.ermis.client.parser.adapters.UpstreamReactionDtoAdapter
import network.ermis.client.parser.adapters.UpstreamUserDtoAdapter
import network.ermis.client.utils.extensions.channelIdToProjectId
import network.ermis.client.utils.extensions.cidToTypeAndId
import network.ermis.core.models.Channel
import network.ermis.core.models.EventType
import network.ermis.core.models.Message
import org.json.JSONObject
import java.util.UUID

public class ErmisFirebaseMessaginfSerice : FirebaseMessagingService() {

    private val logger by taggedLogger("ErmisFirebaseMessaginfSerice")

    override fun onCreate() {
        super.onCreate()
        logger.e { "ErmisFirebaseMessaginfSerice onCreate" }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            val dataRemoteMessage = remoteMessage.data.get("data") as String
            logger.e { "Message data payload : ${dataRemoteMessage}" }
            MyWorker.start(applicationContext, dataRemoteMessage)
        }
    }

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        logger.d { "Refreshed token fcm: $token" }
    }

    internal class MyWorker(private val appContext: Context, workerParams: WorkerParameters) :
        CoroutineWorker(appContext, workerParams) {

        private val logger by taggedLogger("ErmisFirebaseMessaginfSerice")

        private val moshi = Moshi.Builder()
            .addAdapter(DateAdapter())
            .addAdapter(ExactDateAdapter())
            .add(EventAdapterFactory())
            .add(DownstreamMessageDtoAdapter)
            .add(DownstreamModerationDetailsDtoAdapter)
            .add(UpstreamMessageDtoAdapter)
            .add(DownstreamChannelDtoAdapter)
            .add(UpstreamChannelDtoAdapter)
            .add(AttachmentDtoAdapter)
            .add(DownstreamReactionDtoAdapter)
            .add(UpstreamReactionDtoAdapter)
            .add(DownstreamUserDtoAdapter)
            .add(UpstreamUserDtoAdapter)
            .build()
        private val newMessageEventAdapter = moshi.adapter(NewMessageEventDto::class.java)
        private val notificationAddedToChannelEventAdapter =
            moshi.adapter(NotificationAddedToChannelEventDto::class.java)
        private val reactionNewEventAdapter = moshi.adapter(ReactionNewEventDto::class.java)
        private val messageUpdatedEventAdapter = moshi.adapter(MessageUpdatedEventDto::class.java)
        private val memberUpdatedEventAdapter = moshi.adapter(MemberUpdatedEventDto::class.java)

        private inline fun <reified T> Moshi.Builder.addAdapter(adapter: JsonAdapter<T>) = apply {
            this.add(T::class.java, adapter)
        }

        override suspend fun doWork(): Result {

            val messageData: String = inputData.getString(DATA_PUSH)!!
            val jsonData = JSONObject(messageData)
            val client: ErmisClient = ErmisClient.instance()
            when (val type = jsonData.get("type") as? String) {
                EventType.MESSAGE_NEW -> {
                    val messageNew = newMessageEventAdapter.fromJson(messageData) ?: return Result.failure()
                    val projectId = messageNew.channel_id.channelIdToProjectId()
                    val channel = client.repositoryFacade.selectChannel(messageNew.cid) ?: Channel(
                        id = messageNew.channel_id,
                        type = messageNew.channel_type,
                        name = messageNew.channel_name,
                    )
                    client.getUserInfoNoAuth(messageNew.user.id, projectId).enqueue { result ->
                        when (result) {
                            is io.getstream.result.Result.Success -> {
                                val user = result.value
                                val message = messageNew.message.toDomain().copy(user = user)
                                ErmisClient.displayNotification(channel = channel, message = message)
                            }

                            is io.getstream.result.Result.Failure -> {
                                val message = messageNew.message.toDomain()
                                ErmisClient.displayNotification(channel = channel, message = message)
                            }
                        }
                    }
                }

                EventType.MESSAGE_UPDATED -> {
                    val update = messageUpdatedEventAdapter.fromJson(messageData) ?: return Result.failure()
                    val (channelType, channelId) = update.cid.cidToTypeAndId()
                    val channel = client.repositoryFacade.selectChannel(update.cid) ?: Channel(
                        id = channelId,
                        type = channelType,
                        name = " "
                    )
                    val projectId = channelId.channelIdToProjectId()
                    client.getUserInfoNoAuth(update.user.id, projectId).enqueue { result ->
                        if (result is io.getstream.result.Result.Success) {
                            val message = update.message.toDomain()
                            ErmisClient.displayNotification(
                                channel = channel,
                                message = message.copy(user = result.value)
                            )
                        }
                    }
                }

                EventType.NOTIFICATION_ADDED_TO_CHANNEL -> {
                    val messageInvite =
                        notificationAddedToChannelEventAdapter.fromJson(messageData) ?: return Result.failure()
                    val (channelType, channelId) = messageInvite.cid.cidToTypeAndId()
                    val channel = client.repositoryFacade.selectChannel(messageInvite.cid) ?: Channel(
                        id = channelId,
                        type = channelType,
                        name = " "
                    )
                    val projectId = channelId.channelIdToProjectId()
                    client.getUserInfoNoAuth(messageInvite.member.user.id, projectId).enqueue { result ->
                        if (result is io.getstream.result.Result.Success) {
                            val text =
                                if (channelType == "messaging") "You have a new DM invitation" else "You have a new channel invitation"
                            val message = Message(text = text, user = result.value)
                            ErmisClient.displayNotification(channel = channel, message = message)
                        }
                    }
                }

                EventType.REACTION_NEW -> {
                    val reaction = reactionNewEventAdapter.fromJson(messageData) ?: return Result.failure()
                    val (channelType, channelId) = reaction.cid.cidToTypeAndId()
                    val channel = client.repositoryFacade.selectChannel(reaction.cid) ?: Channel(
                        id = channelId,
                        type = channelType,
                        name = " "
                    )
                    val projectId = channelId.channelIdToProjectId()
                    client.getUserInfoNoAuth(reaction.user.id, projectId).enqueue { result ->
                        if (result is io.getstream.result.Result.Success) {
                            val text = "reacted with ${reaction.reaction.type} to your message"
                            val message = Message(text = text, user = result.value, id = reaction.message.id)
                            ErmisClient.displayNotification(channel = channel, message = message)
                        }
                    }
                }

                in listOf(EventType.MEMBER_BANNED, EventType.MEMBER_UNBANNED) -> {
                    val memberUpdate = memberUpdatedEventAdapter.fromJson(messageData) ?: return Result.failure()
                    val (channelType, channelId) = memberUpdate.cid.cidToTypeAndId()
                    val channel = client.repositoryFacade.selectChannel(memberUpdate.cid) ?: Channel(
                        id = channelId,
                        type = channelType,
                        name = " "
                    )
                    val memberId = memberUpdate.member.user.id
                    val currentUserId = client.getCurrentUser()?.id ?: client.getStoredUser()?.id
                    if (currentUserId == memberId) {
                        val text = when (type) {
                            EventType.MEMBER_BANNED -> "You have been banned from interacting in a channel."
                            EventType.MEMBER_UNBANNED -> "You have been unbanned in a channel."
                            else -> ""
                        }
                        val message = Message(text = text, id = UUID.randomUUID().toString())
                        ErmisClient.displayNotification(channel = channel, message = message)
                    }
                }
            }
            return Result.success()
        }

        companion object {
            private const val DATA_PUSH = "DATA_PUSH"

            fun start(context: Context, dataPush: String) {
                // [START dispatch_job]
                val work = OneTimeWorkRequest.Builder(MyWorker::class.java)
                    .setInputData(
                        workDataOf(
                            DATA_PUSH to dataPush
                        ),
                    )
                    .build()
                WorkManager.getInstance(context)
                    .beginWith(work)
                    .enqueue()
                // [END dispatch_job]
            }
        }
    }
}