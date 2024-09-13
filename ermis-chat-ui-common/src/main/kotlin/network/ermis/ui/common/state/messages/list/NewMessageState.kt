
package network.ermis.ui.common.state.messages.list

/**
 * Represents the state when a new message arrives to the channel.
 */
public sealed class NewMessageState

/**
 * If the message is our own (we sent it), we scroll to the bottom of the list.
 */
public data class MyOwn(val ts: Long?) : NewMessageState()

/**
 * If the message is someone else's (we didn't send it), we show a "New message" bubble.
 */
public data class Other(val ts: Long?) : NewMessageState()
