package net.av.vchess.android.ui.layout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import net.av.vchess.R

class HostGameDialog : ComponentActivity() {
    lateinit var gameNameInput: EditText
    lateinit var publishGameButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.host_game_dialog_activity)

        publishGameButton = findViewById(R.id.inform_game_button)
        gameNameInput = findViewById(R.id.game_name_input)

        publishGameButton.setOnClickListener {
            if (validateParams()) {
                val intent = Intent(this, HostGameActivity::class.java)
                intent.putExtra(HostGameActivity.GAME_NAME_KEY, gameNameInput.text.toString())
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