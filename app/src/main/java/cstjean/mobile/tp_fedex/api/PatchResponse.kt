package cstjean.mobile.tp_fedex.api

import com.google.gson.annotations.SerializedName
import cstjean.mobile.tp_fedex.Colis

/**
 * Classe PatchResponse
 *
 * @property colis Voir [Colis]
 */
class PatchResponse {
    @SerializedName("data")
    lateinit var colis: Colis
}