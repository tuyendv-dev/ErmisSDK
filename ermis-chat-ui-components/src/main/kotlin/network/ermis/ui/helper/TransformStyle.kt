
package network.ermis.ui.helper

import network.ermis.ui.view.channels.actions.ChannelActionsDialogViewStyle
import network.ermis.ui.view.channels.ChannelListFragmentViewStyle
import network.ermis.ui.view.channels.ChannelListViewStyle
import network.ermis.ui.view.gallery.AttachmentGalleryViewMediaStyle
import network.ermis.ui.view.gallery.MediaAttachmentGridViewStyle
import network.ermis.ui.view.gallery.options.AttachmentGalleryOptionsViewStyle
import network.ermis.ui.view.mentions.MentionListViewStyle
import network.ermis.ui.view.messages.common.AudioRecordPlayerViewStyle
import network.ermis.ui.view.messages.composer.MessageComposerViewStyle
import network.ermis.ui.view.messages.composer.attachment.picker.AttachmentsPickerDialogStyle
import network.ermis.ui.view.messages.header.MessageListHeaderViewStyle
import network.ermis.ui.view.messages.DefaultQuotedAttachmentViewStyle
import network.ermis.ui.view.messages.FileAttachmentViewStyle
import network.ermis.ui.view.messages.GiphyViewHolderStyle
import network.ermis.ui.view.messages.MessageListItemStyle
import network.ermis.ui.view.messages.MessageListViewStyle
import network.ermis.ui.view.messages.MessageReplyStyle
import network.ermis.ui.view.messages.ScrollButtonViewStyle
import network.ermis.ui.view.messages.UnsupportedAttachmentViewStyle
import network.ermis.ui.view.messages.adapter.view.MediaAttachmentViewStyle
import network.ermis.ui.view.messages.reactions.edit.EditReactionsViewStyle
import network.ermis.ui.view.messages.reactions.user.SingleReactionViewStyle
import network.ermis.ui.view.messages.reactions.view.ViewReactionsViewStyle
import network.ermis.ui.view.pinned.PinnedMessageListViewStyle
import network.ermis.ui.view.search.SearchInputViewStyle
import network.ermis.ui.view.search.SearchResultListViewStyle
import network.ermis.ui.widgets.avatar.AvatarStyle
import network.ermis.ui.widgets.typing.TypingIndicatorViewStyle

public object TransformStyle {
    @JvmStatic
    public var avatarStyleTransformer: StyleTransformer<AvatarStyle> = noopTransformer()

    @JvmStatic
    public var channelListFragmentStyleTransformer: StyleTransformer<ChannelListFragmentViewStyle> =
        noopTransformer()

    @JvmStatic
    public var channelListStyleTransformer: StyleTransformer<ChannelListViewStyle> = noopTransformer()

    @JvmStatic
    public var messageListStyleTransformer: StyleTransformer<MessageListViewStyle> = noopTransformer()

    @JvmStatic
    public var messageListItemStyleTransformer: StyleTransformer<MessageListItemStyle> = noopTransformer()

    @JvmStatic
    public var scrollButtonStyleTransformer: StyleTransformer<ScrollButtonViewStyle> = noopTransformer()

    @JvmStatic
    public var viewReactionsStyleTransformer: StyleTransformer<ViewReactionsViewStyle> = noopTransformer()

    @JvmStatic
    public var editReactionsStyleTransformer: StyleTransformer<EditReactionsViewStyle> = noopTransformer()

    @JvmStatic
    public var singleReactionViewStyleTransformer: StyleTransformer<SingleReactionViewStyle> = noopTransformer()

    @JvmStatic
    public var channelActionsDialogStyleTransformer: StyleTransformer<ChannelActionsDialogViewStyle> = noopTransformer()

    @JvmStatic
    public var giphyViewHolderStyleTransformer: StyleTransformer<GiphyViewHolderStyle> = noopTransformer()

    @JvmStatic
    public var mediaAttachmentStyleTransformer: StyleTransformer<MediaAttachmentViewStyle> = noopTransformer()

    @JvmStatic
    public var messageReplyStyleTransformer: StyleTransformer<MessageReplyStyle> = noopTransformer()

    @JvmStatic
    public var fileAttachmentStyleTransformer: StyleTransformer<FileAttachmentViewStyle> = noopTransformer()

    @JvmStatic
    public var unsupportedAttachmentStyleTransformer: StyleTransformer<UnsupportedAttachmentViewStyle> =
        noopTransformer()

    @JvmStatic
    public var messageListHeaderStyleTransformer: StyleTransformer<MessageListHeaderViewStyle> = noopTransformer()

    @JvmStatic
    public var mentionListViewStyleTransformer: StyleTransformer<MentionListViewStyle> = noopTransformer()

    @JvmStatic
    public var searchInputViewStyleTransformer: StyleTransformer<SearchInputViewStyle> = noopTransformer()

    @JvmStatic
    public var searchResultListViewStyleTransformer: StyleTransformer<SearchResultListViewStyle> = noopTransformer()

    @JvmStatic
    public var typingIndicatorViewStyleTransformer: StyleTransformer<TypingIndicatorViewStyle> = noopTransformer()

    @JvmStatic
    public var pinnedMessageListViewStyleTransformer: StyleTransformer<PinnedMessageListViewStyle> = noopTransformer()

    @JvmStatic
    public var defaultQuotedAttachmentViewStyleTransformer: StyleTransformer<DefaultQuotedAttachmentViewStyle> =
        noopTransformer()

    @JvmStatic
    public var attachmentGalleryOptionsStyleTransformer: StyleTransformer<AttachmentGalleryOptionsViewStyle> =
        noopTransformer()

    @JvmStatic
    public var messageComposerStyleTransformer: StyleTransformer<MessageComposerViewStyle> = noopTransformer()

    @JvmStatic
    public var attachmentsPickerStyleTransformer: StyleTransformer<AttachmentsPickerDialogStyle> = noopTransformer()

    @JvmStatic
    public var attachmentGalleryViewMediaStyle: StyleTransformer<AttachmentGalleryViewMediaStyle> = noopTransformer()

    @JvmStatic
    public var mediaAttachmentGridViewStyle: StyleTransformer<MediaAttachmentGridViewStyle> = noopTransformer()

    @JvmStatic
    public var audioRecordPlayerViewStyle: StyleTransformer<AudioRecordPlayerViewStyle> = noopTransformer()

    private fun <T> noopTransformer() = StyleTransformer<T> { it }
}

public fun interface StyleTransformer<T> {
    public fun transform(source: T): T
}
