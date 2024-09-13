package network.ermis.client.utils

public object TimeProvider {
    private const val MILLIS_TO_SECONDS_FACTOR = 1_000L
    public fun provideCurrentTimeInSeconds(): Long = System.currentTimeMillis() / MILLIS_TO_SECONDS_FACTOR
    public fun provideCurrentTimeInMilliseconds(): Long = System.currentTimeMillis()
}
