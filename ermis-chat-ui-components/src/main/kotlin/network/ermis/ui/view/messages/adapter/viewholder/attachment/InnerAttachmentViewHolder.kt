
package network.ermis.ui.view.messages.adapter.viewholder.attachment

import android.content.Context
import android.view.View
import network.ermis.core.models.Message

/**
 * An inner ViewHolder for custom attachments within message ViewHolder.
 * Follows the lifecycle of the outer message ViewHolder.
 *
 * @param itemView The view that this ViewHolder controls.
 */
public abstract class InnerAttachmentViewHolder(public val itemView: View) {
    public val context: Context = itemView.context

    /**
     * Called when RecyclerView binds the parent message ViewHolder.
     *
     * @param message The message whose attachments will be displayed.
     */
    public open fun onBindViewHolder(message: Message) {}

    /**
     * Called when RecyclerView recycles the parent message ViewHolder.
     */
    public open fun onUnbindViewHolder() {}

    /**
     * Called when a view in this ViewHolder has been attached to a window.
     */
    public open fun onViewAttachedToWindow() {}

    /**
     * Called when a view in this ViewHolder has been detached from its window.
     */
    public open fun onViewDetachedFromWindow() {}

    public companion object {
        /**
         * Creates a stub [InnerAttachmentViewHolder] instance.
         *
         * @param itemView The view that this ViewHolder controls.
         */
        public fun stub(itemView: View): InnerAttachmentViewHolder {
            return object : InnerAttachmentViewHolder(itemView) {}
        }
    }
}
