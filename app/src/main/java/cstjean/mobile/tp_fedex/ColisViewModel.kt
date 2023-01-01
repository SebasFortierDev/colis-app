package cstjean.mobile.tp_fedex

import android.app.Application
import androidx.lifecycle.*

/**
 * Classe ColisViewModel
 *
 * @property colisLiveData Le livedata contenant la liste de colis
 * @property colisFetchr Le colisFetchr fesant les requêtes à l'Api
 * @property mutableSearchTerm Le livedata contenant le filtre de recherche
 * @property searchTerm Le filtre de recherche
 */
class ColisViewModel(private val app: Application) : AndroidViewModel(app) {
    val colisLiveData: LiveData<List<Colis>>

    private val colisFetchr = ColisFetchr()

    private val mutableSearchTerm = MutableLiveData<String>()
    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""

    init {
        mutableSearchTerm.value =  QueryPreferences.getStoredQuery(app)

        colisLiveData = Transformations.switchMap(mutableSearchTerm) { searchTerm ->
            if (searchTerm.isBlank()) {
                colisFetchr.fetchColis()
            } else {
                colisFetchr.fetchRecherche(searchTerm)
            }
        }
    }

    /**
     * Permet d'obtenir la liste de colis avec filtre ou non
     *
     * @param query Le filtre
     */
    fun fetchColis(query: String = "") {
        QueryPreferences.setStoredQuery(app, query)

        mutableSearchTerm.value = query
    }

    /**
     * Permet de récupérer un colis
     *
     * @param id L'id du colis qu'on veut récupérer
     */
    fun patchColis(id: String) : Boolean {
        return colisFetchr.patchColis(id)
    }
}