package network.ermis.ui.common.notifications

import android.content.Context
import androidx.core.graphics.drawable.IconCompat
import network.ermis.client.notifications.handler.UserIconBuilder
import network.ermis.core.internal.StreamHandsOff
import network.ermis.core.models.User
import network.ermis.ui.common.images.internal.ErmisImageLoader

/**
 * Produces an [IconCompat] using Coil, which downloads and caches the user image.
 */
@StreamHandsOff(
    reason = "This class shouldn't be renamed without verifying it works correctly on Chat Client Artifacts because " +
        "we are using it by reflection",
)
public class ErmisCoilUserIconBuilder(private val context: Context) : UserIconBuilder {
    override suspend fun buildIcon(user: User): IconCompat? =
        ErmisImageLoader
            .instance()
            .loadAsBitmap(context, user.image, ErmisImageLoader.ImageTransformation.Circle)
            ?.let(IconCompat::createWithBitmap)
}
