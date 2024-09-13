package network.ermis.client.plugin.listeners

import network.ermis.client.ErmisClient
import network.ermis.core.models.Message
import io.getstream.result.Result

/** Listener for reply queries. */
public interface ThreadQueryListener {
    /**
     * Runs precondition check for [ErmisClient.getReplies]. If it returns [Result.Success] then the request is run
     * otherwise it returns [Result.Failure] and no request is made.
     */
    public suspend fun onGetRepliesPrecondition(messageId: String, limit: Int): Result<Unit> =
        Result.Success(Unit)

    /** Runs side effect before the request is launched. */
    public suspend fun onGetRepliesRequest(messageId: String, limit: Int)

    /** Runs this function on the result of the [ErmisClient.getReplies] request. */
    public suspend fun onGetRepliesResult(result: Result<List<Message>>, messageId: String, limit: Int)

    /**
     * Runs precondition check for [ErmisClient.getRepliesMore]. If it returns [Result.Success] then the request is run
     * otherwise it returns [Result.Failure] and no request is made.
     */
    public suspend fun onGetRepliesMorePrecondition(
        messageId: String,
        firstId: String,
        limit: Int,
    ): Result<Unit> = Result.Success(Unit)

    /** Runs side effect before the request is launched. */
    public suspend fun onGetRepliesMoreRequest(
        messageId: String,
        firstId: String,
        limit: Int,
    )

    /** Runs this function on the result of the [ErmisClient.getRepliesMore] request. */
    public suspend fun onGetRepliesMoreResult(
        result: Result<List<Message>>,
        messageId: String,
        firstId: String,
        limit: Int,
    )
}
