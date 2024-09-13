package network.ermis.client.utils

import android.util.Base64
import io.getstream.log.taggedLogger
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.Calendar

internal object TokenUtils {

    val logger by taggedLogger("Chat:TokenUtils")

    fun getUserId(token: String): String = try {
        JSONObject(
            token
                .takeIf { it.contains(".") }
                ?.split(".")
                ?.getOrNull(1)
                ?.let { String(Base64.decode(it.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)) }
                ?: "",
        ).optString("user_id")
    } catch (e: JSONException) {
        logger.e(e) { "Unable to obtain userId from JWT Token Payload" }
        ""
    } catch (e: IllegalArgumentException) {
        logger.e(e) { "Unable to obtain userId from JWT Token Payload" }
        ""
    }

    fun getTokenExpired(token: String): Boolean = try {
        val exp = JSONObject(
            token
                .takeIf { it.contains(".") }
                ?.split(".")
                ?.getOrNull(1)
                ?.let { String(Base64.decode(it.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)) }
                ?: "",
        ).optLong("exp")
        exp < Calendar.getInstance().timeInMillis
    } catch (e: JSONException) {
        logger.e(e) { "Unable to obtain userId from JWT Token Payload" }
        true
    } catch (e: IllegalArgumentException) {
        logger.e(e) { "Unable to obtain userId from JWT Token Payload" }
        true
    }

    /**
     * Generate a developer token that can be used to connect users while the app is using a development environment.
     *
     * @param userId the desired id of the user to be connected.
     */
    fun devToken(userId: String): String {
        require(userId.isNotEmpty()) { "User id must not be empty" }
        val header = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" //  {"alg": "HS256", "typ": "JWT"}
        val devSignature = "devtoken"
        val payload: String =
            Base64.encodeToString("{\"user_id\":\"$userId\"}".toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
        return "$header.$payload.$devSignature"
    }
}
