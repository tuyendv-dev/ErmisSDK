
package network.ermis.ui.common.state.messages.list

import network.ermis.core.models.Message

/**
 * Represents the list of actions users can take with ephemeral giphy messages.
 *
 * @property message The ephemeral giphy message.
 */
public sealed class GiphyAction {
    public abstract val message: Message
}

/**
 * Send the selected giphy message to the channel.
 */
public data class SendGiphy(override val message: Message) : GiphyAction()

/**
 * Perform the giphy shuffle operation.
 */
public data class ShuffleGiphy(override val message: Message) : GiphyAction()

/**
 * Cancel the ephemeral message.
 */
public data class CancelGiphy(override val message: Message) : GiphyAction()
