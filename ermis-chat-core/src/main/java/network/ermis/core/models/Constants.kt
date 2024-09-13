package network.ermis.core.models

/**
 * Represents constants used across all SDKs.
 */
public object Constants {

    /**
     * Number of bytes in a megabyte.
     */
    public const val MB_IN_BYTES: Long = 1024 * 1024

    /**
     * Maximum request body length in bytes.
     */
    public const val MAX_REQUEST_BODY_LENGTH: Long = 1 * MB_IN_BYTES
}
