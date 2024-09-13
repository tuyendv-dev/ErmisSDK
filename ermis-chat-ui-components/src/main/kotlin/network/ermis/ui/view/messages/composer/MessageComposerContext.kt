package network.ermis.ui.view.messages.composer

import network.ermis.ui.view.messages.composer.content.MessageComposerContentContainer

/**
 * An object with the data that will be propagated to each content view.
 *
 * @param style Style for [MessageComposerView].
 */
public data class MessageComposerContext(
    val style: MessageComposerViewStyle,
    val content: MessageComposerContentContainer,
)