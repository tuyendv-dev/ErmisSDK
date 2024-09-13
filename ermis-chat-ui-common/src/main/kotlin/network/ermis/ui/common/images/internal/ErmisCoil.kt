package network.ermis.ui.common.images.internal

import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import network.ermis.ui.common.images.ErmisImageLoaderFactory

public object ErmisCoil {

    private var imageLoader: ImageLoader? = null
    private var imageLoaderFactory: ImageLoaderFactory? = null

    @Synchronized
    public fun setImageLoader(factory: ImageLoaderFactory) {
        imageLoaderFactory = factory
        imageLoader = null
    }

    internal fun imageLoader(context: Context): ImageLoader = imageLoader ?: newImageLoader(context)

    @Synchronized
    private fun newImageLoader(context: Context): ImageLoader {
        imageLoader?.let { return it }

        val imageLoaderFactory = imageLoaderFactory ?: newImageLoaderFactory(context)
        return imageLoaderFactory.newImageLoader().apply {
            imageLoader = this
        }
    }

    private fun newImageLoaderFactory(context: Context): ImageLoaderFactory {
        return ErmisImageLoaderFactory(context).apply {
            imageLoaderFactory = this
        }
    }

    internal inline val Context.streamImageLoader: ImageLoader
        get() = imageLoader(this)
}
