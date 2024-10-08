package network.ermis.client.plugin.requests

/**
 * Interface for analyse requests of the SDK. It can be use to understand how the SDK is calling the backend API.
 */
public interface ApiRequestsAnalyser {

    /**
     * Registers the request using the name as an ID.
     *
     * @param name Name of the request.
     * @param data All the data that should be included in the analyser about the request.
     */
    public fun registerRequest(requestName: String, data: Map<String, String>)

    /**
     * Gets the information about the request by name
     *
     * @param requestName The name of the request.
     */
    public fun dumpRequestByName(requestName: String): String

    /**
     * Gets all the information in the analyser.
     */
    public fun dumpAll(): String

    /**
     * Clears all the information of the analyser. Should be used to avoid batches of data that are too big
     * to understand.
     */
    public fun clearAll()

    /**
     * Clear an specific requests containing some string in its name. Return -1 it the request is not found.
     */
    public fun clearRequestContaining(queryText: String)

    /**
     * Returns the number of times that a requests was made using name.
     */
    public fun countRequestContaining(requestName: String): Int

    /**
     * Counts all the requests made.
     */
    public fun countAllRequests(): Int

    public companion object {
        private var instance: ApiRequestsAnalyser? = null

        /**
         * Gets the default implementation of [ApiRequestsAnalyser] or creates a new one it is not already
         * initialized.
         */
        public fun get(): ApiRequestsAnalyser = instance ?: ApiRequestsDumper().also { dumper ->
            instance = dumper
        }

        /**
         * Checks if the [ApiRequestsAnalyser] is initialized.
         */
        public fun isInitialized(): Boolean = instance != null
    }
}
