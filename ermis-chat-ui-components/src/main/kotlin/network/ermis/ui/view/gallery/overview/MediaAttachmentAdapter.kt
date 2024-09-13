
package network.ermis.ui.view.gallery.overview

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import network.ermis.client.utils.attachment.isImage
import network.ermis.client.utils.attachment.isVideo
import network.ermis.core.models.AttachmentType
import network.ermis.ui.ChatUI
import network.ermis.ui.common.images.resizing.applyStreamCdnImageResizingIfEnabled
import network.ermis.ui.common.utils.extensions.imagePreviewUrl
import network.ermis.ui.components.databinding.ItemMediaAttachmentBinding
import network.ermis.ui.view.gallery.AttachmentGalleryItem
import network.ermis.ui.view.gallery.MediaAttachmentGridViewStyle
import network.ermis.ui.view.gallery.options.AttachmentGalleryOptionsViewStyle
import network.ermis.ui.utils.extensions.streamThemeInflater
import network.ermis.ui.utils.load

internal class MediaAttachmentAdapter(
    private val style: MediaAttachmentGridViewStyle,
    private val mediaAttachmentClickListener: MediaAttachmentClickListener,
) : ListAdapter<AttachmentGalleryItem, MediaAttachmentAdapter.MediaAttachmentViewHolder>(
    AttachmentGalleryItemDiffCallback,
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaAttachmentViewHolder {
        return ItemMediaAttachmentBinding
            .inflate(parent.streamThemeInflater, parent, false)
            .let {
                MediaAttachmentViewHolder(
                    binding = it,
                    mediaAttachmentClickListener = mediaAttachmentClickListener,
                    style = style,
                )
            }
    }

    override fun onBindViewHolder(holder: MediaAttachmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * The ViewHolder used for displaying media previews.
     *
     * @param binding The binding used to build a UI.
     * @param mediaAttachmentClickListener Click listener used to detect
     * clicks on media attachment previews.
     * @param style Used to change the appearance of the UI.
     */
    class MediaAttachmentViewHolder(
        private val binding: ItemMediaAttachmentBinding,
        private val mediaAttachmentClickListener: MediaAttachmentClickListener,
        private val style: MediaAttachmentGridViewStyle,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.mediaContainer.setOnClickListener {
                mediaAttachmentClickListener.onMediaAttachmentClick(bindingAdapterPosition)
            }
        }

        fun bind(attachmentGalleryItem: AttachmentGalleryItem) {
            setupUserAvatar(attachmentGalleryItem)
            setupPlayButton(attachmentGalleryItem.attachment.type)
            loadImage(attachmentGalleryItem)
        }

        /**
         * Loads the given image.
         *
         * @param attachmentGalleryItem The attachment to be displayed.
         */
        private fun loadImage(attachmentGalleryItem: AttachmentGalleryItem) {
            val isVideoAttachment = attachmentGalleryItem.attachment.isVideo()
            val shouldLoadImage = attachmentGalleryItem.attachment.isImage() ||
                (attachmentGalleryItem.attachment.isVideo() && ChatUI.videoThumbnailsEnabled)
            binding.mediaImageView.load(
                data = if (shouldLoadImage) {
                    attachmentGalleryItem.attachment.imagePreviewUrl?.applyStreamCdnImageResizingIfEnabled(
                        streamCdnImageResizing = ChatUI.streamCdnImageResizing,
                    )
                } else {
                    null
                },
                placeholderDrawable = if (!isVideoAttachment) {
                    style.imagePlaceholder
                } else {
                    null
                },
                onStart = { binding.progressBar.visibility = View.VISIBLE },
                onComplete = {
                    binding.playButtonCardView.isVisible =
                        attachmentGalleryItem.attachment.isVideo()
                    binding.progressBar.visibility = View.GONE
                },
            )
        }

        /**
         * Toggles the visibility of user avatars and load the
         * given image.
         *
         * @param attachmentGalleryItem The attachment to be displayed.
         */
        private fun setupUserAvatar(attachmentGalleryItem: AttachmentGalleryItem) {
            val user = attachmentGalleryItem.user
            if (user == null) {
                binding.userAvatarCardView.isVisible = false
                return
            }
            if (style.showUserAvatars) {
                binding.userAvatarCardView.isVisible = true
                binding.userAvatarView.setUser(user)
            } else {
                binding.userAvatarCardView.isVisible = false
            }
        }

        /**
         * Sets up the play icon overlaid above video attachment previews
         * by pulling relevant values from [AttachmentGalleryOptionsViewStyle].
         **/
        private fun setupPlayButton(attachmentType: String?) {
            if (attachmentType == AttachmentType.VIDEO) {
                setupPlayButtonIcon()
                setupPlayButtonCard()
            }
        }

        /**
         * Sets up the play button icon hosted in an image view.
         */
        private fun setupPlayButtonIcon() {
            with(binding.playButtonImageView) {
                val playVideoDrawable = style.playVideoButtonIcon?.mutate()?.apply {
                    val tintColor = style.playVideoIconTint

                    if (tintColor != null) {
                        this.setTint(tintColor)
                    }
                }

                setImageDrawable(playVideoDrawable)
                setPaddingRelative(
                    style.playVideoIconPaddingStart,
                    style.playVideoIconPaddingTop,
                    style.playVideoIconPaddingEnd,
                    style.playVideoIconPaddingBottom,
                )
            }
        }

        /**
         * Sets up the card wrapping the image view that contains the
         * play button icon.
         */
        private fun setupPlayButtonCard() {
            with(binding.playButtonCardView) {
                elevation = style.playVideoIconElevation
                setCardBackgroundColor(style.playVideoIconBackgroundColor)
                radius = style.playVideoIconCornerRadius
            }
        }
    }

    internal fun interface MediaAttachmentClickListener {
        fun onMediaAttachmentClick(position: Int)
    }

    private object AttachmentGalleryItemDiffCallback : DiffUtil.ItemCallback<AttachmentGalleryItem>() {
        override fun areItemsTheSame(oldItem: AttachmentGalleryItem, newItem: AttachmentGalleryItem): Boolean {
            return oldItem.attachment.imagePreviewUrl == newItem.attachment.imagePreviewUrl &&
                oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(oldItem: AttachmentGalleryItem, newItem: AttachmentGalleryItem): Boolean {
            return oldItem == newItem
        }
    }
}
