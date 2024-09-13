package network.ermis.ui.common.utils.extensions

import network.ermis.core.models.User

public val User.initials: String
    get() = name.initials()
