package network.ermis.ui.view.messages.adapter.view.internal

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import network.ermis.client.ErmisClient
import network.ermis.client.audio.AudioPlayer
import network.ermis.client.audio.AudioState
import network.ermis.client.audio.WaveformExtractor
import network.ermis.client.utils.extensions.duration
import network.ermis.client.utils.extensions.waveformData
import network.ermis.client.utils.attachment.isAudioRecording
import network.ermis.core.models.Attachment
import network.ermis.ui.common.utils.DurationFormatter
import network.ermis.ui.view.messages.common.AudioRecordPlayerViewStyle
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.dpToPx
import io.getstream.log.taggedLogger

private const val NULL_DURATION = 0.0f

/**
 * A LinearLayoutCompat that present the list of audio messages.
 */
@Suppress("MagicNumber")
internal class AudioRecordingAttachmentsGroupView : LinearLayoutCompat {

    public constructor(context: Context) : super(context.createStreamThemeWrapper())
    public constructor(context: Context, attrs: AttributeSet?) : super(context.createStreamThemeWrapper(), attrs)
    public constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
    ) : super(context.createStreamThemeWrapper(), attrs, defStyleAttr)

    init {
        setPadding(2.dpToPx(), 0.dpToPx(), 2.dpToPx(), 2.dpToPx())
    }

    var attachmentClickListener: AttachmentClickListener? = null
    var attachmentLongClickListener: AttachmentLongClickListener? = null

    private val logger by taggedLogger("AudioRecAttachGroupView")

    private var audioAttachments: List<Attachment>? = null

    private val extractor by lazy(LazyThreadSafetyMode.NONE) {
        WaveformExtractor(context, "key", 100) { extractor, progress ->
            if (progress >= 1.0f) {
                logger.v { "[onProgress] progress: $progress, sampleData: ${extractor.sampleData}" }
                if (childCount > 0) {
                    val playerView = getChildAt(0) as AudioRecordPlayerView
                    playerView.setWaveBars(extractor.sampleData)
                    playerView.invalidate()
                    playerView.requestLayout()
                }
            }
        }
    }

    private var style: AudioRecordPlayerViewStyle? = null

    fun setStyle(style: AudioRecordPlayerViewStyle) {
        this.style = style
        children.forEach {
            if (it is AudioRecordPlayerView) {
                it.setStyle(style)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        logger.d { "[onAttachedToWindow] audioAttachments.size: ${audioAttachments?.size}" }
        val audioPlayer = ErmisClient.instance().audioPlayer
        val audioHashes = audioAttachments?.map { it.hashCode() }?.toHashSet() ?: return
        for (child in children) {
            if (child !is AudioRecordPlayerView) continue
            val audioHash = child.audioHash ?: continue
            if (audioHash !in audioHashes) continue
            audioPlayer.registerStateChange(child, audioHash)
            logger.v { "[onAttachedToWindow] restored (audioHash: $audioHash)" }
        }
    }

    /**
     * Shows audio track.
     *
     * @param attachments attachments of type "audio_recording".
     */
    public fun showAudioAttachments(attachments: List<Attachment>) {
        logger.d { "[showAudioAttachments] attachments.size: ${attachments.size}" }
        resetCurrentAttachments()
        removeAllViews()

        val audiosAttachment = attachments.filter { attachment -> attachment.isAudioRecording() }
        this.audioAttachments = audiosAttachment

        audiosAttachment.forEachIndexed(::addAttachmentPlayerView)
    }

    private fun addAttachmentPlayerView(index: Int, attachment: Attachment) {
        logger.d { "[addAttachmentPlayerView] index: $index" }
        // attachment.assetUrl?.also {
        //     extractor.start(it)
        // }

        AudioRecordPlayerView(context).apply {
            attachment.duration
                ?.let(DurationFormatter::formatDurationInSeconds)
                ?.let(this::setTotalDuration)

            logger.i { "[addAttachmentPlayerView] waveformData: ${attachment.waveformData}" }
            attachment.waveformData?.let(::setWaveBars)
        }.let { playerView ->
            setOnClickListener { attachmentClickListener?.onAttachmentClick(attachment) }
            setOnLongClickListener {
                attachmentLongClickListener?.onAttachmentLongClick()
                true
            }

            addView(playerView)

            if (index > 0) {
                playerView.updateLayoutParams<MarginLayoutParams> {
                    topMargin = 2.dpToPx()
                }
            }

            val audioPlayer = ErmisClient.instance().audioPlayer
            val audioHash = attachment.hashCode()

            audioPlayer.registerStateChange(playerView, audioHash)
            playerView.registerButtonsListeners(audioPlayer, attachment, audioHash)
            playerView.audioHash = audioHash

            style?.also { playerView.setStyle(it) }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        logger.d { "[onDetachedFromWindow] no args" }
        resetCurrentAttachments()
    }

    private fun resetCurrentAttachments() {
        val audioAttachments = audioAttachments ?: return
        logger.d { "[resetCurrentAttachments] no args" }
        val audioPlayer = ErmisClient.instance().audioPlayer
        audioAttachments.forEach { attachment ->
            val audioHash = attachment.hashCode()
            logger.v { "[resetCurrentAttachments] audioHash: $audioHash" }
            audioPlayer.resetAudio(audioHash)
        }
    }

    private fun AudioPlayer.registerStateChange(playerView: AudioRecordPlayerView, audioHash: Int) {
        logger.d { "[registerStateChange] audioHash: $audioHash" }
        registerOnAudioStateChange(audioHash) { audioState ->
            logger.d { "[onAudioStateChange] audioHash: $audioHash, audioState: $audioState" }
            when (audioState) {
                AudioState.LOADING -> playerView.setLoading()
                AudioState.PAUSE -> playerView.setPaused()
                AudioState.UNSET, AudioState.IDLE -> playerView.setIdle()
                AudioState.PLAYING -> playerView.setPlaying()
            }
        }
        registerOnProgressStateChange(audioHash) { (duration, progress) ->
            playerView.setDuration(DurationFormatter.formatDurationInMillis(duration))
            // TODO
            playerView.setProgress(progress.toDouble())
        }
        registerOnSpeedChange(audioHash, playerView::setSpeedText)
    }

    private fun AudioRecordPlayerView.registerButtonsListeners(
        audioPlayer: AudioPlayer,
        attachment: Attachment,
        audioHash: Int,
    ) {
        logger.d { "[registerButtonsListeners] audioHash: $audioHash" }
        setOnPlayButtonClickListener {
            logger.v { "[onPlayButtonClick] audioHash: $audioHash" }
            audioPlayer.clearTracks()
            audioAttachments?.forEachIndexed { index, attachment ->
                attachment.assetUrl?.also {
                    val curAudioHash = it.hashCode()
                    audioPlayer.registerTrack(it, curAudioHash, index)
                }
            }

            val assetUrl = attachment.assetUrl
            if (assetUrl != null) {
                audioPlayer.play(assetUrl, audioHash)
            } else {
                setLoading()
            }
        }

        setOnSpeedButtonClickListener {
            logger.v { "[onSpeedButtonClick] audioHash: $audioHash" }
            audioPlayer.changeSpeed()
        }

        setOnSeekbarMoveListeners({
            logger.v { "[onSeekBarStart] audioHash: $audioHash" }
            audioPlayer.startSeek(attachment.hashCode())
        }, { progress ->
            val durationInSeconds = attachment.duration ?: NULL_DURATION
            val positionInMs = progressToMillis(progress, durationInSeconds)
            logger.v { "[onSeekBarStop] audioHash: $audioHash, progress: $progress, duration: $durationInSeconds" }
            audioPlayer.seekTo(
                positionInMs,
                attachment.hashCode(),
            )
        })
    }

    private fun progressToMillis(progress: Int, durationInSeconds: Float): Int {
        val durationInMs = durationInSeconds * 1000
        return (progress * durationInMs / 100).toInt()
    }

    /**
     * Unbinds the view.
     */
    public fun unbind() {
        // extractor.stop()
        audioAttachments?.map { attachment -> attachment.hashCode() }
            ?.let(ErmisClient.instance().audioPlayer::removeAudios)
    }
}
