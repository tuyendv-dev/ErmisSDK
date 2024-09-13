package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.SearchWarningDto
import network.ermis.client.api.model.response.MessageResponse

@JsonClass(generateAdapter = true)
internal data class SearchMessagesResponse(
    val results: List<MessageResponse>,
    val next: String?,
    val previous: String?,
    val resultsWarning: SearchWarningDto?,
)
