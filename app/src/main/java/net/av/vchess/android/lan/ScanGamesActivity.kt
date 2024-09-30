package net.av.vchess.android.lan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.av.vchess.R
import net.av.vchess.network.GameSearcher
import net.av.vchess.network.data.GameInformerData
import kotlin.concurrent.thread

class ScanGamesActivity : ComponentActivity() {
    lateinit var resultList: ViewGroup
    lateinit var scanningInProcessLabel: TextView

    private lateinit var gameSearcher: GameSearcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan_game_activity)
        actionBar?.hide()

        val startScanningButton = findViewById<Button>(R.id.start_scanning_button)
        scanningInProcessLabel = findViewById(R.id.scanning_in_process_label)
        resultList = findViewById(R.id.result_list)


        gameSearcher = GameSearcher(object : GameSearcher.ResultCollector {
            override fun onSearchStarted() {
                runOnUiThread {
                    resultList.removeAllViews()
                    scanningInProcessLabel.visibility = View.VISIBLE
                    startScanningButton.isEnabled = false
                }
            }

            override fun onResultFound(data: GameInformerData) {
                runOnUiThread {
                    val gameOffer = GameOfferView(this@ScanGamesActivity, data)
                    resultList.addView(gameOffer)
                    gameOffer.setOnClickListener {
                        val intent = Intent(this@ScanGamesActivity, JoinGameActivity::class.java)
                        val ipAddress = data.ipAddress
                        intent.putExtra(JoinGameActivity.IP_ADDRESS_KEY, ipAddress)
                        startActivity(intent)
                    }
                }
            }

            override fun onFinish() {
                runOnUiThread {
                    scanningInProcessLabel.visibility = View.INVISIBLE
                    startScanningButton.isEnabled = true
                }
            }
        })

        startScanningButton.setOnClickListener {
            restartScan()
        }

        thread {
            while (!isFinishing && !isDestroyed) {
                for (i in 0..3) {
                    var label: String = getString(R.string.scanning)
                    for (p in 1..i) {
                        label += "."
                    }
                    Thread.sleep(600)
                    runBlocking {
                        withContext(Dispatchers.Main) {
                            scanningInProcessLabel.text = label
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        restartScan()
    }

    private fun restartScan() {
        gameSearcher.scanForGames()
    }
}