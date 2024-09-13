/*
 * Copyright (c) 2014-2023 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-chat-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package network.ermis.ui.view.messages.adapter.viewholder.internal

import android.view.ViewGroup
import network.ermis.ui.components.R
import network.ermis.ui.components.databinding.ItemUnreadSeparatorBinding
import network.ermis.ui.view.messages.MessageListItemStyle
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemPayloadDiff
import network.ermis.ui.view.messages.adapter.internal.DecoratedBaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.streamThemeInflater

internal class UnreadSeparatorViewHolder(
    parent: ViewGroup,
    decorators: List<Decorator>,
    private val style: MessageListItemStyle,
    internal val binding: ItemUnreadSeparatorBinding = ItemUnreadSeparatorBinding.inflate(
        parent.streamThemeInflater,
        parent,
        false,
    ),
) : DecoratedBaseMessageItemViewHolder<MessageListItem.UnreadSeparatorItem>(binding.root, decorators) {

    override fun bindData(data: MessageListItem.UnreadSeparatorItem, diff: MessageListItemPayloadDiff?) {
        super.bindData(data, diff)

        binding.root.setBackgroundColor(style.unreadSeparatorBackgroundColor)
        binding.unreadSeparatorLabel.setTextStyle(style.unreadSeparatorTextStyle)
        binding.unreadSeparatorLabel.text =
            context.resources.getString(R.string.ermis_ui_message_list_unread_separator)
    }
}
