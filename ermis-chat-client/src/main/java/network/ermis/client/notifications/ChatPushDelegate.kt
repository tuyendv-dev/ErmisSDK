package network.ermis.client.notifications

import android.content.Context
import io.getstream.android.push.PushDevice
import io.getstream.android.push.PushProvider
import io.getstream.android.push.delegate.PushDelegate
import network.ermis.client.ErmisClient
import network.ermis.core.errors.PayloadValidator
import network.ermis.client.notifications.parser.ErmisPayloadParser
import network.ermis.core.models.Device
import network.ermis.core.models.PushMessage
import io.getstream.log.taggedLogger

private typealias DevicePushProvider = network.ermis.core.models.PushProvider

/**
 * Internal class that handle PN stuff.
 * It is declared in our Android Manifest and is used by reflection.
 *
 */
@Suppress("Unused")
internal class ChatPushDelegate(context: Context) : PushDelegate(context) {

    private val expectedKeys = hashSetOf(KEY_CHANNEL_ID, KEY_MESSAGE_ID, KEY_CHANNEL_TYPE, KEY_GETSTREAM)
    private val logger by taggedLogger("ChatPushDelegate")
    override fun handlePushMessage(metadata: Map<String, Any?>, payload: Map<String, Any?>): Boolean {
        logger.e { "ChatPushDelegate handlePushMessage metadata=$metadata  payload=$payload" }
        return payload.ifValid {
            ErmisClient.handlePushMessage(
                PushMessage(
                    channelId = payload.getValue(KEY_CHANNEL_ID) as String,
                    messageId = payload.getValue(KEY_MESSAGE_ID) as String,
                    channelType = payload.getValue(KEY_CHANNEL_TYPE) as String,
                    getstream = ErmisPayloadParser.parse(payload[KEY_GETSTREAM] as? String),
                    extraData = payload.filterKeys { it !in expectedKeys },
                    metadata = metadata,
                ),
            )
        }
    }

    override fun registerPushDevice(pushDevice: PushDevice) {
        ErmisClient.setDevice(pushDevice.toDevice())
    }

    private fun Map<String, Any?>.ifValid(effect: () -> Unit): Boolean {
        val isValid = PayloadValidator.isFromStreamServer(this) &&
            PayloadValidator.isValidNewMessage(this)
        effect.takeIf { isValid }?.invoke()
        return isValid
    }

    private companion object {
        private const val KEY_CHANNEL_ID = "channel_id"
        private const val KEY_MESSAGE_ID = "message_id"
        private const val KEY_CHANNEL_TYPE = "channel_type"
        private const val KEY_GETSTREAM = "getstream"
    }
}

internal fun PushDevice.toDevice(): Device =
    Device(
        token = token,
        pushProvider = pushProvider.toDevicePushProvider(),
        providerName = providerName,
    )

private fun PushProvider.toDevicePushProvider(): DevicePushProvider = when (this) {
    PushProvider.FIREBASE -> DevicePushProvider.FIREBASE
    PushProvider.HUAWEI -> DevicePushProvider.HUAWEI
    PushProvider.XIAOMI -> DevicePushProvider.XIAOMI
    PushProvider.UNKNOWN -> DevicePushProvider.UNKNOWN
}
