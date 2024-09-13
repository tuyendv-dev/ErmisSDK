
package network.ermis.ui.view.messages.composer.attachment.preview.factory

import android.view.ViewGroup
import androidx.core.net.toUri
import network.ermis.client.ErmisClient
import network.ermis.client.audio.AudioPlayer
import network.ermis.client.audio.AudioState
import network.ermis.client.utils.extensions.duration
import network.ermis.client.utils.extensions.waveformData
import network.ermis.client.utils.attachment.isAudioRecording
import network.ermis.core.models.Attachment
import network.ermis.ui.common.utils.DurationFormatter
import network.ermis.ui.components.databinding.AudioRecordPlayerPreviewBinding
import network.ermis.ui.view.messages.composer.MessageComposerViewStyle
import network.ermis.ui.view.messages.composer.attachment.preview.AttachmentPreviewViewHolder
import network.ermis.ui.view.messages.adapter.view.internal.AudioRecordPlayerView
import network.ermis.ui.utils.extensions.streamThemeInflater
import io.getstream.log.taggedLogger

private const val NULL_DURATION = 0.0f

/**
 * The default [AttachmentPreviewFactory] for file attachments.
 */
public class AudioRecordAttachmentPreviewFactory : AttachmentPreviewFactory {

    private val logger by taggedLogger("AttachRecordPreviewFactory")

    /**
     * Checks if the factory can create a preview ViewHolder for this attachment.
     *
     * @param attachment The attachment we want to show a preview for.
     * @return True if the factory is able to provide a preview for the given [Attachment].
     */
    public override fun canHandle(attachment: Attachment): Boolean {
        logger.i { "[canHandle] isAudioRecording: ${attachment.isAudioRecording()}; $attachment" }
        return attachment.isAudioRecording()
    }

    /**
     * Creates and instantiates a new instance of [AudioRecordAttachmentPreviewFactory].
     *
     * @param parentView The parent container.
     * @param attachmentRemovalListener Click listener for the remove attachment button.
     * @param style Used to style the factory. If null, the factory will retain
     * the default appearance.
     *
     * @return An instance of attachment preview ViewHolder.
     */
    override fun onCreateViewHolder(
        parentView: ViewGroup,
        attachmentRemovalListener: (Attachment) -> Unit,
        style: MessageComposerViewStyle?,
    ): AttachmentPreviewViewHolder =
        AudioRecordPlayerPreviewBinding
            .inflate(parentView.context.streamThemeInflater, parentView, false)
            .let { AudioRecordAttachmentPreviewViewHolder(it, attachmentRemovalListener, style) }

    /**
     * A ViewHolder for file attachment preview.
     *
     * @param binding Binding generated for the layout.
     * @param attachmentRemovalListener Click listener for the remove attachment button.
     */
    private class AudioRecordAttachmentPreviewViewHolder(
        private val binding: AudioRecordPlayerPreviewBinding,
        attachmentRemovalListener: (Attachment) -> Unit,
        private val style: MessageComposerViewStyle?,
    ) : AttachmentPreviewViewHolder(binding.root) {

        private val logger by taggedLogger("AttachRecordPreviewHolder")

        private lateinit var attachment: Attachment

        init {
            binding.removeButton.setOnClickListener { attachmentRemovalListener(attachment) }
            style?.audioRecordPlayerViewStyle?.also {
                binding.playerView.setStyle(it)
            }
        }

        override fun bind(attachment: Attachment) {
            if (attachment.upload == null) return
            logger.d { "[bind] attachment: $attachment" }
            val audioPlayer = ErmisClient.instance().audioPlayer
            val playerView = binding.playerView

            this.attachment = attachment

            attachment.duration
                ?.let(DurationFormatter::formatDurationInSeconds)
                ?.let { duration ->
                    logger.v { "[bind] duration: $duration" }
                    playerView.setDuration(duration)
                }

            attachment.waveformData?.let { waveBars ->
                playerView.setWaveBars(waveBars)
            }

            audioPlayer.registerStateChange(playerView, attachment.hashCode())
            playerView.registerButtonsListeners(audioPlayer, attachment, attachment.hashCode())
        }

        override fun unbind() {
            ErmisClient.instance().audioPlayer.removeAudios(listOf(attachment.hashCode()))
        }

        private fun AudioPlayer.registerStateChange(playerView: AudioRecordPlayerView, hashCode: Int) {
            registerOnAudioStateChange(hashCode) { audioState ->
                when (audioState) {
                    AudioState.LOADING -> playerView.setLoading()
                    AudioState.PAUSE -> playerView.setPaused()
                    AudioState.UNSET, AudioState.IDLE -> playerView.setIdle()
                    AudioState.PLAYING -> playerView.setPlaying()
                }
            }
            registerOnProgressStateChange(hashCode) { (duration, progress) ->
                playerView.setDuration(DurationFormatter.formatDurationInMillis(duration))
                playerView.setProgress(progress.toDouble())
            }
            registerOnSpeedChange(hashCode, playerView::setSpeedText)
        }

        private fun AudioRecordPlayerView.registerButtonsListeners(
            audioPlayer: AudioPlayer,
            attachment: Attachment,
            hashCode: Int,
        ) {
            setOnPlayButtonClickListener {
                val audioFile = attachment.upload ?: run {
                    logger.w { "[toggleRecordingPlayback] rejected (audioFile is null)" }
                    return@setOnPlayButtonClickListener
                }
                val fileUri = audioFile.toUri().toString()
                audioPlayer.play(fileUri, hashCode)
            }

            setOnSpeedButtonClickListener {
                audioPlayer.changeSpeed()
            }

            setOnSeekbarMoveListeners({
                audioPlayer.startSeek(attachment.hashCode())
            }, { progress ->
                audioPlayer.seekTo(
                    progressToDecimal(progress, attachment.duration),
                    attachment.hashCode(),
                )
            })
        }
    }
}

@Suppress("MagicNumber")
private fun progressToDecimal(progress: Int, totalDuration: Float?): Int =
    (progress * (totalDuration ?: NULL_DURATION) / 100).toInt()
