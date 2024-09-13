/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
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

package network.ermis.sample.feature.chat.messagelist

import network.ermis.core.models.Channel
import network.ermis.ui.common.helper.DateFormatter
import network.ermis.ui.common.state.messages.list.DeletedMessageVisibility
import network.ermis.ui.view.MessageListView
import network.ermis.ui.view.messages.MessageListViewStyle
import network.ermis.ui.view.messages.adapter.viewholder.decorator.DecoratorProvider
import network.ermis.ui.view.messages.adapter.viewholder.decorator.DecoratorProviderFactory
import network.ermis.ui.view.messages.background.MessageBackgroundFactory

class CustomDecoratorProviderFactory : DecoratorProviderFactory {
    override fun createDecoratorProvider(
        channel: Channel,
        dateFormatter: DateFormatter,
        messageListViewStyle: MessageListViewStyle,
        showAvatarPredicate: MessageListView.ShowAvatarPredicate,
        messageBackgroundFactory: MessageBackgroundFactory,
        deletedMessageVisibility: () -> DeletedMessageVisibility,
        getLanguageDisplayName: (code: String) -> String,
    ): DecoratorProvider = CustomDecoratorProvider(
        channel,
        dateFormatter,
        messageListViewStyle,
        showAvatarPredicate,
        messageBackgroundFactory,
        deletedMessageVisibility,
        getLanguageDisplayName,
    )
}

class CustomDecoratorProvider(
    channel: Channel,
    dateFormatter: DateFormatter,
    messageListViewStyle: MessageListViewStyle,
    showAvatarPredicate: MessageListView.ShowAvatarPredicate,
    messageBackgroundFactory: MessageBackgroundFactory,
    deletedMessageVisibility: () -> DeletedMessageVisibility,
    getLanguageDisplayName: (code: String) -> String,
) : DecoratorProvider {
    override val decorators by lazy {
        listOf(ForwardedDecorator())
    }
}
