package cstjean.mobile.tp_fedex

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Classe ServiceMessagerieFirebase
 *
 * @author Sébastien Fortier
 * @author Ethan Leduc
 */
class ServiceMessagerieFirebase : FirebaseMessagingService() {

    /**
     * Fonction qui s'éxécute lors de la création d'un nouveau token
     *
     * @param token Le nouveau token
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FirebaseMessagingService", "Refreshed token: $token")
        // TODO Envoyer au backend
    }

    /**
     * Fonction qui s'éxécute lorsqu'on reçoit le message
     *
     * @param message Le message
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FirebaseMessagingService", "Message reçue: ${message.notification?.title.toString()}")
    }
}