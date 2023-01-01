package cstjean.mobile.tp_fedex

/**
 * Classe Colis
 *
 * @property id L'id du colis
 * @property adresse L'adresse du colis
 * @property poids Le poids du colis
 */
data class Colis(
    var id: String,
    var adresse: String,
    var poids: Double,
)