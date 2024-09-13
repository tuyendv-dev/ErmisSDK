package network.ermis.client.api

import network.ermis.client.ConfigBuilder
import network.ermis.core.models.User
import io.getstream.log.taggedLogger
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import org.json.JSONObject
import java.util.concurrent.TimeUnit

public class SSEClient(private val mToken: String, private val listener: (User) -> Unit) {

    private val logger by taggedLogger("SSEClient")
    private var reconnectCount = 0
    private val sseClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .build()

    private val sseEventSourceListener = object : EventSourceListener() {
        override fun onClosed(eventSource: EventSource) {
            // logger.e { "sseEventSourceListener onClosed eventSource=$eventSource" }
        }

        override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
            logger.i { "sseEventSourceListener onEvent id=$id  type=$type  data=$data" }
            when (type) {
                "connected" -> {}
                else -> {
                    val json = JSONObject(data)
                    val userId = json.getString("id")
                    val name = json.getString("name")
                    val avatar = json.getString("avatar")
                    val role = json.getString("role")
                    if (userId.isNotEmpty()) {
                        listener.invoke(
                            User(
                                id = userId,
                                name = name,
                                image = avatar,
                                role = role,
                            )
                        )
                    }
                }
            }
        }

        override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
            // logger.i { "EventSourceListener onFailure Throwable=$t eventSource=$eventSource response=$response" }
            if (response?.code == 200) {
                // Throwable=java.net.SocketException: Software caused connection abort
                if (reconnectCount < 4) initEventSource()
            }
        }

        override fun onOpen(eventSource: EventSource, response: Response) {
            // logger.i { "EventSourceListener onOpen response=$response" }
            if (response.code == 200 && response.message == "OK") {
                reconnectCount = 0
            }
        }
    }
    init {
        initEventSource()
    }

    private fun initEventSource() {
        reconnectCount++
        val sseRequest = Request.Builder()
            .url("https://${ConfigBuilder.URL_BASE_CHAT}/subscribe")
            .header("Accept", "application/json")
            .addHeader("Accept", "text/event-stream")
            .addHeader("Authorization", mToken)
            .build()
        EventSources.createFactory(sseClient)
            .newEventSource(request = sseRequest, listener = sseEventSourceListener)
    }
}
