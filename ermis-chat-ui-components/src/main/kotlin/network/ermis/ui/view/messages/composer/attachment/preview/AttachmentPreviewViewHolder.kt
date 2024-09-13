
package network.ermis.ui.view.messages.composer.attachment.preview

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import network.ermis.core.models.Attachment

/**
 * A base [RecyclerView.ViewHolder] for attachment preview items in the message composer.
 *
 * @param itemView The view that this ViewHolder controls.
 */
public abstract class AttachmentPreviewViewHolder(
    public val itemView: View,
) : RecyclerView.ViewHolder(itemView.rootView) {
    public val context: Context = itemView.context

    /**
     * Binds the created View to the attachment data.
     *
     * @param attachment The attachment that will be displayed.
     */
    public abstract fun bind(attachment: Attachment)

    public open fun unbind() {
    }
}
