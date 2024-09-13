package network.ermis.core.models

/**
 * An enumeration of various network types used as a constraint in upload attachments worker.
 */
public enum class UploadAttachmentsNetworkType {
    /**
     * Any working network connection is required.
     */
    CONNECTED,

    /**
     * An unmetered network connection is required.
     */
    UNMETERED,

    /**
     * A non-roaming network connection is required.
     */
    NOT_ROAMING,

    /**
     * A metered network connection is required.
     */
    METERED,
}
