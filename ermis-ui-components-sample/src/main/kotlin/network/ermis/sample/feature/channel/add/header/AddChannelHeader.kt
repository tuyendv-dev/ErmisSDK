
package network.ermis.sample.feature.channel.add.header

import android.content.Context
import network.ermis.core.models.User

interface AddChannelHeader {
    val viewContext: Context
    var membersInputListener: MembersInputChangedListener

    fun setMembers(members: List<User>)

    fun showInput()

    fun hideInput()

    fun showAddMemberButton()

    fun hideAddMemberButton()

    fun setAddMemberButtonClickListener(listener: AddMemberButtonClickListener)

    fun setMemberClickListener(listener: MemberClickListener?)
}

fun interface MembersInputChangedListener {
    fun onMembersInputChanged(query: String)
}

fun interface AddMemberButtonClickListener {
    fun onButtonClick()
}

fun interface MemberClickListener {
    fun onMemberClicked(user: User)
}
