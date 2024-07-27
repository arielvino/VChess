package net.av.vchess.android.ui.layout

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import net.av.vchess.R
import net.av.vchess.network.GamePendingInformer

class HostGameActivity : ComponentActivity() {
    companion object {
        val GAME_NAME_KEY = "net.av.vchess.GAME_NAME"
    }

    lateinit var gameNameLabel:TextView
    lateinit var waitingLabel:TextView
    lateinit var connectedLabel:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.host_game_activity)

        gameNameLabel = findViewById(R.id.game_name_label)
        waitingLabel = findViewById(R.id.waiting_label)
        connectedLabel = findViewById(R.id.connected_label)
        val name:String = intent.extras!!.getString(GAME_NAME_KEY)!!
        gameNameLabel.setText(name)

        val informer = GamePendingInformer(name)
        informer.start()

        waitingLabel.visibility = View.VISIBLE

        //todo:
    }
}