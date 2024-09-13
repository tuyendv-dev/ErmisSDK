package network.ermis.sample.feature.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import network.ermis.chat.ui.sample.R
import network.ermis.chat.ui.sample.databinding.SwitchPlatformsErmisDialogBinding

class SwitchPlatformDialogFragment : BottomSheetDialogFragment() {

    var sdkClickListener: (() -> Unit)? = null
    var ermisClickListener: (() -> Unit)? = null

    private val selectedIndex: Int by lazy { requireArguments().getInt(ARG_SELECTED_INDEX) }
    private var _binding: SwitchPlatformsErmisDialogBinding? = null
    private val binding get() = _binding!!

    override fun onDetach() {
        super.onDetach()
        sdkClickListener = null
        ermisClickListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SwitchPlatformsErmisDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int = R.style.ermisUiBottomSheetDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            when (selectedIndex) {
                0 -> tvSdks.setBackgroundResource(R.drawable.bg_input_text_login)
                1 -> tvErmis.setBackgroundResource(R.drawable.bg_input_text_login)
            }
            tvSdks.setOnClickListener {
                sdkClickListener?.invoke()
                dismiss()
            }
            tvErmis.setOnClickListener {
                ermisClickListener?.invoke()
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SwitchPlatformDialogFragment"
        private const val ARG_SELECTED_INDEX = "ARG_SELECTED_INDEX"

        fun newInstance(
            selectedIndex: Int = 0,
        ): SwitchPlatformDialogFragment = SwitchPlatformDialogFragment().apply {
            arguments = bundleOf(
                ARG_SELECTED_INDEX to selectedIndex,
            )
        }
    }
}
