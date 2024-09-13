
package network.ermis.ui

import android.content.Context
import network.ermis.ui.common.helper.DateFormatter
import network.ermis.ui.common.helper.ImageHeadersProvider
import network.ermis.ui.common.images.internal.ErmisImageLoader
import network.ermis.ui.common.images.resizing.ErmisImageResizing
import network.ermis.ui.common.utils.ChannelNameFormatter
import network.ermis.ui.view.messages.composer.attachment.preview.AttachmentPreviewFactoryManager
import network.ermis.ui.view.messages.adapter.viewholder.attachment.AttachmentFactoryManager
import network.ermis.ui.view.messages.adapter.viewholder.attachment.DefaultQuotedAttachmentMessageFactory
import network.ermis.ui.view.messages.adapter.viewholder.attachment.QuotedAttachmentFactoryManager
import network.ermis.ui.view.messages.adapter.viewholder.decorator.DecoratorProviderFactory
import network.ermis.ui.font.ChatFonts
import network.ermis.ui.font.ChatFontsImpl
import network.ermis.ui.font.ChatStyle
import network.ermis.ui.helper.CurrentUserProvider
import network.ermis.ui.helper.MessagePreviewFormatter
import network.ermis.ui.helper.MimeTypeIconProvider
import network.ermis.ui.helper.MimeTypeIconProviderImpl
import network.ermis.ui.helper.SupportedReactions
import network.ermis.ui.helper.transformer.AutoLinkableTextTransformer
import network.ermis.ui.helper.transformer.ChatMessageTextTransformer
import network.ermis.ui.navigation.ChatNavigator
import network.ermis.ui.utils.extensions.getTranslatedText
import network.ermis.ui.utils.lazyVar
import network.ermis.ui.widgets.avatar.ChannelAvatarRenderer
import network.ermis.ui.widgets.avatar.UserAvatarRenderer

/**
 * ChatUI handles any configuration for the Chat UI elements.
 *
 * @see ChatMessageTextTransformer
 * @see ChatFonts
 * @see ImageHeadersProvider
 */
public object ChatUI {
    internal lateinit var appContext: Context

    @JvmStatic
    public var style: ChatStyle = ChatStyle()

    /**
     * A class responsible for handling navigation to chat destinations. Allows overriding
     * a default navigation between chat components.
     */
    @JvmStatic
    public var navigator: ChatNavigator = ChatNavigator()

    /**
     * Provides HTTP headers for image loading requests.
     */
    @JvmStatic
    public var imageHeadersProvider: ImageHeadersProvider by ErmisImageLoader.instance()::imageHeadersProvider

    /**
     * Allows setting default fonts used by UI components.
     */
    @JvmStatic
    public var fonts: ChatFonts by lazyVar { ChatFontsImpl(style, appContext) }

    /**
     * Allows customising the message text's format or style.
     *
     * For example, it can be used to provide markdown support in chat or it can be used
     * to highlight specific messages by making them bold etc.
     */
    @JvmStatic
    public var messageTextTransformer: ChatMessageTextTransformer by lazyVar {
        AutoLinkableTextTransformer { textView, messageItem ->
            // Customize the transformer if needed
            val displayedText = messageItem.message.getTranslatedText()
            textView.text = displayedText
        }
    }

    /**
     * Allows overriding default set of message reactions available.
     */
    @JvmStatic
    public var supportedReactions: SupportedReactions by lazyVar { SupportedReactions(appContext) }

    /**
     * Allows overriding default icons for attachments MIME types.
     */
    @JvmStatic
    public var mimeTypeIconProvider: MimeTypeIconProvider by lazyVar { MimeTypeIconProviderImpl() }

    /**
     * Allows to generate a name for the given channel.
     */
    @JvmStatic
    public var channelNameFormatter: ChannelNameFormatter by lazyVar {
        ChannelNameFormatter.defaultFormatter(appContext)
    }

    /**
     *  Allows to generate a preview text for the given message.
     */
    @JvmStatic
    public var messagePreviewFormatter: MessagePreviewFormatter by lazyVar {
        MessagePreviewFormatter.defaultFormatter(appContext)
    }

    /**
     * Allows formatting date-time objects as strings.
     */
    @JvmStatic
    public var dateFormatter: DateFormatter by lazyVar { DateFormatter.from(appContext) }

    /**
     * Allows adding support for custom attachments in the message list.
     */
    @JvmStatic
    public var decoratorProviderFactory: DecoratorProviderFactory by lazyVar { DecoratorProviderFactory.defaultFactory() }

    /**
     * Allows adding support for custom attachments in the message list.
     */
    @JvmStatic
    public var attachmentFactoryManager: AttachmentFactoryManager by lazyVar { AttachmentFactoryManager() }

    /**
     * Allows adding support for custom attachments in the preview section of the message composer.
     */
    @JvmStatic
    public var attachmentPreviewFactoryManager: AttachmentPreviewFactoryManager by lazyVar { AttachmentPreviewFactoryManager() }

    /**
     * Allows adding support for custom attachment inside quoted messages in the message list. If none are found here
     * will default to [attachmentFactoryManager].
     */
    @JvmStatic
    public var quotedAttachmentFactoryManager: QuotedAttachmentFactoryManager by lazyVar {
        QuotedAttachmentFactoryManager(
            listOf(DefaultQuotedAttachmentMessageFactory()),
        )
    }

    /**
     * Provides the currently logged in user.
     */
    @JvmStatic
    public var currentUserProvider: CurrentUserProvider = CurrentUserProvider.defaultCurrentUserProvider()

    /**
     * Whether thumbnails for video attachments will be displayed in previews.
     */
    @JvmStatic
    public var videoThumbnailsEnabled: Boolean = true

    /**
     * Sets the strategy for resizing images hosted on Stream's CDN. Disabled by default,
     * set [ErmisImageResizing.imageResizingEnabled] to true if you wish to enable resizing images. Note that
     * resizing applies only to images hosted on Stream's CDN which contain the original width (ow) and height (oh)
     * query parameters.
     */
    @JvmStatic
    public var streamCdnImageResizing: ErmisImageResizing = ErmisImageResizing.defaultStreamCdnImageResizing()

    /**
     * Whether or not the auto-translation feature is enabled.
     */
    @JvmStatic
    public var autoTranslationEnabled: Boolean = false

    /**
     * Provides a custom renderer for user avatars.
     */
    @JvmStatic
    public var userAvatarRenderer: UserAvatarRenderer? = null

    /**
     * Provides a custom renderer for channel avatars.
     */
    @JvmStatic
    public var channelAvatarRenderer: ChannelAvatarRenderer? = null
}
