package network.ermis.client.socket

import network.ermis.client.parser.ChatParser
import network.ermis.client.token.TokenManager
import network.ermis.core.models.User
import io.getstream.log.taggedLogger
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

internal class SocketFactory(
    private val parser: ChatParser,
    private val tokenManager: TokenManager,
    private val httpClient: OkHttpClient = OkHttpClient(),
) {
    private val logger by taggedLogger("Chat:SocketFactory")

    @Throws(UnsupportedEncodingException::class)
    fun createSocket(connectionConf: ConnectionConf): StreamWebSocket {
        val request = buildRequest(connectionConf)
        logger.i { "new web socket: ${request.url}" }
        return StreamWebSocket(parser) { httpClient.newWebSocket(request, it) }
    }

    @Throws(UnsupportedEncodingException::class)
    private fun buildRequest(connectionConf: ConnectionConf): Request =
        Request.Builder()
            .url(buildUrl(connectionConf))
            .build()

    @Suppress("TooGenericExceptionCaught")
    @Throws(UnsupportedEncodingException::class)
    private fun buildUrl(connectionConf: ConnectionConf): String {
        var json = buildUserDetailJson(connectionConf)
        return try {
            json = URLEncoder.encode(json, StandardCharsets.UTF_8.name())
            val baseWsUrl = "${connectionConf.endpoint}connect?json=$json&api_key=${connectionConf.apiKey}"
            when (connectionConf) {
                is ConnectionConf.AnonymousConnectionConf ->
                    // "$baseWsUrl&stream-auth-type=anonymous"
                {
                    val token = tokenManager.getToken()
                        .takeUnless { connectionConf.isReconnection }
                        ?: tokenManager.loadSync()
                    "$baseWsUrl&authorization=$token&stream-auth-type=jwt"
                }
                is ConnectionConf.UserConnectionConf -> {
                    val token = tokenManager.getToken()
                        .takeUnless { connectionConf.isReconnection }
                        ?: tokenManager.loadSync()
                    logger.i { "curl $baseWsUrl&authorization=$token&stream-auth-type=jwt"}
                    "$baseWsUrl&authorization=$token&stream-auth-type=jwt"
                    // "wss://api.belo.im/connect?json=%7B%22user_id%22%3A%22dat%22%2C%22user_details%22%3A%7B%22id%22%3A%22dat%22%2C%22name%22%3A%22dat%22%2C%22image%22%3A%22%2Fstatic%2Fmedia%2Fphoto-1531251445707-1f000e1e87d0.be76211f34667253982e.jpeg%22%7D%2C%22client_request_id%22%3A%223ef7a165-4d8b-4c34-be06-5cb9231102b8%22%7D&authorization=Bearer%20eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiZGF0IiwiZXhwIjoxNzEyNjUyODA2MDU3fQ.IPItV9uMRUlZTMy6Xfv2KSnKynWeX4yZXhR37EWuLs8"
                    // "ws://localhost:8888/connect?json=%7B%22user_id%22%3A%22dat%22%2C%22user_details%22%3A%7B%22id%22%3A%22dat%22%2C%22name%22%3A%22dat%22%2C%22image%22%3A%22%2Fstatic%2Fmedia%2Fphoto-1463453091185-61582044d556.3619e52a934ed9b4e659.jpeg%22%7D%2C%22client_request_id%22%3A%223dd39423-e031-4696-a993-f47dfdee7ada%22%7D&authorization=Bearer%20eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiZGF0IiwiZXhwIjoxNzEyNzE3OTkzNjk5fQ.i8g9C5Jmk2IWB9spBRlV7IsSpjbhyxvv4Zfk_SYkGrE"
                }
            }
        } catch (_: Throwable) {
            throw UnsupportedEncodingException("Unable to encode user details json: $json")
        }
    }

    private fun buildUserDetailJson(connectionConf: ConnectionConf): String {
        val data = mapOf(
            "user_details" to connectionConf.reduceUserDetails(),
            "user_id" to connectionConf.id,
            "client_request_id" to UUID.randomUUID().toString(), // TODO LEDAT hard code
            // "server_determines_connection_id" to true, TODO LEDAT hard code
            // "X-Stream-Client" to ChatClient.buildSdkTrackingHeaders(), TODO LEDAT hard code
        )
        return parser.toJson(data)
    }

    /**
     * Converts the [User] object to a map of properties updated while connecting the user.
     * [User.name] and [User.image] will only be included if they are not blank.
     *
     * @return A map of User's properties to update.
     */
    private fun ConnectionConf.reduceUserDetails(): Map<String, Any> = mutableMapOf<String, Any>("id" to id,
        "name" to id, "image" to "", "api_key" to apiKey)
        .apply {
            if (!isReconnection) {
                if (user.role.isNotBlank()) put("role", user.role)
                user.banned?.also { put("banned", it) }
                user.invisible?.also { put("invisible", it) }
                if (user.teams.isNotEmpty()) put("teams", user.teams)
                if (user.language.isNotBlank()) put("language", user.language)
                if (user.image.isNotBlank()) put("image", user.image)
                if (user.name.isNotBlank()) put("name", user.name)
                putAll(user.extraData)
            }
        }

    internal sealed class ConnectionConf {
        var isReconnection: Boolean = false
            private set
        abstract val endpoint: String
        abstract val apiKey: String
        abstract val user: User

        data class AnonymousConnectionConf(
            override val endpoint: String,
            override val apiKey: String,
            override val user: User,
        ) : ConnectionConf()

        data class UserConnectionConf(
            override val endpoint: String,
            override val apiKey: String,
            override val user: User,
        ) : ConnectionConf()

        internal fun asReconnectionConf(): ConnectionConf = this.also { isReconnection = true }

        internal val id: String
            get() = when (this) {
                is AnonymousConnectionConf -> user.id.replace("!", "")
                is UserConnectionConf -> user.id
            }
    }
}
