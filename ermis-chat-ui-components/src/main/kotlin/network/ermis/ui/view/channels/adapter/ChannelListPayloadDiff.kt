
package network.ermis.ui.view.channels.adapter

public data class ChannelListPayloadDiff(
    val nameChanged: Boolean,
    val avatarViewChanged: Boolean,
    val usersChanged: Boolean,
    val lastMessageChanged: Boolean,
    val readStateChanged: Boolean,
    val unreadCountChanged: Boolean,
    val extraDataChanged: Boolean,
    val typingUsersChanged: Boolean,
) {
    public fun hasDifference(): Boolean =
        nameChanged
            .or(avatarViewChanged)
            .or(usersChanged)
            .or(lastMessageChanged)
            .or(readStateChanged)
            .or(unreadCountChanged)
            .or(extraDataChanged)
            .or(typingUsersChanged)

    public operator fun plus(other: ChannelListPayloadDiff): ChannelListPayloadDiff =
        copy(
            nameChanged = nameChanged || other.nameChanged,
            avatarViewChanged = avatarViewChanged || other.avatarViewChanged,
            usersChanged = usersChanged || other.usersChanged,
            lastMessageChanged = lastMessageChanged || other.lastMessageChanged,
            readStateChanged = readStateChanged || other.readStateChanged,
            unreadCountChanged = unreadCountChanged || other.unreadCountChanged,
            extraDataChanged = extraDataChanged || other.extraDataChanged,
            typingUsersChanged = typingUsersChanged || other.typingUsersChanged,
        )
}
