
package network.ermis.sample.feature.chat.group.member

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.res.use
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.getColorFromRes
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupMemberOptionBinding

class GroupChatInfoMemberOptionView : FrameLayout {

    private val binding = ChatInfoGroupMemberOptionBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr,
    ) {
        init(attrs)
    }

    fun setOnOptionClickListener(listener: OptionClickListener) {
        binding.root.setOnClickListener {
            listener.onClick()
        }
    }

    private fun init(attrs: AttributeSet?) {
        attrs ?: return
        context.obtainStyledAttributes(attrs, R.styleable.GroupChatInfoMemberOptionView).use { typedArray ->
            binding.apply {
                iconImageView.setImageResource(
                    typedArray.getResourceId(R.styleable.GroupChatInfoMemberOptionView_optionIcon, R.drawable.ic_cancel),
                )
                typedArray.getColorStateList(R.styleable.GroupChatInfoMemberOptionView_optionIconTint)
                    ?.let { colorStateList ->
                        iconImageView.imageTintList = colorStateList
                    }
                titleTextView.text = typedArray.getString(R.styleable.GroupChatInfoMemberOptionView_optionText)
                titleTextView.setTextColor(
                    typedArray.getColor(
                        R.styleable.GroupChatInfoMemberOptionView_optionTextColor,
                        context.getColorFromRes(R.color.ui_text_color_primary),
                    ),
                )
            }
        }
    }

    fun interface OptionClickListener {
        fun onClick()
    }
}
