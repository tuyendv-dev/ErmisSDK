package network.ermis.ui.common.recording

import android.media.MediaRecorder

/**
 * Represents the current state of the [MediaRecorder].
 */
public enum class MediaRecorderState {
    /**
     * The media recorder has not yet been set up at this point and cannot record.
     */
    UNINITIALIZED,

    /**
     * The media recorder has been set up and is ready to record.
     */
    PREPARED,

    /**
     * The media recorder is currently recording.
     */
    RECORDING,
}
