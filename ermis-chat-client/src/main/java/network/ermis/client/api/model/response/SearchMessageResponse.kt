package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
internal data class SearchMessageResponse(
    val search_result: SearchMessageRes,
)

@JsonClass(generateAdapter = true)
internal data class SearchMessageRes(
    val messages: List<MessageSearch>,
    val limit: Int,
    val offset: Int,
    val total: Int
)

@JsonClass(generateAdapter = true)
internal data class MessageSearch(
    val id: String,
    val text: String,
    val user_id: String,
    val created_at: Date,
)