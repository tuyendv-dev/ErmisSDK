package network.ermis.sample.util.extensions

import java.text.Normalizer

fun CharSequence.unaccent(): String {
    val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return REGEX_UNACCENT.replace(temp, "").replace("đ","d").replace("Đ", "D")
}
fun String.containingSpecialCharacters(): Boolean {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return this != temp
}