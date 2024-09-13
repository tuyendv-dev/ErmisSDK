package network.ermis.core.models

public sealed interface CustomObject {
    public val extraData: Map<String, Any>

    @Suppress("UNCHECKED_CAST")
    public fun <T> getExtraValue(key: String, default: T): T {
        return if (extraData.containsKey(key)) {
            extraData[key] as T
        } else {
            default
        }
    }
}
