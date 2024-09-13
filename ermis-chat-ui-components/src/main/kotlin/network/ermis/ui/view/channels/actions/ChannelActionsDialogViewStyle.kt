
package network.ermis.ui.view.channels.actions

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import network.ermis.ui.components.R
import network.ermis.ui.font.TextStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.getDrawableCompat
import network.ermis.ui.utils.extensions.use

/**
 * Style for [ChannelActionsDialogFragment].
 * Use this class together with [TransformStyle.channelActionsDialogStyleTransformer] to change [ChannelActionsDialogFragment] styles programmatically.
 *
 * @property memberNamesTextStyle Text appearance for dialog title with member names.
 * @property memberInfoTextStyle Text appearance for dialog subtitle with member info.
 * @property itemTextStyle Text appearance for action item.
 * @property itemTextStyle Text appearance for warning action item.
 * @property viewInfoIcon Icon for view info action. Default value is [R.drawable.ic_single_user].
 * @property viewInfoEnabled Shows/hides view info action. Hidden by default.
 * @property leaveGroupIcon Icon for leave group action. Default value is [R.drawable.ic_leave_group].
 * @property leaveGroupEnabled Shows/hides leave group action. Shown by default.
 * @property deleteConversationIcon Icon for delete conversation action. Default value is [R.drawable.ic_delete].
 * @property deleteConversationEnabled Shows/hides delete conversation action. Shown by default.
 * @property cancelIcon Icon for dismiss dialog action. Default value is [R.drawable.ic_clear].
 * @property cancelEnabled Shows/hides dismiss dialog action. Shown by default.
 * @property background Dialog's background.
 */
public data class ChannelActionsDialogViewStyle(
    public val memberNamesTextStyle: TextStyle,
    public val memberInfoTextStyle: TextStyle,
    public val itemTextStyle: TextStyle,
    public val warningItemTextStyle: TextStyle,
    public val viewInfoIcon: Drawable,
    public val viewInfoEnabled: Boolean,
    public val markAsReadIcon: Drawable,
    public val markAsReadEnabled: Boolean,
    public val leaveGroupIcon: Drawable,
    public val leaveGroupEnabled: Boolean,
    public val deleteConversationIcon: Drawable,
    public val deleteConversationEnabled: Boolean,
    public val cancelIcon: Drawable,
    public val cancelEnabled: Boolean,
    public val background: Drawable,
) : ViewStyle {
    internal companion object {
        operator fun invoke(context: Context, attrs: AttributeSet?): ChannelActionsDialogViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.ChannelListView,
                0,
                0,
            ).use {
                val a = context.obtainStyledAttributes(
                    it.getResourceId(
                        R.styleable.ChannelListView_ermisUiChannelActionsDialogStyle,
                        R.style.ermisUi_ChannelList_ActionsDialog,
                    ),
                    R.styleable.ChannelActionsDialog,
                )

                val memberNamesTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsMemberNamesTextSize,
                        context.getDimension(R.dimen.ui_text_large),
                    )
                    .color(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsMemberNamesTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsMemberNamesTextFontAssets,
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsMemberNamesTextFont,
                    )
                    .style(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsMemberNamesTextStyle,
                        Typeface.BOLD,
                    )
                    .build()

                val memberInfoTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsMemberInfoTextSize,
                        context.getDimension(R.dimen.ui_text_small),
                    )
                    .color(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsMemberInfoTextColor,
                        context.getColorCompat(R.color.ui_text_color_secondary),
                    )
                    .font(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsMemberInfoTextFontAssets,
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsMemberInfoTextFont,
                    )
                    .style(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsMemberInfoTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val itemTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsItemTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsItemTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsItemTextFontAssets,
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsItemTextFont,
                    )
                    .style(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsItemTextStyle,
                        Typeface.BOLD,
                    )
                    .build()

                val warningItemTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsWarningItemTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsWarningItemTextColor,
                        context.getColorCompat(R.color.ui_accent_red),
                    )
                    .font(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsWarningItemTextFontAssets,
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsWarningItemTextFont,
                    )
                    .style(
                        R.styleable.ChannelActionsDialog_ermisUiChannelActionsWarningItemTextStyle,
                        Typeface.BOLD,
                    )
                    .build()

                val viewInfoIcon = a.getDrawable(R.styleable.ChannelActionsDialog_ermisUiChannelActionsViewInfoIcon)
                    ?: context.getDrawableCompat(R.drawable.ic_single_user)!!

                val viewInfoEnabled = a.getBoolean(
                    R.styleable.ChannelActionsDialog_ermisUiChannelActionsViewInfoEnabled,
                    false,
                )

                val markAsreadIcon = a.getDrawable(R.styleable.ChannelActionsDialog_ermisUiChannelActionsMarkAsReadIcon)
                    ?: context.getDrawableCompat(R.drawable.ic_mark_as_read)!!

                val markAsreadEnabled = a.getBoolean(
                    R.styleable.ChannelActionsDialog_ermisUiChannelActionsMarkAsReadEnabled,
                    false,
                )

                val leaveGroupIcon =
                    a.getDrawable(R.styleable.ChannelActionsDialog_ermisUiChannelActionsLeaveGroupIcon)
                        ?: context.getDrawableCompat(R.drawable.ic_leave_group)!!

                val leaveGroupEnabled = a.getBoolean(
                    R.styleable.ChannelActionsDialog_ermisUiChannelActionsLeaveGroupEnabled,
                    true,
                )

                val deleteConversationIcon =
                    a.getDrawable(R.styleable.ChannelActionsDialog_ermisUiChannelActionsDeleteConversationIcon)
                        ?: context.getDrawableCompat(R.drawable.ic_delete)!!

                val deleteConversationEnabled = a.getBoolean(
                    R.styleable.ChannelActionsDialog_ermisUiChannelActionsDeleteConversationEnabled,
                    true,
                )

                val cancelIcon = a.getDrawable(R.styleable.ChannelActionsDialog_ermisUiChannelActionsCancelIcon)
                    ?: context.getDrawableCompat(R.drawable.ic_clear)!!

                val cancelEnabled = a.getBoolean(
                    R.styleable.ChannelActionsDialog_ermisUiChannelActionsCancelEnabled,
                    true,
                )

                val background = a.getDrawable(R.styleable.ChannelActionsDialog_ermisUiChannelActionsBackground)
                    ?: context.getDrawableCompat(R.drawable.round_bottom_sheet)!!

                return ChannelActionsDialogViewStyle(
                    memberNamesTextStyle = memberNamesTextStyle,
                    memberInfoTextStyle = memberInfoTextStyle,
                    itemTextStyle = itemTextStyle,
                    warningItemTextStyle = warningItemTextStyle,
                    viewInfoIcon = viewInfoIcon,
                    viewInfoEnabled = viewInfoEnabled,
                    markAsReadIcon = markAsreadIcon,
                    markAsReadEnabled = markAsreadEnabled,
                    leaveGroupIcon = leaveGroupIcon,
                    leaveGroupEnabled = leaveGroupEnabled,
                    deleteConversationIcon = deleteConversationIcon,
                    deleteConversationEnabled = deleteConversationEnabled,
                    cancelIcon = cancelIcon,
                    cancelEnabled = cancelEnabled,
                    background = background,
                ).let(TransformStyle.channelActionsDialogStyleTransformer::transform)
            }
        }
    }
}
