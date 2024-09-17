package network.ermis.state.errorhandler

import kotlinx.coroutines.CoroutineScope
import network.ermis.client.errorhandler.ErrorHandler
import network.ermis.client.errorhandler.factory.ErrorHandlerFactory
import network.ermis.client.persistance.RepositoryFacade
import network.ermis.client.setup.ClientState
import network.ermis.state.errorhandler.internal.CreateChannelErrorHandlerImpl
import network.ermis.state.errorhandler.internal.DeleteReactionErrorHandlerImpl
import network.ermis.state.errorhandler.internal.QueryMembersErrorHandlerImpl
import network.ermis.state.errorhandler.internal.SendReactionErrorHandlerImpl
import network.ermis.state.plugin.logic.LogicRegistry

internal class StateErrorHandlerFactory(
    private val scope: CoroutineScope,
    private val logicRegistry: LogicRegistry,
    private val clientState: ClientState,
    private val repositoryFacade: RepositoryFacade,
) : ErrorHandlerFactory {

    override fun create(): ErrorHandler {
        val deleteReactionErrorHandler = DeleteReactionErrorHandlerImpl(
            scope = scope,
            logic = logicRegistry,
            clientState = clientState,
        )

        val createChannelErrorHandler = CreateChannelErrorHandlerImpl(
            scope = scope,
            clientState = clientState,
            channelRepository = repositoryFacade,
        )

        val queryMembersErrorHandler = QueryMembersErrorHandlerImpl(
            scope = scope,
            clientState = clientState,
            channelRepository = repositoryFacade,
        )

        val sendReactionErrorHandler = SendReactionErrorHandlerImpl(scope = scope, clientState = clientState)

        return StateErrorHandler(
            deleteReactionErrorHandler = deleteReactionErrorHandler,
            createChannelErrorHandler = createChannelErrorHandler,
            queryMembersErrorHandler = queryMembersErrorHandler,
            sendReactionErrorHandler = sendReactionErrorHandler,
        )
    }
}
