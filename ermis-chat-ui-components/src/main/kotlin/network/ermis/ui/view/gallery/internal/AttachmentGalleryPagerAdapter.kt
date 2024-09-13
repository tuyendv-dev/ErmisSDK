
package network.ermis.ui.view.gallery.internal

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import network.ermis.core.models.Attachment
import network.ermis.core.models.AttachmentType

internal class AttachmentGalleryPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val mediaList: List<Attachment>,
    private val mediaClickListener: () -> Unit,
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = mediaList.size

    override fun createFragment(position: Int): Fragment {
        val attachment = getItem(position)

        return when (attachment.type) {
            AttachmentType.IMAGE -> AttachmentGalleryImagePageFragment.create(attachment, mediaClickListener)
            AttachmentType.VIDEO -> AttachmentGalleryVideoPageFragment.create(attachment, mediaClickListener)
            else -> throw Throwable("Unsupported attachment type")
        }
    }

    fun getItem(position: Int): Attachment = mediaList[position]
}
