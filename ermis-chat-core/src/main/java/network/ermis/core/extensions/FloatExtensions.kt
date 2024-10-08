package network.ermis.core.extensions

public fun Float.limitTo(min: Float, max: Float): Float {
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}

public fun Float.isInt(): Boolean {
    val diff = this - toInt()
    return diff <= 0
}
