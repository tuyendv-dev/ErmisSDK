package network.ermis.offline.repository.domain.queryChannels

import androidx.room.Entity
import androidx.room.PrimaryKey
import network.ermis.core.models.Channel
import network.ermis.core.models.FilterObject
import network.ermis.core.models.querysort.QuerySorter

@Entity(tableName = QUERY_CHANNELS_ENTITY_TABLE_NAME)
internal data class QueryChannelsEntity(
    @PrimaryKey
    var id: String,
    val filter: FilterObject,
    val querySort: QuerySorter<Channel>,
    val cids: List<String>,
)

internal const val QUERY_CHANNELS_ENTITY_TABLE_NAME = "stream_channel_query"
