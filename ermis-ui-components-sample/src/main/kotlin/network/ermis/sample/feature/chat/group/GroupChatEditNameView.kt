package network.ermis.sample.feature.chat.group

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.widget.doOnTextChanged
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupEditNameViewBinding

class GroupChatEditNameView : FrameLayout {

    private val binding = ChatInfoGroupEditNameViewBinding.inflate(LayoutInflater.from(context), this, true)
    private var nameChangedListener: GroupNameChangedListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr,
    ) {
        init()
    }

    fun init() {
        binding.nameEditText.doOnTextChanged { _, _, _, _ ->
            updateChannelName()
        }
        binding.descriptionEditText.doOnTextChanged { _, _, _, _ ->
            updateChannelName()
        }
    }

    fun setChannelData(name: String, description: String) {
        binding.nameEditText.setText(name)
        binding.descriptionEditText.setText(description)
    }

    fun setGroupNameChangedListener(listener: GroupNameChangedListener?) {
        nameChangedListener = listener
    }

    private fun updateChannelName() {
        nameChangedListener?.onNameChanged(
            binding.nameEditText.text.trim().toString(),
            binding.descriptionEditText.text.trim().toString()
        )
    }

    fun interface GroupNameChangedListener {
        fun onNameChanged(name: String, description: String)
    }
}
