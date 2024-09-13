
package network.ermis.ui.viewmodel.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Specialized factory class that produces [ChannelActionsViewModel].
 *
 * @param cid The full channel id, i.e. "messaging:123".
 */
internal class ChannelActionsViewModelFactory(
    private val cid: String,
) : ViewModelProvider.Factory {

    /**
     * Creates an instance of [ChannelActionsViewModel].
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChannelActionsViewModel(cid) as T
    }
}
