
package network.ermis.sample.feature.chat.info.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import network.ermis.client.ErmisClient
import network.ermis.client.api.model.response.AttachmentGet
import network.ermis.client.api.model.response.isImage
import network.ermis.client.api.model.response.isVideo
import network.ermis.client.utils.extensions.getCreatedAtOrThrow
import network.ermis.core.models.Message
import io.getstream.result.Result
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class ChatInfoSharedAttachmentsViewModel(
    private val cid: String,
    private val attachmentsType: AttachmentsType,
    private val chatClient: ErmisClient = ErmisClient.instance(),
) : ViewModel() {

    private val channelClient = chatClient.channel(cid)
    private val _state: MutableLiveData<State> = MutableLiveData(INITIAL_STATE)
    private var isLoadingMore: Boolean = false
    val state: LiveData<State> = _state
    private var messages: List<Message> = listOf()

    init {
        viewModelScope.launch {
            fetchAttachments()
        }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.LoadMoreRequested -> loadMore()
        }
    }

    private fun loadMore() {
    //     viewModelScope.launch {
    //         val currentState = _state.value!!
    //
    //         if (!currentState.canLoadMore || isLoadingMore) {
    //             return@launch
    //         }
    //
    //         isLoadingMore = true
    //         fetchAttachments()
    //         isLoadingMore = false
    //     }
    }

    private suspend fun fetchAttachments() {
        val result = channelClient.getAttachmentsOfChannel().await()
        when (result) {
            is Result.Success -> {
                val attachments = result.value.attachments
                _state.value = State(
                    results = mapAttachments(attachments),
                    isLoading = false,
                    canLoadMore = false //newMessages.size == QUERY_LIMIT,
                )
            }
            is Result.Failure -> _state.value = State(
                results = emptyList(),
                isLoading = false,
                canLoadMore = true,
            )
        }


        // val result = channelClient.getMessagesWithAttachments(
        //     offset = messages.size,
        //     limit = QUERY_LIMIT,
        //     types = listOf(attachmentsType.requestTypeKey),
        // ).await()
        //
        // when (result) {
        //     is Result.Success -> {
        //         val newMessages = result.value
        //         messages = messages + newMessages
        //         _state.value = State(
        //             results = mapAttachments(messages),
        //             isLoading = false,
        //             canLoadMore = newMessages.size == QUERY_LIMIT,
        //         )
        //     }
        //     is Result.Failure -> _state.value = State(
        //         results = mapAttachments(messages),
        //         isLoading = false,
        //         canLoadMore = true,
        //     )
        // }
    }

    private fun mapAttachments(list: List<AttachmentGet>): List<SharedAttachment> {
        return if (attachmentsType == AttachmentsType.FILES) {
            list.filter { it.isImage().not() && it.isVideo().not()}
                .map { attachmentGet ->
                    SharedAttachment.AttachmentGetItem(attachmentGet)
                }
        } else {
            list.filter { it.isImage() || it.isVideo()}
                .map { attachmentGet ->
                    SharedAttachment.AttachmentGetItem(attachmentGet)
                }
        }
    }

    // private fun mapAttachments(messages: List<Message>): List<SharedAttachment> {
    //     return if (attachmentsType == AttachmentsType.FILES) {
    //         messages
    //             .groupBy { mapDate(it.getCreatedAtOrThrow()) }
    //             .flatMap { (date, messages) ->
    //                 listOf(SharedAttachment.DateDivider(date)) + messages.toAttachmentItems(attachmentsType)
    //             }
    //     } else {
    //         messages.toAttachmentItems(attachmentsType)
    //     }
    // }

    private fun mapDate(date: Date): Date {
        // We only care about year and month
        return Calendar.getInstance().apply {
            time = date
            val year = get(Calendar.YEAR)
            val month = get(Calendar.MONTH)
            clear()
            set(year, month, getActualMinimum(Calendar.DAY_OF_MONTH))
        }.time
    }

    data class State(
        val canLoadMore: Boolean,
        val results: List<SharedAttachment>,
        val isLoading: Boolean,
    )

    sealed class Action {
        object LoadMoreRequested : Action()
    }

    enum class AttachmentsType(val requestTypeKey: String) {
        MEDIA("image"),
        FILES("file"),
    }

    companion object {
        private const val QUERY_LIMIT = 30
        private val INITIAL_STATE = State(
            canLoadMore = false,
            results = emptyList(),
            isLoading = true,
        )
    }
}

class ChatInfoSharedAttachmentsViewModelFactory(
    private val cid: String,
    private val type: ChatInfoSharedAttachmentsViewModel.AttachmentsType,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == ChatInfoSharedAttachmentsViewModel::class.java) {
            "ChatInfoSharedAttachmentsViewModelFactory can only create instances of ChatInfoSharedAttachmentsViewModel"
        }

        @Suppress("UNCHECKED_CAST")
        return ChatInfoSharedAttachmentsViewModel(cid, type) as T
    }
}

private fun List<Message>.toAttachmentItems(attachmentType: ChatInfoSharedAttachmentsViewModel.AttachmentsType): List<SharedAttachment.AttachmentItem> =
    flatMap { message ->
        message.attachments
            .filter { it.type == attachmentType.requestTypeKey }
            .map { SharedAttachment.AttachmentItem(message, message.getCreatedAtOrThrow(), it) }
    }
