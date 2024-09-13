
package network.ermis.ui.view.channels.header

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import network.ermis.core.models.User
import network.ermis.ui.components.R
import network.ermis.ui.components.databinding.ChannelListHeaderViewBinding
import network.ermis.ui.font.TextStyle
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.streamThemeInflater

/**
 * A component that shows the title of the channels list, the current connection status,
 * the avatar of the current user, and provides an action button which can be used to create a new conversation.
 * It is designed to be displayed at the top of the channels screen of your app.
 */
public class ChannelListHeaderView : ConstraintLayout {

    public constructor(context: Context) : super(context.createStreamThemeWrapper()) {
        init(null)
    }

    public constructor(context: Context, attrs: AttributeSet?) : super(context.createStreamThemeWrapper(), attrs) {
        init(attrs)
    }

    public constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr,
    ) {
        init(attrs)
    }

    public constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr,
        defStyleRes,
    ) {
        init(attrs)
    }

    private val binding = ChannelListHeaderViewBinding.inflate(streamThemeInflater, this, true)

    private fun init(attrs: AttributeSet?) {
        context.obtainStyledAttributes(
            attrs,
            R.styleable.ChannelListHeaderView,
            R.attr.ermisUiChannelListHeaderStyle,
            R.style.ermisUi_ChannelListHeader,
        ).use { typedArray ->
            configUserAvatar(typedArray)
            configOnlineTitle(typedArray)
            configOfflineTitleContainer(typedArray)
            configActionButton(typedArray)
            configureSeparator(typedArray)
        }
    }

    private fun configUserAvatar(typedArray: TypedArray) {
        val showAvatar = typedArray.getBoolean(R.styleable.ChannelListHeaderView_ermisUiShowUserAvatar, true)
        binding.userAvatarView.apply {
            isInvisible = !showAvatar
            isClickable = showAvatar
        }
    }

    private fun configOnlineTitle(typedArray: TypedArray) {
        binding.onlineTextView.setTextStyle(getOnlineTitleTextStyle(typedArray))
    }

    private fun configOfflineTitleContainer(typedArray: TypedArray) {
        binding.offlineTextView.setTextStyle(getOfflineTitleTextStyle(typedArray))

        binding.offlineProgressBar.apply {
            isVisible =
                typedArray.getBoolean(R.styleable.ChannelListHeaderView_ermisUiShowOfflineProgressBar, true)
            indeterminateTintList = getProgressBarTint(typedArray)
        }
    }

    private fun getProgressBarTint(typedArray: TypedArray) =
        typedArray.getColorStateList(R.styleable.ChannelListHeaderView_ermisUiOfflineProgressBarTint)
            ?: ContextCompat.getColorStateList(context, R.color.ui_accent_blue)

    private fun configActionButton(typedArray: TypedArray) {
        binding.actionButton.apply {
            val showActionButton =
                typedArray.getBoolean(R.styleable.ChannelListHeaderView_ermisUiShowActionButton, true)

            isInvisible = !showActionButton
            isClickable = showActionButton

            val drawable = typedArray.getDrawable(R.styleable.ChannelListHeaderView_ermisUiActionButtonIcon)
                ?: ContextCompat.getDrawable(context, R.drawable.ic_pen)
            setImageDrawable(drawable)
            backgroundTintList =
                typedArray.getColorStateList(R.styleable.ChannelListHeaderView_ermisUiActionBackgroundTint)
                    ?: ContextCompat.getColorStateList(context, R.color.ermis_ui_icon_button_background_selector)
        }
    }

    /**
     * Uses the [typedArray] to customize the separator View's background drawable.
     *
     * @param typedArray The attribute array the user passed to the XML component.
     */
    private fun configureSeparator(typedArray: TypedArray) {
        binding.separator.apply {
            val drawable =
                typedArray.getDrawable(R.styleable.ChannelListHeaderView_ermisUiChannelListSeparatorBackgroundDrawable)
            visibility = if (drawable != null) VISIBLE else GONE
            background = drawable
        }
    }

    private fun getOnlineTitleTextStyle(typedArray: TypedArray): TextStyle {
        return TextStyle.Builder(typedArray)
            .size(
                R.styleable.ChannelListHeaderView_ermisUiOnlineTitleTextSize,
                context.getDimension(R.dimen.ui_text_large),
            )
            .color(
                R.styleable.ChannelListHeaderView_ermisUiOnlineTitleTextColor,
                context.getColorCompat(R.color.ui_text_color_primary),
            )
            .font(
                R.styleable.ChannelListHeaderView_ermisUiOnlineTitleFontAssets,
                R.styleable.ChannelListHeaderView_ermisUiOnlineTitleTextFont,
            )
            .style(
                R.styleable.ChannelListHeaderView_ermisUiOnlineTitleTextStyle,
                Typeface.BOLD,
            ).build()
    }

    private fun getOfflineTitleTextStyle(typedArray: TypedArray): TextStyle {
        return TextStyle.Builder(typedArray)
            .size(
                R.styleable.ChannelListHeaderView_ermisUiOfflineTitleTextSize,
                context.getDimension(R.dimen.ui_text_large),
            )
            .color(
                R.styleable.ChannelListHeaderView_ermisUiOfflineTitleTextColor,
                context.getColorCompat(R.color.ui_text_color_primary),
            )
            .font(
                R.styleable.ChannelListHeaderView_ermisUiOfflineTitleFontAssets,
                R.styleable.ChannelListHeaderView_ermisUiOfflineTitleTextFont,
            )
            .style(
                R.styleable.ChannelListHeaderView_ermisUiOfflineTitleTextStyle,
                Typeface.BOLD,
            ).build()
    }

    /**
     * Sets [User] to bind user information with the avatar in the header.
     *
     * @param user A user that will represent the avatar in the header.
     */
    public fun setUser(user: User) {
        binding.userAvatarView.setUser(user)
    }

    /**
     * Sets the title that is shown on the header when the client's network state is online.
     *
     * @param title A title that indicates the online network state.
     */
    public fun setOnlineTitle(title: String) {
        binding.onlineTextView.text = title
    }

    public fun setShowAvatar(show: Boolean) {
        binding.userAvatarView.isVisible = show
    }

    /**
     * Shows the title that indicates the network state is online.
     */
    public fun showOnlineTitle() {
        binding.offlineTitleContainer.isVisible = false
        binding.onlineTextView.isVisible = true
    }

    /**
     * Sets a click listener for the title in the header.
     */
    public fun setOnTitleClickListener(listener: () -> Unit) {
        binding.offlineTextView.setOnClickListener { listener() }
        binding.onlineTextView.setOnClickListener { listener() }
    }

    /**
     * Sets a long click listener for the title in the header.
     */
    public fun setOnTitleLongClickListener(listener: () -> Unit) {
        binding.offlineTextView.setOnLongClickListener {
            listener()
            true
        }
        binding.onlineTextView.setOnLongClickListener {
            listener()
            true
        }
    }

    /**
     * Shows the title that indicates the network state is offline.
     */
    public fun showOfflineTitle() {
        binding.offlineTitleContainer.isVisible = true
        binding.offlineProgressBar.isVisible = false
        binding.onlineTextView.isVisible = false

        binding.offlineTextView.text = resources.getString(R.string.ermis_ui_channel_list_header_offline)
    }

    /**
     * Shows the title that indicates the network state is connecting.
     */
    public fun showConnectingTitle() {
        binding.offlineTitleContainer.isVisible = true
        binding.offlineProgressBar.isVisible = true
        binding.onlineTextView.isVisible = false

        binding.offlineTextView.text = resources.getString(R.string.ermis_ui_channel_list_header_disconnected)
    }

    /**
     * Sets a click listener for the left button in the header represented by the avatar of
     * the current user.
     */
    public fun setOnUserAvatarClickListener(listener: UserAvatarClickListener) {
        binding.userAvatarView.setOnClickListener { listener.onUserAvatarClick() }
    }

    /**
     * Sets a click listener for the right button in the header.
     */
    public fun setOnActionButtonClickListener(listener: ActionButtonClickListener) {
        binding.actionButton.setOnClickListener { listener.onClick() }
    }

    public fun setOnUserAvatarLongClickListener(listener: () -> Unit) {
        binding.userAvatarView.setOnLongClickListener {
            listener()
            true
        }
    }

    /**
     * Click listener for the left button in the header represented by the avatar of
     * the current user.
     */
    public fun interface UserAvatarClickListener {
        public fun onUserAvatarClick()
    }

    /**
     * Click listener for the right button in the header.
     */
    public fun interface ActionButtonClickListener {
        public fun onClick()
    }
}
