package network.ermis.client.notifications.handler

import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmapOrNull
import network.ermis.core.internal.coroutines.DispatcherProvider
import network.ermis.core.models.User
import kotlinx.coroutines.withContext
import java.net.URL

/**
 * Creates an [IconCompat] for a given user to be shown on notifications.
 */
public interface UserIconBuilder {

    /**
     * Creates an [IconCompat] for a given user or null if it cannot be created.
     *
     * @param user from which the [IconCompat] should be created.
     *
     * @return an [IconCompat] for the given user or null.
     */
    public suspend fun buildIcon(user: User): IconCompat?
}

/**
 * Default implementation of [UserIconBuilder].
 */
internal class DefaultUserIconBuilder(val context: Context) : UserIconBuilder {
    override suspend fun buildIcon(user: User): IconCompat? =
        user.image
            .takeUnless { it.isEmpty() }
            ?.let {
                withContext(DispatcherProvider.IO) {
                    runCatching {
                        URL(it).openStream().use {
                            RoundedBitmapDrawableFactory.create(
                                context.resources,
                                BitmapFactory.decodeStream(it),
                            )
                                .apply { isCircular = true }
                                .toBitmapOrNull()
                        }
                            ?.let(IconCompat::createWithBitmap)
                    }.getOrNull()
                }
            }
}
