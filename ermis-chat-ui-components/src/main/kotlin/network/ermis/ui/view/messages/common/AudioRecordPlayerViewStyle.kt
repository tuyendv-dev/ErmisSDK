package network.ermis.ui.view.messages.common

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import network.ermis.ui.components.R
import network.ermis.ui.view.messages.background.ShapeAppearanceModelFactory
import network.ermis.ui.font.TextStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewPadding
import network.ermis.ui.helper.ViewSize
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.applyTint
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getColorOrNull
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.getDrawableCompat
import network.ermis.ui.utils.extensions.use

/**
 * Style for [io.getstream.chat.android.ui.feature.messages.list.adapter.view.internal.AudioRecordPlayerView].
 *
 * @param height Height of the view.
 * @param padding Padding of the view.
 * @param backgroundDrawable Background drawable of the view.
 * @param backgroundDrawableTint Background drawable tint of the view.
 * @param playbackProgressContainerSize Size of the playback progress container.
 * @param playbackButtonSize Size of the playback button.
 * @param playbackButtonPadding Padding of the playback button.
 * @param playbackButtonElevation Elevation of the playback button.
 * @param playbackButtonBackground Background of the playback button.
 * @param playbackButtonBackgroundTint Background tint of the playback button.
 * @param playIconDrawable Play icon drawable.
 * @param playIconDrawableTint Play icon drawable tint.
 * @param pauseIconDrawable Pause icon drawable.
 * @param pauseIconDrawableTint Pause icon drawable tint.
 * @param progressBarDrawable Progress bar drawable.
 * @param progressBarDrawableTint Progress bar drawable tint.
 * @param progressBarSize Size of the progress bar.
 * @param durationTextViewSize Size of the duration text view.
 * @param durationTextMarginStart Margin start of the duration text view.
 * @param durationTextStyle Style of the duration text.
 * @param waveBarHeight Height of the wave bar.
 * @param waveBarMarginStart Margin start of the wave bar.
 * @param waveBarColorPlayed Color of the played wave bar.
 * @param waveBarColorFuture Color of the future wave bar.
 * @param scrubberDrawable Scrubber drawable.
 * @param scrubberDrawableTint Scrubber drawable tint.
 * @param scrubberWidthDefault Default width of the scrubber.
 * @param scrubberWidthPressed Pressed width of the scrubber.
 * @param isFileIconContainerVisible Is file icon container visible.
 * @param fileIconContainerWidth Width of the file icon container.
 * @param audioFileIconDrawable Audio file icon drawable.
 * @param speedButtonSize Size of the speed button.
 * @param speedButtonElevation Elevation of the speed button.
 * @param speedButtonBackground Background of the speed button.
 * @param speedButtonBackgroundTint Background tint of the speed button.
 * @param speedButtonTextStyle Style of the speed button text.
 */
