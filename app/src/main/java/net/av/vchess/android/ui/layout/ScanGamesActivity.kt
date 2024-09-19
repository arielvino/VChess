package net.av.vchess.android.ui.layout

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import net.av.vchess.R
import net.av.vchess.network.ClientConnector
import net.av.vchess.network.Encryptor
import net.av.vchess.network.GameSearcher
import net.av.vchess.network.IConnector
import kotlin.concurrent.thread

class ScanGamesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan_game_activity)

        val startScanningButton = findViewById<Button>(R.id.start_scanning_button)
        val scanningInProcessLabel = findViewById<TextView>(R.id.scanning_in_process_label)
        val resultList = findViewById<ViewGroup>(R.id.result_display)

        val gameSearcher = GameSearcher(object : GameSearcher.ResultCollector {
            override fun onResultFound(fakeId: String) {
                runOnUiThread {
                    val gameOffer = Button(this@ScanGamesActivity)
                    gameOffer.text = fakeId
                    resultList.addView(gameOffer)
                    gameOffer.setOnClickListener {
                        thread {
                            val connector = ClientConnector(
                                listener = object : IConnector.IListener {
                                    override fun onConnect(clientAlias: String) {
                                        runOnUiThread {
                                            Toast.makeText(
                                                this@ScanGamesActivity,
                                                "Connected!",
                                                Toast.LENGTH_LONG
                                            ).show()

                                        }
                                    }
                                },
                                ipAddress = fakeId.substring(fakeId.lastIndexOf(" ") + 1),
                                "fabricated name"
                            )
                            while (!connector.isConnected());
                            val encryptor = Encryptor(connector)
                            runOnUiThread {
                                Toast.makeText(
                                    this@ScanGamesActivity,
                                    encryptor.receive(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            encryptor.send("I'm the client.")
                        }
                    }
                }
            }

            override fun onFinish() {
                runOnUiThread {
                    scanningInProcessLabel.visibility = View.GONE
                    startScanningButton.isEnabled = true
                }
            }
        })

        startScanningButton.setOnClickListener {
            resultList.removeAllViews()
            scanningInProcessLabel.visibility = View.VISIBLE
            startScanningButton.isEnabled = false
            gameSearcher.scanForGames()
        }

        startScanningButton.performClick()
    }
}