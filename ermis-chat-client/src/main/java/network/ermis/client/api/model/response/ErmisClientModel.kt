package network.ermis.client.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class ErmisClientModel(

    @Json(name = "client_name")
    val client_name: String = "",

    @Json(name = "client_id")
    val client_id: String = "",

    @Json(name = "client_image")
    val client_avatar: String? = "",

    @Json(name = "projects")
    var projects: List<ErmisProject> = listOf(),

    @Json(name = "showProjects")
    var showProjects: Boolean? = true
)
