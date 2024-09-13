package network.ermis.client.plugin.factory

import network.ermis.client.plugin.Plugin
import network.ermis.client.plugin.ThrottlingPlugin
import network.ermis.core.models.User
import kotlin.reflect.KClass

internal object ThrottlingPluginFactory : PluginFactory {
    override fun <T : Any> resolveDependency(klass: KClass<T>): T? = null
    override fun get(user: User): Plugin = ThrottlingPlugin()
}
