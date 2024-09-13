
package network.ermis.ui.view.gallery.internal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import network.ermis.core.models.Attachment
import network.ermis.ui.components.databinding.ItemAttachmentGalleryImageBinding
import network.ermis.ui.utils.load

internal class AttachmentGalleryImagePageFragment : Fragment() {

    private var _binding: ItemAttachmentGalleryImageBinding? = null
    private val binding get() = _binding!!

    private val imageUrl: String? by lazy {
        requireArguments().getString(ARG_IMAGE_URL)
    }

    private var imageClickListener: () -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ItemAttachmentGalleryImageBinding.inflate(inflater)
            .apply { _binding = this }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.photoView) {
            load(
                data = imageUrl,
                onStart = {
                    binding.placeHolderImageView.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                },
                onComplete = {
                    binding.placeHolderImageView.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                },
            )

            setOnClickListener {
                imageClickListener()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IMAGE_URL = "image_url"

        fun create(attachment: Attachment, imageClickListener: () -> Unit = {}): Fragment {
            return AttachmentGalleryImagePageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMAGE_URL, attachment.imageUrl)
                }
                this.imageClickListener = imageClickListener
            }
        }
    }
}
