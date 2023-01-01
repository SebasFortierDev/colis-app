package cstjean.mobile.tp_fedex.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface de l'Api de colis
 */
interface ColisApi {

    /**
     * Routes qui permet d'obtenir la liste des colis
     *
     * @return Un colis response contenant les informations des colis
     */
    @GET("/colis")
    fun fetchColis(): Call<ColisResponse>

    /**
     * Routes qui permet d'obtenir la liste des colis avec un filtre selon l'id
     *
     * @return Un colis response contenant les informations des colis
     */
    @GET("/colis")
    fun fetchRecherche(@Query("s") query: String): Call<ColisResponse>

    /**
     * Routes qui permet de changer l'état du colis pour 'récupérer'
     *
     * @return Un colis response contenant les informations des colis
     */
    @PATCH("/colis/{id}/recuperer")
    fun patchColis(@Path("id") id: String): Call<PatchResponse>
}