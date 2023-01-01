package cstjean.mobile.tp_fedex.api

import com.google.gson.annotations.SerializedName
import cstjean.mobile.tp_fedex.Colis

/**
 * Classe ColisResponse
 *
 * @property listeColis La liste de [Colis]
 */
class ColisResponse {
    @SerializedName("data")
    lateinit var listeColis: List<Colis>
}