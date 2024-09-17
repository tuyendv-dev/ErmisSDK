package network.ermis.ui.common.images.internal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import coil.drawable.MovieDrawable
import coil.drawable.ScaleDrawable
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import kotlinx.coroutines.withContext
import network.ermis.core.internal.coroutines.DispatcherProvider
import network.ermis.ui.common.disposable.CoilDisposable
import network.ermis.ui.common.disposable.Disposable
import network.ermis.ui.common.helper.DefaultImageHeadersProvider
import network.ermis.ui.common.helper.ImageHeadersProvider
import network.ermis.ui.common.images.internal.ErmisCoil.streamImageLoader
import okhttp3.Headers.Companion.toHeaders

internal object CoilErmisImageLoader : ErmisImageLoader {

    override var imageHeadersProvider: ImageHeadersProvider = DefaultImageHeadersProvider

    override suspend fun loadAsBitmap(
        context: Context,
        url: String,
        transformation: ErmisImageLoader.ImageTransformation,
    ): Bitmap? = withContext(DispatcherProvider.IO) {
        url.takeUnless(String::isBlank)
            ?.let { url ->
                val imageResult = context.streamImageLoader.execute(
                    ImageRequest.Builder(context)
                        .headers(imageHeadersProvider.getImageRequestHeaders().toHeaders())
                        .data(url)
                        .applyTransformation(transformation)
                        .build(),
                )
                (imageResult.drawable as? BitmapDrawable)?.bitmap
            }
    }

    override fun load(
        target: ImageView,
        data: Any?,
        placeholderResId: Int?,
        transformation: ErmisImageLoader.ImageTransformation,
        onStart: () -> Unit,
        onComplete: () -> Unit,
    ): Disposable {
        val context = target.context
        val disposable = target.load(data, context.streamImageLoader) {
            headers(imageHeadersProvider.getImageRequestHeaders().toHeaders())

            if (placeholderResId != null) {
                placeholder(placeholderResId)
                fallback(placeholderResId)
                error(placeholderResId)
            }

            listener(
                onStart = { onStart() },
                onCancel = { onComplete() },
                onError = { _, _ -> onComplete() },
                onSuccess = { _, _ -> onComplete() },
            )
            applyTransformation(transformation)
        }

        return CoilDisposable(disposable)
    }

    override fun load(
        target: ImageView,
        data: Any?,
        placeholderDrawable: Drawable?,
        transformation: ErmisImageLoader.ImageTransformation,
        onStart: () -> Unit,
        onComplete: () -> Unit,
    ): Disposable {
        val context = target.context
        val disposable = target.load(data, context.streamImageLoader) {
            headers(imageHeadersProvider.getImageRequestHeaders().toHeaders())

            if (placeholderDrawable != null) {
                placeholder(placeholderDrawable)
                fallback(placeholderDrawable)
                error(placeholderDrawable)
            }

            listener(
                onStart = { onStart() },
                onCancel = { onComplete() },
                onError = { _, _ -> onComplete() },
                onSuccess = { _, _ -> onComplete() },
            )
            applyTransformation(transformation)
        }

        return CoilDisposable(disposable)
    }

    /**
     * Loads an image into a drawable and then applies the drawable to the container, resizing it based on the scale
     * types and the given configuration.
     *
     * @param target The target to load the image into.
     * @param data The data to load.
     * @param placeholderDrawable Drawable that's shown while the image is loading.
     * @param transformation The transformation for the image before applying to the target.
     * @param onStart The callback when the load has started.
     * @param onComplete The callback when the load has finished.
     */
    override suspend fun loadAndResize(
        target: ImageView,
        data: Any?,
        placeholderDrawable: Drawable?,
        transformation: ErmisImageLoader.ImageTransformation,
        onStart: () -> Unit,
        onComplete: () -> Unit,
    ) {
        val context = target.context

        val drawable = withContext(DispatcherProvider.IO) {
            val result = context.streamImageLoader.execute(
                ImageRequest.Builder(context)
                    .headers(imageHeadersProvider.getImageRequestHeaders().toHeaders())
                    .placeholder(placeholderDrawable)
                    .fallback(placeholderDrawable)
                    .error(placeholderDrawable)
                    .data(data)
                    .listener(
                        onStart = { onStart() },
                        onCancel = { onComplete() },
                        onError = { _, _ -> onComplete() },
                        onSuccess = { _, _ -> onComplete() },
                    )
                    .applyTransformation(transformation)
                    .build(),
            )

            result.drawable
        } ?: return

        if (drawable is ScaleDrawable &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && drawable.child is AnimatedImageDrawable
        ) {
            (drawable.child as AnimatedImageDrawable).start()
        } else if (drawable is MovieDrawable) {
            drawable.start()
        }

        target.setImageDrawable(drawable)
    }

    override fun loadVideoThumbnail(
        target: ImageView,
        uri: Uri?,
        placeholderResId: Int?,
        transformation: ErmisImageLoader.ImageTransformation,
        onStart: () -> Unit,
        onComplete: () -> Unit,
    ): Disposable {
        val context = target.context
        val disposable = target.load(uri, context.streamImageLoader) {
            headers(imageHeadersProvider.getImageRequestHeaders().toHeaders())

            if (placeholderResId != null) {
                placeholder(placeholderResId)
                fallback(placeholderResId)
                error(placeholderResId)
            }

            listener(
                onStart = { onStart() },
                onCancel = { onComplete() },
                onError = { _, _ -> onComplete() },
                onSuccess = { _, _ -> onComplete() },
            )
            applyTransformation(transformation)
        }

        return CoilDisposable(disposable)
    }

    private fun ImageRequest.Builder.applyTransformation(
        transformation: ErmisImageLoader.ImageTransformation,
    ): ImageRequest.Builder =
        when (transformation) {
            is ErmisImageLoader.ImageTransformation.None -> this
            is ErmisImageLoader.ImageTransformation.Circle -> transformations(
                CircleCropTransformation(),
            )
            is ErmisImageLoader.ImageTransformation.RoundedCorners -> transformations(
                RoundedCornersTransformation(
                    transformation.radius,
                ),
            )
        }
}
