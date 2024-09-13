
package network.ermis.ui.view.gallery.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import network.ermis.ui.components.R
import network.ermis.ui.components.databinding.DialogMediaAttachmentBinding
import network.ermis.ui.view.gallery.internal.AttachmentGalleryViewModel

internal class MediaAttachmentDialogFragment : BottomSheetDialogFragment() {
    private var _binding: DialogMediaAttachmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AttachmentGalleryViewModel by viewModels()
    private var mediaClickListener: (Int) -> Unit = {}

    override fun getTheme(): Int = R.style.ermisUiBottomSheetDialogTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return DialogMediaAttachmentBinding.inflate(inflater, container, false)
            .apply { _binding = this }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            closeButton.setOnClickListener {
                dismiss()
            }
            mediaAttachmentGridView.setMediaClickListener {
                mediaClickListener.invoke(it)
            }
            viewModel.attachmentGalleryItemsLiveData.observe(viewLifecycleOwner, mediaAttachmentGridView::setAttachments)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun setMediaClickListener(listener: (Int) -> Unit) {
        mediaClickListener = listener
    }

    internal companion object {
        fun newInstance(): MediaAttachmentDialogFragment {
            return MediaAttachmentDialogFragment()
        }
    }
}
