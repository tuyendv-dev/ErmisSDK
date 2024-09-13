package network.ermis.client.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class ErmisProject(

    @Json(name = "project_name")
    val project_name: String = "",

    @Json(name = "project_id")
    val project_id: String = "",

    @Json(name = "image")
    val image: String? = "",

    @Json(name = "description")
    val description: String? = "",

    var hasUnread: Boolean = false
)
