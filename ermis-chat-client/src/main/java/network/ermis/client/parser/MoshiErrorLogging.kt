package network.ermis.client.parser

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.reflect.Type

internal fun MoshiConverterFactory.withErrorLogging(): Converter.Factory {
    val originalFactory = this

    return object : Converter.Factory() {
        override fun responseBodyConverter(
            type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit,
        ): Converter<ResponseBody, *> {
            val originalConverter: Converter<ResponseBody, *> =
                originalFactory.responseBodyConverter(type, annotations, retrofit)!!
            return Converter { value -> originalConverter.convert(value) }
        }

        override fun requestBodyConverter(
            type: Type,
            parameterAnnotations: Array<out Annotation>,
            methodAnnotations: Array<out Annotation>,
            retrofit: Retrofit,
        ): Converter<*, RequestBody>? {
            return originalFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
        }

        override fun stringConverter(
            type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit,
        ): Converter<*, String>? {
            return originalFactory.stringConverter(type, annotations, retrofit)
        }
    }
}
