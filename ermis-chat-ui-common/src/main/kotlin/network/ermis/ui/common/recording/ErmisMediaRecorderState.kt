package network.ermis.ui.common.recording

import android.media.MediaRecorder

/**
 * Holds information about the current state of the [MediaRecorder] used by [ErmisMediaRecorder].
 * The values correspond to the info and error values found inside [MediaRecorder].
 *
 * @param streamMediaRecorder The [ErmisMediaRecorder] instance this event is tied to.
 * @param what Error or info type.
 * @param extra An extra code, specific to the error or info type.
 */
public class ErmisMediaRecorderState(
    private val streamMediaRecorder: ErmisMediaRecorder,
    private val what: Int,
    private val extra: Int,
)
