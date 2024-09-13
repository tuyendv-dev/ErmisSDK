package network.ermis.ui.common.images

import android.content.Context
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient

private const val DEFAULT_MEMORY_PERCENTAGE = 0.25
private const val DEFAULT_DISK_CACHE_PERCENTAGE = 0.02
private const val DISK_CACHE_DIRECTORY = "image_cache"

public class ErmisImageLoaderFactory(
    private val context: Context,
    private val builder: ImageLoader.Builder.() -> Unit = {},
) : ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache { MemoryCache.Builder(context).maxSizePercent(DEFAULT_MEMORY_PERCENTAGE).build() }
            .allowHardware(false)
            .crossfade(true)
            .okHttpClient {
                val cacheControlInterceptor = Interceptor { chain ->
                    chain.proceed(chain.request())
                        .newBuilder()
                        .header("Cache-Control", "max-age=3600,public")
                        .build()
                }
                // Don't limit concurrent network requests by host.
                val dispatcher = Dispatcher().apply { maxRequestsPerHost = maxRequests }

                OkHttpClient.Builder()
                    .dispatcher(dispatcher)
                    .addNetworkInterceptor(cacheControlInterceptor)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve(DISK_CACHE_DIRECTORY))
                    .maxSizePercent(DEFAULT_DISK_CACHE_PERCENTAGE)
                    .build()
            }
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory(enforceMinimumFrameDelay = true))
                } else {
                    add(GifDecoder.Factory(enforceMinimumFrameDelay = true))
                }
                add(VideoFrameDecoder.Factory())
            }
            .apply(builder)
            .build()
    }
}