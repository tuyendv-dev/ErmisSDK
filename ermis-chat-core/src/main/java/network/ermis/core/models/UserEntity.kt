package network.ermis.core.models

public sealed interface UserEntity {

    public val user: User

    public fun getUserId(): String {
        return user.id
    }
}
