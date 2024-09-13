
package network.ermis.ui.view.messages.composer.attachment.preview

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import network.ermis.core.models.Attachment
import network.ermis.ui.view.messages.composer.MessageComposerViewStyle
import network.ermis.ui.view.messages.composer.attachment.preview.factory.AttachmentPreviewFactory
import network.ermis.ui.view.messages.composer.attachment.preview.factory.AudioRecordAttachmentPreviewFactory
import network.ermis.ui.view.messages.composer.attachment.preview.factory.FallbackAttachmentPreviewFactory
import network.ermis.ui.view.messages.composer.attachment.preview.factory.FileAttachmentPreviewFactory
import network.ermis.ui.view.messages.composer.attachment.preview.factory.MediaAttachmentPreviewFactory
import io.getstream.log.taggedLogger

/**
 * A manager for registered attachment preview factories.
 *
 * @param attachmentPreviewFactories The list of ViewHolder factories for attachment preview items.
 * @param fallbackAttachmentPreviewFactory The fallback factory that will be used in case there are
 * no other factories that can handle the attachment.
 */
public class AttachmentPreviewFactoryManager @JvmOverloads constructor(
    attachmentPreviewFactories: List<AttachmentPreviewFactory> = listOf(
        MediaAttachmentPreviewFactory(),
        AudioRecordAttachmentPreviewFactory(),
        FileAttachmentPreviewFactory(),
    ),
    private val fallbackAttachmentPreviewFactory: FallbackAttachmentPreviewFactory = FallbackAttachmentPreviewFactory(),
) {

    private val logger by taggedLogger("AttachPreviewFM")

    private val viewTypeToFactoryMapping = SparseArrayCompat<AttachmentPreviewFactory>()

    init {
        for (i in attachmentPreviewFactories.indices) {
            viewTypeToFactoryMapping.put(i, attachmentPreviewFactories[i])
        }
    }

    /**
     * Creates and instantiates a new instance of [AttachmentPreviewViewHolder].
     *
     * @param parentView The parent container.
     * @param attachmentRemovalListener Click listener for the remove attachment button.
     * @param style Used to style the various factories. If null, the respective factory will retain
     * the default appearance.
     *
     * @return An instance of attachment preview ViewHolder.
     */
    public fun onCreateViewHolder(
        parentView: ViewGroup,
        viewType: Int,
        attachmentRemovalListener: (Attachment) -> Unit,
        style: MessageComposerViewStyle? = null,
    ): AttachmentPreviewViewHolder {
        return viewTypeToFactoryMapping.get(viewType, fallbackAttachmentPreviewFactory)
            .onCreateViewHolder(
                parentView = parentView,
                attachmentRemovalListener = attachmentRemovalListener,
                style = style,
            )
    }

    /**
     * Finds the first factory that is capable of displaying the given attachment
     * and return a view type associated with the factory.
     *
     * @param attachment The attachment to display.
     */
    public fun getItemViewType(attachment: Attachment): Int {
        for (i in 0 until viewTypeToFactoryMapping.size()) {
            val factory = viewTypeToFactoryMapping.valueAt(i)
            logger.w { "[getItemViewType] i: $i, factory: $factory" }
            if (factory.canHandle(attachment)) {
                return viewTypeToFactoryMapping.keyAt(i)
            }
        }
        return FALLBACK_FACTORY_VIEW_TYPE
    }

    private companion object {
        /**
         * An arbitrary view type value for the fallback factory.
         */
        private const val FALLBACK_FACTORY_VIEW_TYPE = 100
    }
}
