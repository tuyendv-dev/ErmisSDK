
package network.ermis.ui.viewmodel.pinned

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * A ViewModel factory for PinnedMessageListViewModel.
 *
 * @param cid The channel id in the format messaging:123.
 *
 * @see PinnedMessageListViewModel
 */
public class PinnedMessageListViewModelFactory(private val cid: String?) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(cid != null) {
            "Channel cid should not be null"
        }
        require(modelClass == PinnedMessageListViewModel::class.java) {
            "PinnedMessageListViewModelFactory can only create instances of PinnedMessageListViewModel"
        }

        @Suppress("UNCHECKED_CAST")
        return PinnedMessageListViewModel(cid) as T
    }

    @Suppress("NEWER_VERSION_IN_SINCE_KOTLIN")
    public class Builder
    @SinceKotlin("99999.9")
    constructor() {
        private var cid: String? = null

        /**
         * Sets the channel id in the format messaging:123.
         */
        public fun cid(cid: String): Builder = apply {
            this.cid = cid
        }

        /**
         * Builds [PinnedMessageListViewModelFactory] instance.
         */
        public fun build(): ViewModelProvider.Factory {
            return PinnedMessageListViewModelFactory(
                cid = cid ?: error("Channel cid should not be null"),
            )
        }
    }
}
