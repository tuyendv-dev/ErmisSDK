
package network.ermis.ui.view.channels.adapter.viewholder

import network.ermis.ui.view.ChannelListView.ChannelClickListener
import network.ermis.ui.view.ChannelListView.ChannelLongClickListener
import network.ermis.ui.view.ChannelListView.SwipeListener
import network.ermis.ui.view.ChannelListView.UserClickListener
import network.ermis.ui.utils.ListenerDelegate

internal class ChannelListListenerContainerImpl(
    channelClickListener: ChannelClickListener = ChannelClickListener.DEFAULT,
    channelLongClickListener: ChannelLongClickListener = ChannelLongClickListener.DEFAULT,
    deleteClickListener: ChannelClickListener = ChannelClickListener.DEFAULT,
    moreOptionsClickListener: ChannelClickListener = ChannelClickListener.DEFAULT,
    userClickListener: UserClickListener = UserClickListener.DEFAULT,
    swipeListener: SwipeListener = SwipeListener.DEFAULT,
) : ChannelListListenerContainer {

    override var channelClickListener: ChannelClickListener by ListenerDelegate(channelClickListener) { realListener ->
        ChannelClickListener { channel ->
            realListener().onClick(channel)
        }
    }

    override var channelLongClickListener: ChannelLongClickListener by ListenerDelegate(
        channelLongClickListener,
    ) { realListener ->
        ChannelLongClickListener { channel ->
            realListener().onLongClick(channel)
        }
    }

    override var deleteClickListener: ChannelClickListener by ListenerDelegate(deleteClickListener) { realListener ->
        ChannelClickListener { channel ->
            realListener().onClick(channel)
        }
    }

    override var moreOptionsClickListener: ChannelClickListener by ListenerDelegate(
        moreOptionsClickListener,
    ) { realListener ->
        ChannelClickListener { channel ->
            realListener().onClick(channel)
        }
    }

    override var userClickListener: UserClickListener by ListenerDelegate(userClickListener) { realListener ->
        UserClickListener { user ->
            realListener().onClick(user)
        }
    }

    override var swipeListener: SwipeListener by ListenerDelegate(swipeListener) { realListener ->
        object : SwipeListener {
            override fun onSwipeStarted(viewHolder: SwipeViewHolder, adapterPosition: Int, x: Float?, y: Float?) {
                realListener().onSwipeStarted(viewHolder, adapterPosition, x, y)
            }

            override fun onSwipeChanged(
                viewHolder: SwipeViewHolder,
                adapterPosition: Int,
                dX: Float,
                totalDeltaX: Float,
            ) {
                realListener().onSwipeChanged(viewHolder, adapterPosition, dX, totalDeltaX)
            }

            override fun onSwipeCompleted(
                viewHolder: SwipeViewHolder,
                adapterPosition: Int,
                x: Float?,
                y: Float?,
            ) {
                realListener().onSwipeCompleted(viewHolder, adapterPosition, x, y)
            }

            override fun onSwipeCanceled(
                viewHolder: SwipeViewHolder,
                adapterPosition: Int,
                x: Float?,
                y: Float?,
            ) {
                realListener().onSwipeCanceled(viewHolder, adapterPosition, x, y)
            }

            override fun onRestoreSwipePosition(viewHolder: SwipeViewHolder, adapterPosition: Int) {
                realListener().onRestoreSwipePosition(viewHolder, adapterPosition)
            }
        }
    }
}
