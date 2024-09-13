package network.ermis.client.plugin

import kotlin.reflect.KClass

/**
 * Resolves requested dependencies.
 */
public interface DependencyResolver {

    /**
     * Resolves dependencies to [T] objects.
     *
     * @see [KClass] to look for.
     */
    public fun <T : Any> resolveDependency(klass: KClass<T>): T?
}
