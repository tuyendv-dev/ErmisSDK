package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ListContactIdsResponse(val project_id_user_ids: Map<String, List<String>>)