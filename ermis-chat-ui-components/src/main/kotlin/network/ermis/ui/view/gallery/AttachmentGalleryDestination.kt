
package network.ermis.ui.view.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import network.ermis.ui.view.gallery.internal.AttachmentGalleryRepository
import network.ermis.ui.navigation.destinations.ChatDestination

public class AttachmentGalleryDestination(
    context: Context,
    private val attachmentReplyOptionHandler: AttachmentGalleryActivity.AttachmentReplyOptionHandler,
    private val attachmentShowInChatOptionHandler: AttachmentGalleryActivity.AttachmentShowInChatOptionHandler,
    private val attachmentDownloadOptionHandler: AttachmentGalleryActivity.AttachmentDownloadOptionHandler,
    private val attachmentDeleteOptionClickHandler: AttachmentGalleryActivity.AttachmentDeleteOptionHandler,
) : ChatDestination(context) {
    private var launcher: ActivityResultLauncher<AttachmentGalleryResultContract.Input>? = null

    private lateinit var attachmentGalleryItems: List<AttachmentGalleryItem>
    private var attachmentIndex: Int = 0

    public fun setData(attachmentGalleryItems: List<AttachmentGalleryItem>, attachmentIndex: Int) {
        this.attachmentGalleryItems = attachmentGalleryItems
        this.attachmentIndex = attachmentIndex
    }

    override fun navigate() {
        if (attachmentGalleryItems.isEmpty()) {
            Toast.makeText(context, "Invalid image(s)!", Toast.LENGTH_SHORT).show()
            return
        }

        AttachmentGalleryRepository.setAttachmentGalleryItems(attachmentGalleryItems)
        launcher?.launch(AttachmentGalleryResultContract.Input(attachmentIndex))
    }

    public fun register(activityResultRegistry: ActivityResultRegistry) {
        launcher = activityResultRegistry.register(
            "attachment_gallery_launcher#${hashCode()}",
            AttachmentGalleryResultContract(),
        ) { result ->
            when (result) {
                is AttachmentGalleryActivity.AttachmentOptionResult.Reply -> {
                    attachmentReplyOptionHandler.onClick(result.result)
                }
                is AttachmentGalleryActivity.AttachmentOptionResult.ShowInChat -> {
                    attachmentShowInChatOptionHandler.onClick(result.result)
                }
                is AttachmentGalleryActivity.AttachmentOptionResult.Delete -> {
                    attachmentDeleteOptionClickHandler.onClick(result.result)
                }
                is AttachmentGalleryActivity.AttachmentOptionResult.Download -> {
                    attachmentDownloadOptionHandler.onClick(result.result)
                }
                null -> Unit // No result from Gallery, do nothing
            }
        }
    }

    public fun unregister() {
        launcher?.unregister()
        launcher = null
    }
}

private class AttachmentGalleryResultContract :
    ActivityResultContract<AttachmentGalleryResultContract.Input, AttachmentGalleryActivity.AttachmentOptionResult?>() {

    class Input(val attachmentIndex: Int)

    override fun createIntent(context: Context, input: Input): Intent {
        return AttachmentGalleryActivity.createIntent(context, input.attachmentIndex)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): AttachmentGalleryActivity.AttachmentOptionResult? {
        if (resultCode != Activity.RESULT_OK) {
            return null
        }
        return intent?.getParcelableExtra(AttachmentGalleryActivity.EXTRA_ATTACHMENT_OPTION_RESULT)
    }
}
