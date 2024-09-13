package network.ermis.client.api.endpoint

import network.ermis.client.api.AnonymousApi
import network.ermis.client.call.RetrofitCall
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * An interface used to download files directly.
 */
@AnonymousApi
internal interface FileDownloadApi {

    /**
     * A method that downloads the file and does not
     * convert the body.
     *
     * @param fileUrl The url that contains the file we are downloading.
     *
     * @return Returns an unconverted body inside of a [ResponseBody] wrapped
     * in a [RetrofitCall].
     */
    @Streaming
    @GET
    fun downloadFile(@Url fileUrl: String): RetrofitCall<ResponseBody>
}
