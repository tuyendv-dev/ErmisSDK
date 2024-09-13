
package network.ermis.ui.view.messages.adapter.viewholder.decorator

/**
 * A provider for the list of [Decorator]s that will be used to decorate the message list items.
 */
public interface DecoratorProvider {
    /**
     * The list of [Decorator]s that will be used to decorate the message list items.
     */
    public val decorators: List<Decorator>
}

/**
 * Combines two [DecoratorProvider]s into a single one.
 */
public operator fun DecoratorProvider.plus(
    other: DecoratorProvider,
): DecoratorProvider = object : DecoratorProvider {
    override val decorators: List<Decorator> by lazy {
        this@plus.decorators + other.decorators
    }
}
