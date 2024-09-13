package network.ermis.client.notifications

import com.google.gson.Gson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import network.ermis.client.api.mapping.toDomain
import network.ermis.client.api.model.dto.NewMessageEventDto
import network.ermis.client.events.NewMessageEvent
import network.ermis.client.parser.adapters.AttachmentDtoAdapter
import network.ermis.client.parser.adapters.DateAdapter
import network.ermis.client.parser.adapters.DownstreamChannelDtoAdapter
import network.ermis.client.parser.adapters.DownstreamMessageDtoAdapter
import network.ermis.client.parser.adapters.DownstreamModerationDetailsDtoAdapter
import network.ermis.client.parser.adapters.DownstreamReactionDtoAdapter
import network.ermis.client.parser.adapters.DownstreamUserDtoAdapter
import network.ermis.client.parser.adapters.EventAdapterFactory
import network.ermis.client.parser.adapters.ExactDateAdapter
import network.ermis.client.parser.adapters.UpstreamChannelDtoAdapter
import network.ermis.client.parser.adapters.UpstreamMessageDtoAdapter
import network.ermis.client.parser.adapters.UpstreamReactionDtoAdapter
import network.ermis.client.parser.adapters.UpstreamUserDtoAdapter

internal object PartEventPushMoshi {

    val gson = Gson()

    private val moshi = Moshi.Builder()
        .addAdapter(DateAdapter())
        .addAdapter(ExactDateAdapter())
        .add(EventAdapterFactory())
        .add(DownstreamMessageDtoAdapter)
        .add(DownstreamModerationDetailsDtoAdapter)
        .add(UpstreamMessageDtoAdapter)
        .add(DownstreamChannelDtoAdapter)
        .add(UpstreamChannelDtoAdapter)
        .add(AttachmentDtoAdapter)
        .add(DownstreamReactionDtoAdapter)
        .add(UpstreamReactionDtoAdapter)
        .add(DownstreamUserDtoAdapter)
        .add(UpstreamUserDtoAdapter)
        .build()

    private inline fun <reified T> Moshi.Builder.addAdapter(adapter: JsonAdapter<T>) = apply {
        this.add(T::class.java, adapter)
    }

    private val newMessageEventAdapter = moshi.adapter(NewMessageEventDto::class.java)

    fun getNewMessageEvent(json: String): NewMessageEvent {
        return newMessageEventAdapter.fromJson(json)!!.toDomain() as NewMessageEvent
    }

}