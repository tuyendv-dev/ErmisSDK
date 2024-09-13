package network.ermis.client.plugin.listeners

import network.ermis.core.models.Message
import io.getstream.result.Result

public interface ShuffleGiphyListener {

    /**
     * A method called after receiving the response from the shuffle Giphy call.
     *
     * @param cid The full channel id, i.e. "messaging:123".
     * @param result The API call result.
     */
    public suspend fun onShuffleGiphyResult(
        cid: String,
        result: Result<Message>,
    )
}
