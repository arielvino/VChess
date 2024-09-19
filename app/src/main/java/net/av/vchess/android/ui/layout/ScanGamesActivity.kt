package net.av.vchess.android.ui.layout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import net.av.vchess.R
import net.av.vchess.network.GameSearcher

class ScanGamesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan_game_activity)

        val startScanningButton = findViewById<Button>(R.id.start_scanning_button)
        val scanningInProcessLabel = findViewById<TextView>(R.id.scanning_in_process_label)
        val resultList = findViewById<ViewGroup>(R.id.result_display)

        var gameSearcher:GameSearcher? =null
        gameSearcher = GameSearcher(object : GameSearcher.ResultCollector {
            override fun onResultFound(fakeId: String) {
                runOnUiThread {
                    val gameOffer = Button(this@ScanGamesActivity)
                    gameOffer.text = fakeId
                    resultList.addView(gameOffer)
                    gameOffer.setOnClickListener {
                        gameSearcher!!.stopScan()
                        val intent = Intent(this@ScanGamesActivity, JoinGameActivity::class.java)
                        val ipAddress = fakeId.substring(fakeId.lastIndexOf(" ") + 1)
                        intent.putExtra(JoinGameActivity.IP_ADDRESS_KEY, ipAddress)
                        startActivity(intent)
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