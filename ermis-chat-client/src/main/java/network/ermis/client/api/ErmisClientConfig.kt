package network.ermis.client.api

import network.ermis.client.ErmisClient
import network.ermis.client.api.internal.DistinctChatApi
import network.ermis.client.logger.ChatLoggerConfig
import network.ermis.client.notifications.handler.NotificationConfig

/**
 * A config to setup the [ErmisClient] behavior.
 *
 * @param apiKey The API key of your.
 * @param httpUrl The base URL to be used by the client.
 * @param cdnHttpUrl The base CDN URL to be used by the client.
 * @param wssUrl The base WebSocket URL to be used by the client.
 * @param warmUp Controls the connection warm-up behavior.
 * @param loggerConfig A logging config to be used by the client.
 * @param distinctApiCalls Controls whether [DistinctChatApi] is enabled or not.
 * @param debugRequests Controls whether requests can be recorded or not.
 * @param notificationConfig A notification config to be used by the client.
 */
@Suppress("LongParameterList")
public class ErmisClientConfig @JvmOverloads constructor(
    public val apiKey: String,
    public var httpUrl: String,
    public var cdnHttpUrl: String,
    public var walletHttpUrl: String,
    public var wssUrl: String,
    public val warmUp: Boolean,
    public val loggerConfig: ChatLoggerConfig,
    public var distinctApiCalls: Boolean = true,
    public val debugRequests: Boolean,
    public val notificationConfig: NotificationConfig,
) {
    public var isAnonymous: Boolean = false
}
