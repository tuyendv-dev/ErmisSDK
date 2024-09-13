package network.ermis.client.utils

import io.getstream.result.Error

/**
 * Callback to listen for file upload status.
 */
public interface ProgressCallback {

    /**
     * Called when the attachment is uploaded successfully with an [url].
     */
    public fun onSuccess(url: String?)

    /**
     * Called when the attachment could not be uploaded due to cancellation, network problem or timeout etc
     * with an [error].
     *
     * @see Error
     */
    public fun onError(error: Error)

    /**
     * Called when the attachment upload is in progress with [bytesUploaded] count
     * and [totalBytes] in bytes of the file.
     */
    public fun onProgress(bytesUploaded: Long, totalBytes: Long)
}
