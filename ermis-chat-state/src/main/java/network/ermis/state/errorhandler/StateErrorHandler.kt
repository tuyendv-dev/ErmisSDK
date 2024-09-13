package network.ermis.state.errorhandler

import network.ermis.client.errorhandler.CreateChannelErrorHandler
import network.ermis.client.errorhandler.DeleteReactionErrorHandler
import network.ermis.client.errorhandler.ErrorHandler
import network.ermis.client.errorhandler.QueryMembersErrorHandler
import network.ermis.client.errorhandler.SendReactionErrorHandler

internal class StateErrorHandler(
    private val deleteReactionErrorHandler: DeleteReactionErrorHandler,
    private val createChannelErrorHandler: CreateChannelErrorHandler,
    private val queryMembersErrorHandler: QueryMembersErrorHandler,
    private val sendReactionErrorHandler: SendReactionErrorHandler,
) : ErrorHandler,
    DeleteReactionErrorHandler by deleteReactionErrorHandler,
    CreateChannelErrorHandler by createChannelErrorHandler,
    QueryMembersErrorHandler by queryMembersErrorHandler,
    SendReactionErrorHandler by sendReactionErrorHandler {

    override val priority: Int
        get() = ErrorHandler.DEFAULT_PRIORITY
}
