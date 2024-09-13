package network.ermis.ui.common.disposable

/**
 * Disposable resource. Implementations of this interface can be disposed once their work is done or the result is
 * no longer necessary
 */
public interface Disposable {

    public val isDisposed: Boolean

    /**
     * Disposes the resource
     */
    public fun dispose()
}
