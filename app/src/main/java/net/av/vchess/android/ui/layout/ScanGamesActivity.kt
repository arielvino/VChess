package net.av.vchess.android.ui.layout

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.children
import net.av.vchess.R
import net.av.vchess.network.GameSearcher

class ScanGamesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan_game_activity)
        val startScanningButton = findViewById<Button>(R.id.start_scanning_button)
        val scanningInProcessLabel = findViewById<TextView>(R.id.scanning_in_process_label)
        val resultList = findViewById<ViewGroup>(R.id.result_display)

        val gameSearcher = GameSearcher(object :GameSearcher.ResultCollector {
            override fun onResultFound(fakeId: String) {
                runOnUiThread{
                    val gameOffer = EditText(this@ScanGamesActivity)
                    gameOffer.setText(fakeId)
                    resultList.addView(gameOffer)
                }
            }

            override fun onFinish() {
                runOnUiThread {
                    scanningInProcessLabel.visibility = View.GONE
                }
            }
        })

        startScanningButton.setOnClickListener {
            resultList.removeAllViews()
            scanningInProcessLabel.visibility = View.VISIBLE
            gameSearcher.scanForGames()
        }
    }
}