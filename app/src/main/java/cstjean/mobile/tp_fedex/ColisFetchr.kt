package cstjean.mobile.tp_fedex

import android.util.Log
import androidx.lifecycle.MutableLiveData
import cstjean.mobile.tp_fedex.api.ColisApi
import cstjean.mobile.tp_fedex.api.ColisResponse
import cstjean.mobile.tp_fedex.api.PatchResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Classe ColisFetch
 *
 * @property colisApi Voir [ColisApi]
 */
class ColisFetchr {
    private val colisApi: ColisApi

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor(ColisInterceptor())
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://test.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        colisApi = retrofit.create(ColisApi::class.java)
    }

    /**
     * Permet d'obtenir la liste de tous les colis
     *
     * @return La liste des colis
     */
    fun fetchColis(): MutableLiveData<List<Colis>> {
        return fetchMetadata(colisApi.fetchColis())
    }

    /**
     * Permet d'obtenir la liste des colis filtrés
     *
     * @param query Le filtre
     *
     * @return La liste des colis filtrés
     */
    fun fetchRecherche(query: String): MutableLiveData<List<Colis>> {
        return fetchMetadata(colisApi.fetchRecherche(query))
    }

    /**
     * Permet de faire une requête qui retourne des metadata
     *
     * @param colisRequest La requête à faire
     *
     * @return La réponse de la requête en livedata
     */
    private fun fetchMetadata(colisRequest: Call<ColisResponse>) : MutableLiveData<List<Colis>> {
        val responseLiveData: MutableLiveData<List<Colis>> = MutableLiveData()
        colisRequest.enqueue(object : Callback<ColisResponse> {
            override fun onResponse(call: Call<ColisResponse>, response: Response<ColisResponse>) {

                val colisResponse: ColisResponse? = response.body()

                if (colisResponse != null) {
                    responseLiveData.value = colisResponse.listeColis
                }
            }
            override fun onFailure(call: Call<ColisResponse>, t: Throwable) {
                Log.d("API Colis", "Une erreur est survenue lors de La requête à l'API")
            }
        })
        return responseLiveData
    }

    /**
     * Permet de faire une requête patch pour récupérer un colis
     *
     * @param id L'id du colis qu'on veut récupérer
     *
     * @return Vrai si la requête à fonctionné
     */
     fun patchColis(id: String) : Boolean {
        var aRecupere = false
        colisApi.patchColis(id).enqueue(object : Callback<PatchResponse> {
            override fun onResponse(call: Call<PatchResponse>, response: Response<PatchResponse>) {
                aRecupere = true
            }
            override fun onFailure(call: Call<PatchResponse>, t: Throwable) {
                // Erreur, à gérer dans un contexte professionnel
                Log.d("API Colis", "Une erreur est survenue lors de La requête à l'API")
            }
        })
        return aRecupere
    }
}