package network.ermis.ui.common.utils

import network.ermis.core.models.Constants

/**
 * Various constants regarding default attachment limits.
 */
public object AttachmentConstants {

    /**
     * Default max upload size in MB.
     */
    public const val MAX_UPLOAD_SIZE_IN_MB: Int = 200

    /**
     * Default max upload size in bytes.
     */
    public const val MAX_UPLOAD_FILE_SIZE: Long = Constants.MB_IN_BYTES * MAX_UPLOAD_SIZE_IN_MB

    /**
     * Default max number of attachments.
     */
    public const val MAX_ATTACHMENTS_COUNT: Int = 30
}
