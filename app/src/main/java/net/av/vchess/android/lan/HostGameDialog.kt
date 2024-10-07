package net.av.vchess.android.lan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import net.av.vchess.R
import net.av.vchess.network.data.GameInformerData

class HostGameDialog : ComponentActivity() {
    private lateinit var gameNameInput: EditText
    private lateinit var publishGameButton: Button
    private lateinit var blackButton: ImageButton
    private lateinit var whiteButton: ImageButton
    private lateinit var randomButton: ImageButton

    private var myColor: GameInformerData.MyColorSetting = GameInformerData.MyColorSetting.Random

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.host_game_dialog_activity)

        publishGameButton = findViewById(R.id.inform_game_button)
        gameNameInput = findViewById(R.id.game_name_input)
        whiteButton = findViewById(R.id.white_button)
        blackButton = findViewById(R.id.black_button)
        randomButton = findViewById(R.id.random_button)

        whiteButton.setOnClickListener {
            whiteButton.setBackgroundColor(getColor(R.color.purple_500))
            blackButton.setBackgroundColor(getColor(R.color.gray_600))
            randomButton.setBackgroundColor(getColor(R.color.gray_600))
            myColor = GameInformerData.MyColorSetting.White
        }

        blackButton.setOnClickListener {
            whiteButton.setBackgroundColor(getColor(R.color.gray_600))
            blackButton.setBackgroundColor(getColor(R.color.purple_500))
            randomButton.setBackgroundColor(getColor(R.color.gray_600))
            myColor = GameInformerData.MyColorSetting.Black
        }

        randomButton.setOnClickListener {
            whiteButton.setBackgroundColor(getColor(R.color.gray_600))
            blackButton.setBackgroundColor(getColor(R.color.gray_600))
            randomButton.setBackgroundColor(getColor(R.color.purple_500))
            myColor = GameInformerData.MyColorSetting.Random
        }

        publishGameButton.setOnClickListener {
            if (validateParams()) {
                val intent = Intent(this, HostGameActivity::class.java)
                intent.putExtra(HostGameActivity.LOBBY_NAME_KEY, gameNameInput.text.toString())
                intent.putExtra(HostGameActivity.MY_COLOR_KEY, myColor.name)
                startActivity(intent)
            }
        }
    }

    private fun isLegalName(): Boolean {
        return gameNameInput.text.isNotBlank()
    }

    private fun validateParams(): Boolean {
        return isLegalName()
    }
}