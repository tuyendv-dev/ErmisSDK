package network.ermis.client.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class ErmisChain(

    @Json(name = "chain_id")
    val chain_id: Int = 0,

    @Json(name = "clients")
    val clients: List<ErmisClientModel> = listOf(),
)

@JsonClass(generateAdapter = true)
public data class ErmisChainResponse(

    @Json(name = "chains")
    val chains: List<Int> = listOf(),

    @Json(name = "joined")
    val joined: List<ErmisChain> = listOf(),

    @Json(name = "not_joined")
    val notJoined: List<ErmisChain> = listOf(),
)
