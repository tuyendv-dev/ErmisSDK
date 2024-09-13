package network.ermis.client.api

import network.ermis.client.call.RetrofitCall
import network.ermis.client.parser.ChatParser
import io.getstream.result.call.Call
import kotlinx.coroutines.CoroutineScope
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class RetrofitCallAdapterFactory private constructor(
    private val chatParser: ChatParser,
    private val coroutineScope: CoroutineScope,
) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != RetrofitCall::class.java) {
            return null
        }
        if (returnType !is ParameterizedType) {
            throw IllegalArgumentException("Call return type must be parameterized as Call<Foo>")
        }
        val responseType: Type = getParameterUpperBound(0, returnType)
        return RetrofitCallAdapter<Any>(responseType, chatParser, coroutineScope)
    }

    companion object {
        fun create(
            chatParser: ChatParser,
            coroutineScope: CoroutineScope,
        ): RetrofitCallAdapterFactory = RetrofitCallAdapterFactory(chatParser, coroutineScope)
    }
}

internal class RetrofitCallAdapter<T : Any>(
    private val responseType: Type,
    private val parser: ChatParser,
    private val coroutineScope: CoroutineScope,
) : CallAdapter<T, Call<T>> {
    override fun responseType(): Type = responseType
    override fun adapt(call: retrofit2.Call<T>): Call<T> {
        return RetrofitCall(call, parser, coroutineScope)
    }
}
