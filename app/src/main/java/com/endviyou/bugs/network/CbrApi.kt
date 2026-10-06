package com.endviyou.bugs.network

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API ЦБ РФ для получения курса металлов
 */
interface CbrApi {

    @GET("scripts/xml_metall.asp")
    suspend fun getMetals(
        @Query("date_req1") dateFrom: String,
        @Query("date_req2") dateTo: String
    ): CbrResponse
}