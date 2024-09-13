
package network.ermis.ui.view.messages.composer.attachment.picker.factory

import network.ermis.ui.view.messages.composer.attachment.picker.factory.camera.AttachmentsPickerCameraTabFactory
import network.ermis.ui.view.messages.composer.attachment.picker.factory.file.AttachmentsPickerFileTabFactory
import network.ermis.ui.view.messages.composer.attachment.picker.factory.media.AttachmentsPickerMediaTabFactory

/**
 * Provides the default list of tab factories for the attachment picker.
 */
public object AttachmentsPickerTabFactories {

    /**
     * Creates a list of factories for the tabs that will be displayed in the attachment picker.
     *
     * @param mediaAttachmentsTabEnabled If the media attachments tab will be displayed in the picker.
     * @param fileAttachmentsTabEnabled If the file attachments tab will be displayed in the picker.
     * @param cameraAttachmentsTabEnabled If the camera attachments tab will be displayed in the picker.
     * @return The list factories for the tabs that will be displayed in the attachment picker.
     */
    public fun defaultFactories(
        mediaAttachmentsTabEnabled: Boolean,
        fileAttachmentsTabEnabled: Boolean,
        cameraAttachmentsTabEnabled: Boolean,
    ): List<AttachmentsPickerTabFactory> {
        return listOfNotNull(
            if (mediaAttachmentsTabEnabled) AttachmentsPickerMediaTabFactory() else null,
            if (fileAttachmentsTabEnabled) AttachmentsPickerFileTabFactory() else null,
            if (cameraAttachmentsTabEnabled) AttachmentsPickerCameraTabFactory() else null,
        )
    }
}
