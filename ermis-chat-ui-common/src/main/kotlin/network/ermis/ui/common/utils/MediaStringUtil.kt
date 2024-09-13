package network.ermis.ui.common.utils

import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow

private const val SECONDS_IN_A_HOUR = 3600
private const val SECONDS_IN_A_MINUTE = 60
private const val BYTE_UNIT_CONVERSION_FACTOR = 1024

public object MediaStringUtil {

    @JvmStatic
    public fun convertVideoLength(videoLengthInSeconds: Long, locale: Locale = Locale.getDefault()): String {
        return if (videoLengthInSeconds <= 0) {
            "--:--:--"
        } else {
            val hours = videoLengthInSeconds / SECONDS_IN_A_HOUR
            val minutes = videoLengthInSeconds % SECONDS_IN_A_HOUR / SECONDS_IN_A_MINUTE
            val seconds = videoLengthInSeconds % SECONDS_IN_A_MINUTE
            String.format(locale, "%02d:%02d:%02d", hours, minutes, seconds)
        }
    }

    @JvmStatic
    public fun convertFileSizeByteCount(bytes: Long): String {
        return when {
            bytes <= 0 -> "0 B"
            bytes < BYTE_UNIT_CONVERSION_FACTOR -> "$bytes B"
            else -> {
                val exp = (ln(bytes.toDouble()) / ln(BYTE_UNIT_CONVERSION_FACTOR.toDouble())).toInt()
                val pre = "KMGTPE"[exp - 1].toString()
                val df = DecimalFormat("###.##")
                df.format(bytes / BYTE_UNIT_CONVERSION_FACTOR.toDouble().pow(exp.toDouble())) + " ${pre}B"
            }
        }
    }
}
