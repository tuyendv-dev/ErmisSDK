package network.ermis.ui.common.recording

import android.media.MediaRecorder
import io.getstream.result.Result
import java.io.File

/**
 * A media recording interface, designed to simplify using [MediaRecorder].
 */
public interface ErmisMediaRecorder {

    /**
     * Creates a [File] internally and starts recording.
     * Calling the function again after a recording has already been started will reset the recording process.
     *
     * @param recordingName The file name the recording will be stored under.
     * @param amplitudePollingInterval Dictates how often the recorder is polled for the latest max amplitude and
     * how often [OnMaxAmplitudeSampled] emits a new value.
     * @param override Determines if the new recording file should override one with the same name, if it exists.
     *
     * @return The [File] to which the recording will be stored wrapped inside a [Result] if recording has
     * started successfully. Returns a [ChatError] wrapped inside a [Result] if the action had failed.
     */
    public fun startAudioRecording(
        recordingName: String,
        amplitudePollingInterval: Long = 100L,
        override: Boolean = true,
    ): Result<File>

    /**
     * Prepares the given [recordingFile] and starts recording.
     * Calling the function again after a recording has already been started will reset the recording process.
     *
     * @param recordingFile The [File] the audio will be saved to once the recording stops.
     * @param amplitudePollingInterval Dictates how often the recorder is polled for the latest max amplitude and
     * how often [OnMaxAmplitudeSampled] emits a new value.
     *
     * @return A Unit wrapped inside a [Result] if recording has started successfully. Returns a [ChatError] wrapped
     * inside [Result] if the action had failed.
     */
    public fun startAudioRecording(
        recordingFile: File,
        amplitudePollingInterval: Long = 100L,
    ): Result<Unit>

    /**
     * Stops recording and saves the recording to the file provided by [startAudioRecording].
     *
     * @return A Unit wrapped inside a [Result] if recording has been stopped successfully. Returns a [ChatError]
     * wrapped inside [Result] if the action had failed.
     */
    public fun stopRecording(): Result<RecordedMedia>

    /**
     * Deleted the recording to the file provided by [recordingFile].
     *
     * @param recordingFile The [File] to be deleted.
     *
     * @return A Unit wrapped inside a [Result] if recording has been deleted successfully. Returns a [ChatError]
     * wrapped inside [Result] if the action had failed.
     */
    public fun deleteRecording(recordingFile: File): Result<Unit>

    /**
     * Releases the [MediaRecorder] used by [ErmisMediaRecorder].
     */
    public fun release()

    // TODO add an onDestroy method, should kill the coroutine scope inside DefaultStreamMediaRecorder

    /**
     * Sets an error listener.
     *
     * @param onErrorListener [ErmisMediaRecorder.OnErrorListener] SAM used to notify the user about any underlying
     * [MediaRecorder] errors.
     */
    public fun setOnErrorListener(onErrorListener: OnErrorListener)

    /**
     * Sets an info listener.
     *
     * @param onInfoListener [ErmisMediaRecorder.OnInfoListener] SAM used to notify the user about any underlying
     * [MediaRecorder] information and warnings.
     */
    public fun setOnInfoListener(onInfoListener: OnInfoListener)

    /**
     * Sets a [ErmisMediaRecorder.OnRecordingStarted] listener on this instance of [ErmisMediaRecorder].
     *
     * @param onRecordingStarted [ErmisMediaRecorder.OnRecordingStarted] SAM used for notifying after the recording
     * has started successfully.
     */
    public fun setOnRecordingStartedListener(onRecordingStarted: OnRecordingStarted)

    /**
     * Sets a [ErmisMediaRecorder.OnRecordingStopped] listener on this instance of [ErmisMediaRecorder].
     *
     * @param onRecordingStopped [ErmisMediaRecorder.OnRecordingStarted] SAM used to notify the user after the
     * recording has stopped.
     */
    public fun setOnRecordingStoppedListener(onRecordingStopped: OnRecordingStopped)

