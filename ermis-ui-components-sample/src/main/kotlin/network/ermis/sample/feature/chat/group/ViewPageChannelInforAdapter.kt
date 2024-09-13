package network.ermis.sample.feature.chat.group

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import network.ermis.sample.feature.chat.group.member.GroupChatMembersFragment
import network.ermis.sample.feature.chat.info.shared.files.ChatInfoSharedFilesFragment
import network.ermis.sample.feature.chat.info.shared.media.ChatInfoSharedMediaFragment

class ViewPageChannelInforAdapter(
    private val fragment: Fragment,
    private val cid: String,
    private val isGroup: Boolean = false,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return if (isGroup) 3 else 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (isGroup) {
            when (position) {
                0 -> getFragmentMembers()
                1 -> getFragmentMedia()
                2 -> getFragmentFile()
                else -> getFragmentMembers()
            }
        } else {
            when (position) {
                0 -> getFragmentMedia()
                1 -> getFragmentFile()
                else -> getFragmentMembers()
            }
        }
    }

    fun getTitleTab(position: Int): String {
        return if (isGroup) {
            when (position) {
                0 -> "Members"
                1 -> "Media"
                2 -> "Files"
                else -> "Links"
            }
        } else {
            when (position) {
                0 -> "Media"
                1 -> "Files"
                else -> "Links"
            }
        }
    }

    private fun getFragmentMembers(): GroupChatMembersFragment {
        return GroupChatMembersFragment().apply {
            val bundleSend = Bundle()
            bundleSend.putString(GroupChatMembersFragment.KEY_CID_BUNDLE, cid)
            bundleSend.putBoolean(GroupChatMembersFragment.KEY_IS_EDIT_MEMBERS, false)
            arguments = bundleSend
        }
    }

    private fun getFragmentMedia(): ChatInfoSharedMediaFragment {
        return ChatInfoSharedMediaFragment().apply {
            val bundleSend = Bundle()
            bundleSend.putString(ChatInfoSharedMediaFragment.KEY_CID_BUNDLE, cid)
            bundleSend.putBoolean(ChatInfoSharedMediaFragment.KEY_SHOW_TOOLBAR, false)
            arguments = bundleSend
        }
    }

    private fun getFragmentFile(): ChatInfoSharedFilesFragment {
        return ChatInfoSharedFilesFragment().apply {
            val bundleSend = Bundle()
            bundleSend.putString(ChatInfoSharedFilesFragment.KEY_CID_BUNDLE, cid)
            bundleSend.putBoolean(ChatInfoSharedFilesFragment.KEY_SHOW_TOOLBAR, false)
            arguments = bundleSend
        }
    }
}