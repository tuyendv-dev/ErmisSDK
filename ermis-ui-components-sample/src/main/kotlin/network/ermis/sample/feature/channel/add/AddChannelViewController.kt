
package network.ermis.sample.feature.channel.add

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import network.ermis.core.models.User
import network.ermis.ui.view.messages.composer.MessageComposerView
import network.ermis.ui.view.MessageListView
import network.ermis.chat.ui.sample.R
import network.ermis.sample.feature.channel.add.header.AddChannelHeader
import network.ermis.sample.feature.channel.add.header.MembersInputChangedListener
import io.getstream.log.taggedLogger

class AddChannelViewController(
    private val headerView: AddChannelHeader,
    private val usersTitle: TextView,
    private val usersRecyclerView: RecyclerView,
    private val createGroupContainer: ViewGroup,
    private val messageListView: MessageListView,
    private val messageComposerView: MessageComposerView,
    private val emptyStateView: View,
    private val loadingView: View,
    private val isAddGroupChannel: Boolean,
) {

    private val logger by taggedLogger("AddChannelViewController")
    private val usersAdapter = AddChannelUsersAdapter()
    private val members: MutableList<User> = mutableListOf()
    private val userInfoList: MutableList<UserInfo> = mutableListOf()
    private var isSearching = false

    var membersChangedListener = AddChannelView.MembersChangedListener {}
    var searchInputChangedListener = AddChannelView.SearchInputChangedListener { }

    init {
        usersRecyclerView.adapter = usersAdapter
        usersAdapter.userClickListener = AddChannelUsersAdapter.UserClickListener {
            onUserClicked(it)
        }
        headerView.apply {
            membersInputListener = MembersInputChangedListener { query ->
                usersTitle.text = if (query.isEmpty()) {
                    isSearching = false
                    viewContext.getString(R.string.add_channel_user_list_title)
                } else {
                    isSearching = true
                    viewContext.getString(R.string.add_channel_user_list_search_title, query)
                }
                searchInputChangedListener.onInputChanged(query)
            }
            setAddMemberButtonClickListener {
                this@AddChannelViewController.showInput()
                showUsersView()
            }
            setMemberClickListener {
                onUserClicked(UserInfo(it, true))
            }
        }
    }

    fun setUsers(users: List<User>) {
        userInfoList.clear()
        addMoreUsers(users) {
            showUsersView()
        }
    }

    fun addMoreUsers(users: List<User>, usersSubmittedCallback: () -> Unit = {}) {
        userInfoList.addAll(users.map { UserInfo(it, members.contains(it)) })
        showUsers(userInfoList, usersSubmittedCallback)
    }

    fun setMembers(members: List<User>) {
        this.members.clear()
        this.members.addAll(members)
        headerView.setMembers(members.toList())

        showUsers(userInfoList.map { UserInfo(it.user, members.contains(it.user)) })
    }

    fun messageComposerViewClicked() {
        if (members.isNotEmpty()) {
            showMessageListView()
        }
    }

    private fun showUsersView() {
        usersRecyclerView.isVisible = userInfoList.isNotEmpty()
        emptyStateView.isVisible = userInfoList.isEmpty()
        usersTitle.isVisible = true
        messageListView.isVisible = false
        loadingView.isVisible = false
    }

    private fun showMessageListView() {
        usersRecyclerView.isVisible = false
        emptyStateView.isVisible = false
        usersTitle.isVisible = false
        messageListView.isVisible = true
        loadingView.isVisible = false
        hideInput()
    }

    private fun showUsers(users: List<UserInfo>, usersSubmittedCallback: () -> Unit = {}) {
        if (isSearching) {
            usersAdapter.submitList(users.map { UserListItem.UserItem(it) }, usersSubmittedCallback)
        } else {
            showSectionedUsers(users, usersSubmittedCallback)
        }
    }

    private fun showSectionedUsers(userInfoList: List<UserInfo>, usersSubmittedCallback: () -> Unit) {
        // TODO tuyendv bỏ sắp xếp theo tên có header kí tự đầu
        // val sectionedUsers = userInfoList
        //     .groupBy { it.user.name.firstOrNull()?.uppercaseChar() ?: EMPTY_NAME_SYMBOL }
        //     .toSortedMap()//Comparator.reverseOrder()
        //     .flatMap { (letter, users) ->
        //         mutableListOf(UserListItem.Separator(letter)) + users.map { UserListItem.UserItem(it) }
        //     }

        val sectionedUsers = userInfoList.map { UserListItem.UserItem(it) }
        usersAdapter.submitList(sectionedUsers, usersSubmittedCallback)
    }

    private fun hideInput() {
        headerView.hideInput()
        // headerView.showAddMemberButton()
        headerView.hideAddMemberButton()
    }

    private fun showInput() {
        // headerView.showInput()
        headerView.hideInput()
        headerView.hideAddMemberButton()
    }

    private fun onUserClicked(userInfo: UserInfo) {
        logger.d { "isAddGroupChannel= $isAddGroupChannel onUserClicked= $userInfo" }
        // Update members
        if (isAddGroupChannel) {
            if (userInfo.isSelected) {
                members.remove(userInfo.user)
            } else {
                members.add(userInfo.user)
            }
        } else {
            members.clear()
            members.add(userInfo.user)
        }
        if (members.isEmpty()) {
            if (!isAddGroupChannel) {
                messageComposerView.isVisible = false
                createGroupContainer.isVisible = true
            }
            showUsersView()
            showInput()
        }
        // else {
        //     if (!isAddGroupChannel) {
        //         messageComposerView.isVisible = true
        //         createGroupContainer.isVisible = false
        //     }
        //     hideInput()
        // }
        if (isAddGroupChannel) {
            headerView.setMembers(members.toList())
        }
        membersChangedListener.onMembersChanged(members)

        // Update user list
        val index = userInfoList.indexOf(userInfo)
        if (index != -1) {
            if (isAddGroupChannel) {
                userInfoList[index] = userInfoList[index].copy(isSelected = !userInfo.isSelected)
                showUsers(userInfoList)
            }
        }
    }

    companion object {
        const val EMPTY_NAME_SYMBOL = Char.MAX_VALUE
    }
}
