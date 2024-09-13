
package network.ermis.ui.view.messages.composer.attachment.picker.factory

import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import network.ermis.ui.view.messages.composer.attachment.picker.AttachmentsPickerDialogStyle

/**
 * A factory responsible for creating attachments picker tabs.
 */
public interface AttachmentsPickerTabFactory {

    /**
     * Creates an icon for the tab.
     *
     * @param style The style for the attachment picker dialog.
     * @return The Drawable used as the teb icon.
     */
    public fun createTabIcon(style: AttachmentsPickerDialogStyle): Drawable

    /**
     * Provides a new Fragment associated with this attachments picker tab.
     *
     * @param style The style for the attachment picker dialog.
     * @param attachmentsPickerTabListener The listener invoked when attachments are selected in the tab.
     * @return A new content Fragment for the tab.
     */
    public fun createTabFragment(
        style: AttachmentsPickerDialogStyle,
        attachmentsPickerTabListener: AttachmentsPickerTabListener,
    ): Fragment
}
