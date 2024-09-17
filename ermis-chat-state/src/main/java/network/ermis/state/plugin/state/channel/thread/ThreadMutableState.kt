package network.ermis.state.plugin.state.channel.thread

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import network.ermis.core.models.Message

internal class ThreadMutableState(
    override val parentId: String,
    scope: CoroutineScope,
) : ThreadState {
    private var _messages: MutableStateFlow<Map<String, Message>>? = MutableStateFlow(emptyMap())
    private var _loading: MutableStateFlow<Boolean>? = MutableStateFlow(false)
    private var _loadingOlderMessages: MutableStateFlow<Boolean>? = MutableStateFlow(false)
    private var _endOfOlderMessages: MutableStateFlow<Boolean>? = MutableStateFlow(false)
    private var _oldestInThread: MutableStateFlow<Message?>? = MutableStateFlow(null)

    val rawMessage: StateFlow<Map<String, Message>> = _messages!!
    override val messages: StateFlow<List<Message>> = rawMessage
        .map { it.values }
        .map { threadMessages -> threadMessages.sortedBy { m -> m.createdAt ?: m.createdLocallyAt } }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())
    override val loading: StateFlow<Boolean> = _loading!!
    override val loadingOlderMessages: StateFlow<Boolean> = _loadingOlderMessages!!
    override val endOfOlderMessages: StateFlow<Boolean> = _endOfOlderMessages!!
    override val oldestInThread: StateFlow<Message?> = _oldestInThread!!

    fun setLoading(isLoading: Boolean) {
        _loading?.value = isLoading
    }

    fun setLoadingOlderMessages(isLoading: Boolean) {
        _loadingOlderMessages?.value = isLoading
    }

    fun setEndOfOlderMessages(isEnd: Boolean) {
        _endOfOlderMessages?.value = isEnd
    }

    fun setOldestInThread(message: Message?) {
        _oldestInThread?.value = message
    }

    fun deleteMessage(message: Message) {
        _messages?.apply { value -= message.id }
    }

    fun upsertMessages(messages: List<Message>) {
        _messages?.apply { value += messages.associateBy(Message::id) }
    }

    fun destroy() {
        _messages = null
        _loading = null
        _loadingOlderMessages = null
        _endOfOlderMessages = null
        _oldestInThread = null
    }
}
