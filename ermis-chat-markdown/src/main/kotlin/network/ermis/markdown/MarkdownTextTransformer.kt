package network.ermis.markdown

import android.content.Context
import android.widget.TextView
import io.noties.markwon.Markwon
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import network.ermis.ui.helper.transformer.ChatMessageTextTransformer
import network.ermis.ui.utils.Linkify
import network.ermis.ui.view.messages.adapter.MessageListItem

/**
 * Markdown based implementation of [ChatMessageTextTransformer] that parses the message text as Markdown
 * and apply it to [TextView].
 */
public class MarkdownTextTransformer @JvmOverloads constructor(
    context: Context,
    private val getDisplayedText: (messageItem: MessageListItem.MessageItem) -> String = { it.message.text },
) : ChatMessageTextTransformer {
    private val markwon: Markwon = Markwon.builder(context)
        .usePlugin(CorePlugin.create())
        .usePlugin(LinkifyPlugin.create())
        .usePlugin(ImagesPlugin.create())
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(SoftBreakAddsNewLinePlugin.create())
        .build()

    override fun transformAndApply(textView: TextView, messageItem: MessageListItem.MessageItem) {
        val displayedText = getDisplayedText(messageItem)
        markwon.setMarkdown(textView, displayedText.fixMarkdownItalicAtEnd())
        Linkify.addLinks(textView)
    }
}