    /**
     * Sets a [ErmisMediaRecorder.setOnMaxAmplitudeSampledListener] listener on this instance of [ErmisMediaRecorder].
     *
     * @param onMaxAmplitudeSampled [ErmisMediaRecorder.setOnMaxAmplitudeSampledListener] SAM used to notify when a new
     * maximum amplitude value has been sampled.
     */
    public fun setOnMaxAmplitudeSampledListener(onMaxAmplitudeSampled: OnMaxAmplitudeSampled)

    /**
     * Sets a [ErmisMediaRecorder.OnMediaRecorderStateChange] listener on this instance of [ErmisMediaRecorder].
     *
     * @param onMediaRecorderStateChange [ErmisMediaRecorder.OnMediaRecorderStateChange] SAM used to notify when the
     * media recorder state has changed.
     */
    public fun setOnMediaRecorderStateChangedListener(onMediaRecorderStateChange: OnMediaRecorderStateChange)

    /**
     * Sets a [ErmisMediaRecorder.OnCurrentRecordingDurationChanged] listener on this instance of
     * [ErmisMediaRecorder].
     *
     * @param onCurrentRecordingDurationChanged [ErmisMediaRecorder.OnCurrentRecordingDurationChanged] SAM updated
     * when the duration of the currently active recording has changed.
     */
    public fun setOnCurrentRecordingDurationChangedListener(
        onCurrentRecordingDurationChanged: OnCurrentRecordingDurationChanged,
    )

    /**
     * A functional interface used for listening to info events dispatched by the [MediaRecorder] internally
     * used by [ErmisMediaRecorder].
     */
    public fun interface OnInfoListener {

        /**
         * Called when the [MediaRecorder] used internally by [ErmisMediaRecorder] emits an info event.
         *
         * @param streamMediaRecorder The [ErmisMediaRecorder] instance this event is tied to.
         * @param what Error or info type.
         * @param extra An extra code, specific to the error or info type.
         */
        public fun onInfo(
            streamMediaRecorder: ErmisMediaRecorder,
            what: Int,
            extra: Int,
        )
    }

    /**
     * A functional interface used for listening to error events dispatched by the [MediaRecorder] internally
     * used by [ErmisMediaRecorder].
     */
    public fun interface OnErrorListener {

        /**
         * Called when the [MediaRecorder] used internally by [ErmisMediaRecorder] emits an error event.
         *
         * @param streamMediaRecorder The [ErmisMediaRecorder] instance this event is tied to.
         * @param what Error or info type.
         * @param extra An extra code, specific to the error or info type.
         */
        public fun onError(
            streamMediaRecorder: ErmisMediaRecorder,
            what: Int,
            extra: Int,
        )
    }

    /**
     * A functional interface used for notifying after the recording has started successfully.
     */
    public fun interface OnRecordingStarted {

        /**
         * Called after the recording has started successfully.
         */
        public fun onStarted()
    }

    /**
     * A functional interface used for notifying after the recording has stopped.
     */
    public fun interface OnRecordingStopped {

        /**
         * Called after the recording has stopped.
         */
        public fun onStopped()
    }

    /**
     * A functional interface used for emitting max amplitude readings during recording.
     */
    public fun interface OnMaxAmplitudeSampled {

        /**
         * Called after the recording has stopped.
         *
         * @param maxAmplitude The maximum amplitude value sampled since the previous sample.
         * @see [MediaRecorder.getMaxAmplitude]
         */
        public fun onSampled(maxAmplitude: Int)
    }

    /**
     * A functional interface used for listening to [ErmisMediaRecorder] state changes.
     */
    public fun interface OnMediaRecorderStateChange {

        /**
         * Called after the recording has stopped.
         *
         * @param recorderState The current state of the media recorder
         */
        public fun onStateChanged(recorderState: MediaRecorderState)
    }

    /**
     * A functional interface used for listening to updated in the duration of the currently active recording.
     */
    public fun interface OnCurrentRecordingDurationChanged {

        /**
         * Called after the recording has stopped.
         *
         * @param durationMs The duration of the currently active recording expressed in milliseconds.
         */
        public fun onDurationChanged(durationMs: Long)
    }
}
