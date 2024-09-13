package network.ermis.client.errorhandler.factory

import network.ermis.client.errorhandler.ErrorHandler

public interface ErrorHandlerFactory {

    /**
     * Provides a single instance of [ErrorHandler].
     *
     * @return The [ErrorHandler] instance.
     */
    public fun create(): ErrorHandler
}
