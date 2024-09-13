package network.ermis.client.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
public data class AttachmentResponse(val attachments: List<AttachmentGet>)

@JsonClass(generateAdapter = true)
public data class AttachmentGet(

    @Json(name = "id")
    val id: String = "",

    @Json(name = "user_id")
    val user_id: String = "",

    @Json(name = "cid")
    val cid: String = "",

    @Json(name = "url")
    var url: String = "",

    @Json(name = "thumb_url")
    var thumb_url: String = "",

    @Json(name = "file_name")
    var file_name: String = "",

    @Json(name = "content_type")
    var content_type: String = "",

    @Json(name = "content_length")
    var content_length: Long = 0,

    @Json(name = "content_disposition")
    var content_disposition: String = "",

    @Json(name = "message_id")
    var message_id: String = "",

    @Json(name = "created_at")
    val created_at: Date? = null,

    @Json(name = "updated_at")
    val updated_at: Date? = null,
)

public fun AttachmentGet.isVideo() : Boolean {
    return content_type.contains("video", true)
}

public fun AttachmentGet.isImage() : Boolean {
    return content_type.contains("image", true)
}
