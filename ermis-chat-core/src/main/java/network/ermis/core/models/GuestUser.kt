package network.ermis.core.models

import androidx.compose.runtime.Immutable
import network.ermis.core.models.User

@Immutable
public data class GuestUser(val user: User, val token: String)
