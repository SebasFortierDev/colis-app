package cstjean.mobile.tp_fedex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

/**
 * Activité principale de l'application
 *
 * @property boutonConnexion Le bouton permettant de se connecter
 * @property swipeRefreshLayout Le swipeRefreshLayout permettant de rafraichir les colis
 * @property colisViewModel Le viewModel des colis
 * @property connexionEstCharge Permet de savoir si le menu est chargé
 * @property colisRecyclerView Le recyclerView affichant la liste de colis
 * @property adapter L'adapter du recyclerView de colis
 * @property signInLauncher Le launcher de l'écran de connexion
 * @property providers Les services de connection supporté
 * @property signInIntent Le intent pour l'écran de connexion
 */
class MainActivity : AppCompatActivity() {
    private lateinit var boutonConnexion: MenuItem
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val colisViewModel: ColisViewModel by viewModels()

    private var connexionEstCharge: Boolean = false

    private lateinit var colisRecyclerView: RecyclerView
    private var adapter: ColisAdapter? = ColisAdapter(emptyList())

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build())

    private val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setIsSmartLockEnabled(false)
        .setAvailableProviders(providers)
        .build()

    /**
     * Initialisation de l'Activity.
     *
     * @param savedInstanceState Les données conservées au changement d'état.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        colisRecyclerView = findViewById(R.id.colis_recycler_view)
        colisRecyclerView.layoutManager = LinearLayoutManager(this)
        colisRecyclerView.adapter = adapter

        swipeRefreshLayout = findViewById(R.id.swipe_container)

        updateUser()

        colisViewModel.colisLiveData.observe(this,
            { colisResponse ->
                adapter = ColisAdapter(colisResponse)
                colisRecyclerView.adapter = adapter
        })

        swipeRefreshLayout.setOnRefreshListener {
            colisViewModel.fetchColis(colisViewModel.searchTerm)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    /**
     * Colis Holder
     *
     * @param view La vue du holder
     *
     * @property colis Le colis associé à la vue
     * @property idColis Le texte affichant l'id du colis
     * @property adresse Le texte affichant l'adresse de destination du colis
     * @property poids Le texte affichant le poids du colis
     * @property btnRecupere Le bouton permettant de récupérer un colis
     */
    private inner class ColisHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var colis: Colis
        val idColis: TextView = itemView.findViewById(R.id.colis_id)
        val adresse: TextView = itemView.findViewById(R.id.colis_adresse)
        val poids: TextView = itemView.findViewById(R.id.colis_poids)
        val btnRecupere: Button = itemView.findViewById(R.id.btn_recupere)

        init {
            itemView.setOnClickListener(this)
        }

        /**
         * Permet de lier des éléments du colis à la vue
         *
         * @param colis Le colis à associer au ViewHolder.
         */
        fun bind(colis: Colis) {
            this.colis = colis
            idColis.text = getString(R.string.label_id, colis.id)
            adresse.text = getString(R.string.label_adresse, colis.adresse)
            poids.text = getString(R.string.label_poids, colis.poids.toString())
            btnRecupere.text = getString(R.string.recupere)

            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                btnRecupere.visibility = View.GONE
            }

            btnRecupere.setOnClickListener {
                colisViewModel.patchColis(colis.id)
                btnRecupere.isEnabled = false
                btnRecupere.text = getString(R.string.texte_est_recupere)
            }
        }

        /**
         * Permet de faire des actions lorsqu'on clique sur la vue du colis
         *
         * @param v La view du colis
         */
        override fun onClick(v: View?) {
            // Rien à faire pour l'instant
        }
    }

    /**
     * Classe ColisAdapter
     *
     * @property listeColis La liste des colis pour l'Adapter du RecyclerView.
     */
    private inner class ColisAdapter(var listeColis: List<Colis>) : RecyclerView.Adapter<ColisHolder>() {

        /**
         * Création du ViewHolder
         *
         * @param parent Le parent où ajouter notre ViewHolder.
         * @param viewType Le type de la nouvelle vue.
         *
         * @return Le ViewHolder instancié.
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColisHolder {
            val view = layoutInflater.inflate(R.layout.list_item_colis, parent, false)
            return ColisHolder(view)
        }

        /**
         * Bind sur le ViewHolder selon la position
         *
         * @param holder Le ViewHolder qui est bindé.
         * @param position La position à charger.
         */
        override fun onBindViewHolder(holder: ColisHolder, position: Int) {
            val colis: Colis = listeColis[position]
            holder.bind(colis)
        }

        /**
         * La quantité de données dans le RecyclerView.
         */
        override fun getItemCount(): Int = listeColis.size
    }

    /**
     * Permet de gérer les actions lors de la création du menu
     *
     * @param menu Le menu
     *
     * @return True lorsque la création est terminé
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.activity_main, menu)

        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView

        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(queryText: String): Boolean {

                    colisViewModel.fetchColis(queryText)
                    return true
                }
                override fun onQueryTextChange(queryText: String): Boolean {
                    return false
                }
            })

            setOnSearchClickListener {
                searchView.setQuery(colisViewModel.searchTerm, false)
            }
        }

        boutonConnexion = menu.findItem(R.id.btn_connecter)
        connexionEstCharge = true
        updateUser()

        return true
    }

    /**
     * Permet de gérer les clicks sur les options du menu
     *
     * @param item Les option du menu
     *
     * @return True quand l'opération est terminé
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btn_connecter -> {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    deconnexion()
                }
                else {
                    signInLauncher.launch(signInIntent)
                }
                true
            }
            R.id.menu_item_clear -> {
                colisViewModel.fetchColis("")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Permet de mettre à jour l'interface selon si l'utilisateur est connecté ou non
     */
    private fun updateUser() {
        val user = FirebaseAuth.getInstance().currentUser
        var texteMenu = getString(R.string.texte_bouton_connexion)
        var imageBouton = R.drawable.ic_baseline_login_24


        if (user != null) {
            texteMenu = getString(R.string.texte_bouton_deconnexion)
            imageBouton = R.drawable.ic_baseline_logout_24
        }
        colisRecyclerView.adapter = adapter

        if (connexionEstCharge) {
            boutonConnexion.title = texteMenu
            boutonConnexion.setIcon(imageBouton)
        }
    }

    /**
     * Permet de se déconnecter de l'application
     */
    private fun deconnexion() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                updateUser()
            }
    }

    /**
     * Action à effectuer lorsque la connexion réussi ou non
     *
     * @param result Le résultat de l'opération
     */
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Correctement connecté
            updateUser()
        } else {
            // Erreur, à gérer dans un contexte professionnel
            updateUser()
        }
    }
}