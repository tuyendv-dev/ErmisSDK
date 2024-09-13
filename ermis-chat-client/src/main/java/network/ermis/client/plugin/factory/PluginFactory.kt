package network.ermis.client.plugin.factory

import network.ermis.client.plugin.DependencyResolver
import network.ermis.client.plugin.Plugin
import network.ermis.core.models.User

/**
 * Interface used to add new plugins to the SDK. Use this to provide a [Plugin] that will be used to cause side effects
 * in certain API calls.
 */
public interface PluginFactory : DependencyResolver {

    /**
     * Creates a [Plugin]
     *
     * @return The [Plugin] instance.
     */
    public fun get(user: User): Plugin
}
