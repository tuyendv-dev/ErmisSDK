package network.ermis.client.user

/**
 * Data class that contains credentials of the current user.
 */
public class CredentialConfig(
    /**
     * Id of the current user.
     */
    public val userId: String,
    /**
     * Api token of the current user.
     */
    public val userToken: String,
    /**
     * Name of the current user.
     */
    public val userName: String,
    /**
     * The user is anonymous or not
     */
    public val isAnonymous: Boolean,
) {
    internal fun isValid(): Boolean = userId.isNotEmpty() && userToken.isNotEmpty()
}
