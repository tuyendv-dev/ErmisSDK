package network.ermis.ui.common.feature.messages.composer.transliteration

import android.icu.text.Transliterator
import android.os.Build
import androidx.annotation.RequiresApi
import io.getstream.log.taggedLogger

/**
 * Default implementation for [StreamTransliterator]. This class uses the native transliteration provided by Android.
 * Requires Android Q or higher.
 */
public class DefaultStreamTransliterator(transliterationId: String? = null) : StreamTransliterator {

    private val logger by taggedLogger("Chat:Transliterator")

    private var transliterator: Transliterator? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            transliterationId?.let(::setTransliterator)
        } else {
            logger.d {
                "This android version: ${Build.VERSION.SDK_INT} doesn't support transliteration natively. " +
                    "User a custom StreamTransliterator to add transliteration."
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setTransliterator(id: String) {
        if (Transliterator.getAvailableIDs().asSequence().contains(id)) {
            this.transliterator = Transliterator.getInstance(id)
        } else {
            logger.d { "The id: $id for transliteration is not available" }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun transliterate(text: String): String {
        return try {
            transliterator?.transliterate(text)?.also {
                logger.v { "[transliterate] input: $text, output: $it" }
            } ?: text
        } catch (e: Exception) {
            logger.e(e) { "[transliterate] failed($text): $e" }
            text
        }
    }
}
