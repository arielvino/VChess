package net.av.vchess.android.ui.layout

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import net.av.vchess.R
import net.av.vchess.network.Encryptor
import net.av.vchess.network.GamePendingInformer
import net.av.vchess.network.HostConnector
import net.av.vchess.network.IConnector
import kotlin.concurrent.thread

class HostGameActivity : ComponentActivity() {
    companion object {
        val GAME_NAME_KEY = "net.av.vchess.GAME_NAME"
    }

    lateinit var gameNameLabel: TextView
    lateinit var waitingLabel: TextView
    lateinit var connectedLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.host_game_activity)

        gameNameLabel = findViewById(R.id.game_name_label)
        waitingLabel = findViewById(R.id.waiting_label)
        connectedLabel = findViewById(R.id.connected_label)

        val name: String = intent.extras!!.getString(GAME_NAME_KEY)!!
        gameNameLabel.text = name

        val informer = GamePendingInformer(name)
        informer.start()

        waitingLabel.visibility = View.VISIBLE

        //todo:
        thread {
            val connector = HostConnector(object : IConnector.IListener {
                override fun onConnect(clientAlias: String) {
                    runOnUiThread {
                        informer.stop()
                        Toast.makeText(this@HostGameActivity, clientAlias, Toast.LENGTH_LONG).show()
                    }
                }
            })
            while (!connector.isConnected());
            val encryptor = Encryptor(connector)
            encryptor.send("I'm the host.")
            runOnUiThread {
                waitingLabel.text = encryptor.receive()
            }
        }


    }
}