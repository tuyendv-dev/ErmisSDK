package network.ermis.ui.common.feature.messages.composer.transliteration

/**
 * Interface for transliteration of text.
 */
public interface StreamTransliterator {

    /**
     * Transliterate the given [text].
     *
     * @param text The text to transliterate.
     * @return The transliterated text.
     */
    public fun transliterate(text: String): String
}
