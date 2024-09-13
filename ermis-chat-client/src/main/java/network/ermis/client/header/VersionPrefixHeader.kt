package network.ermis.client.header

/**
 * An enumeration used for tracking which SDK is being used.
 *
 * @param prefix Header for particular SDK.
 */
public sealed class VersionPrefixHeader {
    public abstract val prefix: String

    /**
     * Low-level client.
     */
    public data object Default : VersionPrefixHeader() {
        override val prefix: String = "ermis-chat-android-"
    }

    /**
     * XML based UI components.
     */
    public data object UiComponents : VersionPrefixHeader() {
        override val prefix: String = "ermis-chat-ui-components-"
    }

}
