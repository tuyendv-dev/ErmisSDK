package network.ermis.core.models

import androidx.compose.runtime.Immutable

@Immutable
public data class GuestUser(val user: User, val token: String)
