package network.ermis.core.models

import androidx.compose.runtime.Immutable

@Immutable
public data class TypingEvent(val channelId: String, val users: List<User>)