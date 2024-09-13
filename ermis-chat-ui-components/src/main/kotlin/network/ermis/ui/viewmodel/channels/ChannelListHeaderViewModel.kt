
package network.ermis.ui.viewmodel.channels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import network.ermis.client.ErmisClient
import network.ermis.client.setup.ClientState
import network.ermis.core.models.ConnectionState
import network.ermis.core.models.User
import network.ermis.ui.view.channels.header.ChannelListHeaderView

/**
 * ViewModel class for [ChannelListHeaderView].
 * Responsible for updating current user information.
 * Can be bound to the view using [ChannelListHeaderViewModel.bindView] function.
 *
 * @param clientState Client state of SDK that contains information such as the current user and connection state.
 * such as the current user, connection state...
 */
public class ChannelListHeaderViewModel @JvmOverloads constructor(
    clientState: ClientState = ErmisClient.instance().clientState,
) : ViewModel() {

    /**
     * The user who is currently logged in.
     */
    public val currentUser: LiveData<User?> = clientState.user.asLiveData()

    /**
     * The state of the connection for the user currently logged in.
     */
    public val connectionState: LiveData<ConnectionState> = clientState.connectionState.asLiveData()
}
