
package network.ermis.ui.view.messages.composer.attachment.picker.factory.camera

import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import network.ermis.ui.view.messages.composer.attachment.picker.AttachmentsPickerDialogStyle
import network.ermis.ui.view.messages.composer.attachment.picker.factory.AttachmentsPickerTabFactory
import network.ermis.ui.view.messages.composer.attachment.picker.factory.AttachmentsPickerTabListener

/**
 * A factory responsible for creating camera attachments tab in the attachments picker.
 */
public class AttachmentsPickerCameraTabFactory : AttachmentsPickerTabFactory {

    /**
     * Creates an icon for camera attachments tab.
     *
     * @param style The style for the attachment picker dialog.
     * @return The Drawable used as the teb icon.
     */
    override fun createTabIcon(style: AttachmentsPickerDialogStyle): Drawable {
        return style.cameraAttachmentsTabIconDrawable
    }

    /**
     * Provides a new Fragment associated with this camera attachments tab.
     *
     * @param style The style for the attachment picker dialog.
     * @param attachmentsPickerTabListener The listener invoked when attachments are selected in the tab.
     * @return A new content Fragment for the tab.
     */
    override fun createTabFragment(
        style: AttachmentsPickerDialogStyle,
        attachmentsPickerTabListener: AttachmentsPickerTabListener,
    ): Fragment {
        return CameraAttachmentFragment.newInstance(style).apply {
            setAttachmentsPickerTabListener(attachmentsPickerTabListener)
        }
    }
}