public data class AudioRecordPlayerViewStyle(
    @Px public val height: Int,
    public val padding: ViewPadding,
    public val backgroundDrawable: Drawable?,
    @ColorInt public val backgroundDrawableTint: Int?,
    public val playbackProgressContainerSize: ViewSize,
    public val playbackButtonSize: ViewSize,
    public val playbackButtonPadding: ViewPadding,
    @Px public val playbackButtonElevation: Int,
    public val playbackButtonBackground: Drawable?,
    @ColorInt public val playbackButtonBackgroundTint: Int?,
    public val progressBarDrawable: Drawable?,
    @ColorInt public val progressBarDrawableTint: Int?,
    public val progressBarSize: ViewSize,
    public val playIconDrawable: Drawable?,
    @ColorInt public val playIconDrawableTint: Int?,
    public val pauseIconDrawable: Drawable?,
    @ColorInt public val pauseIconDrawableTint: Int?,
    public val durationTextViewSize: ViewSize,
    @Px public val durationTextMarginStart: Int,
    public val durationTextStyle: TextStyle,
    @Px public val waveBarHeight: Int,
    @Px public val waveBarMarginStart: Int,
    @ColorInt public val waveBarColorPlayed: Int,
    @ColorInt public val waveBarColorFuture: Int,
    public val scrubberDrawable: Drawable?,
    @ColorInt public val scrubberDrawableTint: Int?,
    @Px public val scrubberWidthDefault: Int,
    @Px public val scrubberWidthPressed: Int,
    public val isFileIconContainerVisible: Boolean,
    @Px public val fileIconContainerWidth: Int,
    public val audioFileIconDrawable: Drawable?,
    public val speedButtonTextStyle: TextStyle,
    public val speedButtonBackground: Drawable?,
    @ColorInt public val speedButtonBackgroundTint: Int?,
    public val speedButtonSize: ViewSize,
    @Px public val speedButtonElevation: Int,
) : ViewStyle {

    val tintedBackgroundDrawable: Drawable?
        get() = backgroundDrawable?.applyTint(backgroundDrawableTint)

    val tintedPlaybackButtonBackground: Drawable?
        get() = playbackButtonBackground?.applyTint(playbackButtonBackgroundTint)

    val tintedProgressBarDrawable: Drawable?
        get() = progressBarDrawable?.applyTint(progressBarDrawableTint)

    val tintedPlayIconDrawable: Drawable?
        get() = playIconDrawable?.applyTint(playIconDrawableTint)

    val tintedPauseIconDrawable: Drawable?
        get() = pauseIconDrawable?.applyTint(pauseIconDrawableTint)

    val tintedScrubberDrawable: Drawable?
        get() = scrubberDrawable?.applyTint(scrubberDrawableTint)

    val tintedSpeedButtonBackground: Drawable?
        get() = speedButtonBackground?.applyTint(speedButtonBackgroundTint)

    public companion object {

        internal operator fun invoke(context: Context, attrs: AttributeSet?): AudioRecordPlayerViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.AudioRecordPlayerView,
                R.attr.ermisUiAudioRecordPlayerViewStyle,
                R.style.ermisUi_AudioRecordPlayerView,
            ).use {
                return invoke(context, it)
                    .let(TransformStyle.audioRecordPlayerViewStyle::transform)
            }
        }

        internal operator fun invoke(context: Context, attributes: TypedArray): AudioRecordPlayerViewStyle {
            val height = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerHeight,
                context.getDimension(R.dimen.ui_audio_record_player_height),
            )

            val paddingStart = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPaddingStart,
                context.getDimension(R.dimen.ui_audio_record_player_padding_start),
            )
            val paddingTop = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPaddingTop,
                context.getDimension(R.dimen.ui_audio_record_player_padding_top),
            )
            val paddingEnd = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPaddingEnd,
                context.getDimension(R.dimen.ui_audio_record_player_padding_end),
            )
            val paddingBottom = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPaddingBottom,
                context.getDimension(R.dimen.ui_audio_record_player_padding_bottom),
            )

            val backgroundDrawable = ShapeAppearanceModelFactory.audioBackground(context)
            val backgroundDrawableTint: Int? = null

            val playbackProgressContainerWidth = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPlaybackProgressContainerWidth,
                context.getDimension(R.dimen.ui_audio_record_player_playback_progress_container_width),
            )
            val playbackProgressContainerHeight = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPlaybackProgressContainerHeight,
                context.getDimension(R.dimen.ui_audio_record_player_playback_progress_container_height),
            )

            val playbackButtonWidth = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPlaybackButtonWidth,
                context.getDimension(R.dimen.ui_audio_record_player_playback_button_width),
            )
            val playbackButtonHeight = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPlaybackButtonHeight,
                context.getDimension(R.dimen.ui_audio_record_player_playback_button_height),
            )
            val playbackButtonElevation = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPlaybackButtonElevation,
                context.getDimension(R.dimen.ui_audio_record_player_playback_button_elevation),
            )
            val playbackButtonPadding = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPlaybackButtonPadding,
                context.getDimension(R.dimen.ui_audio_record_player_playback_button_padding),
            )
            val playbackButtonBackground: Drawable = attributes.getDrawable(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPlaybackButtonBackground,
            ) ?: context.getDrawableCompat(R.drawable.white_shape_circular)!!
            val playbackButtonBackgroundTint = attributes.getColorOrNull(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPlaybackButtonBackgroundTint,
            )

            val playIconDrawable = attributes.getDrawable(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPlayIconDrawable,
            ) ?: context.getDrawableCompat(R.drawable.ic_play)!!
            val playIconDrawableTint: Int? = attributes.getColorOrNull(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPlayIconDrawableTint,
            )

            val pauseIconDrawable = attributes.getDrawable(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPauseIconDrawable,
            ) ?: context.getDrawableCompat(R.drawable.ic_pause)!!
            val pauseIconDrawableTint: Int? = attributes.getColorOrNull(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerPauseIconDrawableTint,
            )

            val progressBarDrawable = attributes.getDrawable(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerProgressBarDrawable,
            ) ?: context.getDrawableCompat(R.drawable.rotating_indeterminate_progress_gradient)!!
            val progressBarDrawableTint: Int? = attributes.getColorOrNull(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerProgressBarDrawableTint,
            )
            val progressBarWidth = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerProgressBarWidth,
                context.getDimension(R.dimen.ui_audio_record_player_progress_bar_width),
            )
            val progressBarHeight = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerProgressBarHeight,
                context.getDimension(R.dimen.ui_audio_record_player_progress_bar_height),
            )

            val durationTextViewWidth = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerDurationTextViewWidth,
                context.getDimension(R.dimen.ui_audio_record_player_duration_text_view_width),
            )
            val durationTextViewHeight = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerDurationTextViewHeight,
                context.getDimension(R.dimen.ui_audio_record_player_duration_text_view_height),
            )
            val durationTextViewMarginStart = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerDurationTextViewMarginStart,
                context.getDimension(R.dimen.ui_audio_record_player_duration_text_view_margin_start),
            )
            val durationTextStyle = TextStyle.Builder(attributes)
                .size(
                    R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerDurationTextSize,
                    context.getDimension(R.dimen.ui_audio_record_player_duration_text_size),
                )
                .color(
                    R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerDurationTextColor,
                    context.getColorCompat(R.color.ui_audio_record_player_duration_text_color),
                )
                .font(
                    R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerDurationTextFontAssets,
                    R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerDurationTextFont,
                )
                .style(
                    R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerDurationTextStyle,
                    Typeface.NORMAL,
                )
                .build()

            val waveBarHeight = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerWaveBarHeight,
                context.getDimension(R.dimen.ui_audio_record_player_wave_bar_height),
            )
            val waveBarMarginStart = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerWaveBarMarginStart,
                context.getDimension(R.dimen.ui_audio_record_player_wave_bar_margin_start),
            )
            val waveBarColorPlayed = context.getColorCompat(R.color.ui_accent_blue)
            val waveBarColorFuture = context.getColorCompat(R.color.ui_grey)

            val scrubberWidthDefault = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerScrubberWidthDefault,
                context.getDimension(R.dimen.ui_audio_record_player_scrubber_width_default),
            )
            val scrubberWidthPressed = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerScrubberWidthPressed,
                context.getDimension(R.dimen.ui_audio_record_player_scrubber_width_pressed),
            )
            val scrubberDrawable = attributes.getDrawable(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerScrubberDrawable,
            ) ?: context.getDrawableCompat(R.drawable.share_rectangle)!!
            val scrubberDrawableTint: Int? = attributes.getColorOrNull(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerScrubberDrawableTint,
            )

            val fileIconContainerWidth = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerFileIconContainerWidth,
                context.getDimension(R.dimen.ui_audio_record_player_file_icon_container_width),
            )
            val fileIconContainerVisible = attributes.getBoolean(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerFileIconContainerVisible,
                context.resources.getBoolean(R.bool.ermis_ui_audio_record_player_file_icon_container_visible),
            )

            val audioFileIconDrawable = attributes.getDrawable(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerAudioFileIconDrawable,
            ) ?: context.getDrawableCompat(R.drawable.ic_file_aac)!!

            val speedButtonWidth = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerSpeedButtonWidth,
                context.getDimension(R.dimen.ui_audio_record_player_speed_button_width),
            )
            val speedButtonHeight = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerSpeedButtonHeight,
                context.getDimension(R.dimen.ui_audio_record_player_speed_button_height),
            )
            val speedButtonElevation = attributes.getDimensionPixelSize(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerSpeedButtonElevation,
                context.getDimension(R.dimen.ui_audio_record_player_speed_button_elevation),
            )
            val speedButtonBackground = attributes.getDrawable(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerSpeedButtonBackgroundDrawable,
            ) ?: context.getDrawableCompat(R.drawable.literal_white_shape_16dp_corners)!!
            val speedButtonBackgroundTint: Int? = attributes.getColorOrNull(
                R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerSpeedButtonBackgroundDrawableTint,
            )
            val speedButtonTextStyle = TextStyle.Builder(attributes)
                .size(
                    R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerSpeedButtonTextSize,
                    context.getDimension(R.dimen.ui_audio_record_player_speed_text_size),
                )
                .color(
                    R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerSpeedButtonTextColor,
                    context.getColorCompat(R.color.ui_audio_record_player_speed_text_color),
                )
                .font(
                    R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerSpeedButtonTextFontAssets,
                    R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerSpeedButtonTextFont,
                )
                .style(
                    R.styleable.AudioRecordPlayerView_ermisUiAudioRecordPlayerSpeedButtonTextStyle,
                    Typeface.NORMAL,
                )
                .build()

            return AudioRecordPlayerViewStyle(
                height = height,
                padding = ViewPadding(
                    start = paddingStart,
                    top = paddingTop,
                    end = paddingEnd,
                    bottom = paddingBottom,
                ),
                backgroundDrawable = backgroundDrawable,
                backgroundDrawableTint = backgroundDrawableTint,
                // Playback Progress Container
                playbackProgressContainerSize = ViewSize(
                    width = playbackProgressContainerWidth,
                    height = playbackProgressContainerHeight,
                ),
                // Playback Button
                playbackButtonSize = ViewSize(
                    width = playbackButtonWidth,
                    height = playbackButtonHeight,
                ),
                playbackButtonPadding = ViewPadding(
                    start = playbackButtonPadding,
                    top = playbackButtonPadding,
                    end = playbackButtonPadding,
                    bottom = playbackButtonPadding,
                ),
                playbackButtonElevation = playbackButtonElevation,
                playbackButtonBackground = playbackButtonBackground,
                playbackButtonBackgroundTint = playbackButtonBackgroundTint,
                playIconDrawable = playIconDrawable,
                playIconDrawableTint = playIconDrawableTint,
                pauseIconDrawable = pauseIconDrawable,
                pauseIconDrawableTint = pauseIconDrawableTint,
                // Progress Bar
                progressBarDrawable = progressBarDrawable,
                progressBarDrawableTint = progressBarDrawableTint,
                progressBarSize = ViewSize(
                    width = progressBarWidth,
                    height = progressBarHeight,
                ),
                // Duration Text
                durationTextViewSize = ViewSize(
                    width = durationTextViewWidth,
                    height = durationTextViewHeight,
                ),
                durationTextMarginStart = durationTextViewMarginStart,
                durationTextStyle = durationTextStyle,
                // Wave Bar
                waveBarHeight = waveBarHeight,
                waveBarMarginStart = waveBarMarginStart,
                waveBarColorPlayed = waveBarColorPlayed,
                waveBarColorFuture = waveBarColorFuture,
                // Scrubber
                scrubberDrawable = scrubberDrawable,
                scrubberDrawableTint = scrubberDrawableTint,
                scrubberWidthDefault = scrubberWidthDefault,
                scrubberWidthPressed = scrubberWidthPressed,
                // File Icon Container
                fileIconContainerWidth = fileIconContainerWidth,
                isFileIconContainerVisible = fileIconContainerVisible,
                audioFileIconDrawable = audioFileIconDrawable,
                // Speed Button
                speedButtonSize = ViewSize(
                    width = speedButtonWidth,
                    height = speedButtonHeight,
                ),
                speedButtonElevation = speedButtonElevation,
                speedButtonBackground = speedButtonBackground,
                speedButtonBackgroundTint = speedButtonBackgroundTint,
                speedButtonTextStyle = speedButtonTextStyle,
            )
        }

        public fun default(context: Context): AudioRecordPlayerViewStyle {
            val height = context.getDimension(R.dimen.ui_audio_record_player_height)

            val paddingStart = context.getDimension(R.dimen.ui_audio_record_player_padding_start)
            val paddingTop = context.getDimension(R.dimen.ui_audio_record_player_padding_top)
            val paddingEnd = context.getDimension(R.dimen.ui_audio_record_player_padding_end)
            val paddingBottom = context.getDimension(R.dimen.ui_audio_record_player_padding_bottom)

            val backgroundDrawable = ShapeAppearanceModelFactory.audioBackground(context)
            val backgroundDrawableTint: Int? = null

            val playbackProgressContainerWidth = context.getDimension(R.dimen.ui_audio_record_player_playback_progress_container_width)
            val playbackProgressContainerHeight = context.getDimension(R.dimen.ui_audio_record_player_playback_progress_container_height)

            val playbackButtonWidth = context.getDimension(R.dimen.ui_audio_record_player_playback_button_width)
            val playbackButtonHeight = context.getDimension(R.dimen.ui_audio_record_player_playback_button_height)
            val playbackButtonElevation = context.getDimension(R.dimen.ui_audio_record_player_playback_button_elevation)
            val playbackButtonPadding = context.getDimension(R.dimen.ui_audio_record_player_playback_button_padding)
            val playbackButtonBackground = context.getDrawableCompat(R.drawable.white_shape_circular)
            val playbackButtonBackgroundTint: Int? = null

            val playIconDrawable = context.getDrawableCompat(R.drawable.ic_play)
            val playIconDrawableTint: Int? = null

            val pauseIconDrawable = context.getDrawableCompat(R.drawable.ic_pause)
            val pauseIconDrawableTint: Int? = null

            val progressBarDrawable = context.getDrawableCompat(R.drawable.rotating_indeterminate_progress_gradient)
            val progressBarDrawableTint: Int? = null
            val progressBarWidth = context.getDimension(R.dimen.ui_audio_record_player_progress_bar_width)
            val progressBarHeight = context.getDimension(R.dimen.ui_audio_record_player_progress_bar_height)

            val durationTextViewWidth = context.getDimension(R.dimen.ui_audio_record_player_duration_text_view_width)
            val durationTextViewHeight = context.getDimension(R.dimen.ui_audio_record_player_duration_text_view_height)
            val durationTextViewMarginStart = context.getDimension(R.dimen.ui_audio_record_player_duration_text_view_margin_start)
            val durationTextStyle = TextStyle(
                size = context.getDimension(R.dimen.ui_audio_record_player_duration_text_size),
                color = context.getColorCompat(R.color.ui_audio_record_player_duration_text_color),
            )

            val waveBarHeight = context.getDimension(R.dimen.ui_audio_record_player_wave_bar_height)
            val waveBarMarginStart = context.getDimension(R.dimen.ui_audio_record_player_wave_bar_margin_start)
            val waveBarColorPlayed = context.getColorCompat(R.color.ui_accent_blue)
            val waveBarColorFuture = context.getColorCompat(R.color.ui_grey)

            val scrubberWidthDefault = context.getDimension(R.dimen.ui_audio_record_player_scrubber_width_default)
            val scrubberWidthPressed = context.getDimension(R.dimen.ui_audio_record_player_scrubber_width_pressed)
            val scrubberDrawable = context.getDrawableCompat(R.drawable.share_rectangle)
            val scrubberDrawableTint: Int? = null

            val fileIconContainerWidth = context.getDimension(R.dimen.ui_audio_record_player_file_icon_container_width)
            val fileIconContainerVisible = context.resources.getBoolean(R.bool.ermis_ui_audio_record_player_file_icon_container_visible)

            val audioFileIconDrawable = context.getDrawableCompat(R.drawable.ic_file_aac)

            val speedButtonWidth = context.getDimension(R.dimen.ui_audio_record_player_speed_button_width)
            val speedButtonHeight = context.getDimension(R.dimen.ui_audio_record_player_speed_button_height)
            val speedButtonElevation = context.getDimension(R.dimen.ui_audio_record_player_speed_button_elevation)
            val speedButtonBackground = context.getDrawableCompat(R.drawable.literal_white_shape_16dp_corners)
            val speedButtonBackgroundTint: Int? = null
            val speedButtonTextStyle = TextStyle(
                size = context.getDimension(R.dimen.ui_audio_record_player_speed_text_size),
                color = context.getColorCompat(R.color.ui_audio_record_player_speed_text_color),
            )

            return AudioRecordPlayerViewStyle(
                height = height,
                padding = ViewPadding(
                    start = paddingStart,
                    top = paddingTop,
                    end = paddingEnd,
                    bottom = paddingBottom,
                ),
                backgroundDrawable = backgroundDrawable,
                backgroundDrawableTint = backgroundDrawableTint,
                // Playback Progress Container
                playbackProgressContainerSize = ViewSize(
                    width = playbackProgressContainerWidth,
                    height = playbackProgressContainerHeight,
                ),
                // Playback Button
                playbackButtonSize = ViewSize(
                    width = playbackButtonWidth,
                    height = playbackButtonHeight,
                ),
                playbackButtonPadding = ViewPadding(
                    start = playbackButtonPadding,
                    top = playbackButtonPadding,
                    end = playbackButtonPadding,
                    bottom = playbackButtonPadding,
                ),
                playbackButtonElevation = playbackButtonElevation,
                playbackButtonBackground = playbackButtonBackground,
                playbackButtonBackgroundTint = playbackButtonBackgroundTint,
                playIconDrawable = playIconDrawable,
                playIconDrawableTint = playIconDrawableTint,
                pauseIconDrawable = pauseIconDrawable,
                pauseIconDrawableTint = pauseIconDrawableTint,
                // Progress Bar
                progressBarDrawable = progressBarDrawable,
                progressBarDrawableTint = progressBarDrawableTint,
                progressBarSize = ViewSize(
                    width = progressBarWidth,
                    height = progressBarHeight,
                ),
                // Duration Text
                durationTextViewSize = ViewSize(
                    width = durationTextViewWidth,
                    height = durationTextViewHeight,
                ),
                durationTextMarginStart = durationTextViewMarginStart,
                durationTextStyle = durationTextStyle,
                // Wave Bar
                waveBarHeight = waveBarHeight,
                waveBarMarginStart = waveBarMarginStart,
                waveBarColorPlayed = waveBarColorPlayed,
                waveBarColorFuture = waveBarColorFuture,
                // Scrubber
                scrubberDrawable = scrubberDrawable,
                scrubberDrawableTint = scrubberDrawableTint,
                scrubberWidthDefault = scrubberWidthDefault,
                scrubberWidthPressed = scrubberWidthPressed,
                // File Icon Container
                fileIconContainerWidth = fileIconContainerWidth,
                isFileIconContainerVisible = fileIconContainerVisible,
                audioFileIconDrawable = audioFileIconDrawable,
                // Speed Button
                speedButtonSize = ViewSize(
                    width = speedButtonWidth,
                    height = speedButtonHeight,
                ),
                speedButtonElevation = speedButtonElevation,
                speedButtonBackground = speedButtonBackground,
                speedButtonBackgroundTint = speedButtonBackgroundTint,
                speedButtonTextStyle = speedButtonTextStyle,
            )
        }
    }
}
