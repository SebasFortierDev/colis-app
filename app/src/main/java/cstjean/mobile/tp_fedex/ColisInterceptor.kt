package cstjean.mobile.tp_fedex

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * L'API KEY de l'Api
 */
private const val API_KEY = ""

/**
 * Classe ColisInterceptor
 */
class ColisInterceptor : Interceptor {
    /**
     * Permet de créer la base de l'Api
     *
     * @return La base de l'Api
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val newUrl: HttpUrl = originalRequest.url().newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .build()
        val newRequest: Request = originalRequest.newBuilder()
            .url(newUrl)
            .build()
        return chain.proceed(newRequest)
    }
}