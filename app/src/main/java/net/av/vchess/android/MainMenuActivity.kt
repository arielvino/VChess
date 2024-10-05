package net.av.vchess.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.av.vchess.android.compose.MainActivity
import net.av.vchess.android.lan.PlayLanActivity
import net.av.vchess.android.ui.layout.EditorActivity
import net.av.vchess.android.compose.theme.VChessTheme

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        enableEdgeToEdge()
        setContent {
            VChessTheme {
                MainMenuScreen(
                    onComposableAppClicked = {
                        val intent = Intent(this@MainMenuActivity, MainActivity::class.java)
                        startActivity(intent)
                    },
                    onEditorClicked = {
                        val intent = Intent(this@MainMenuActivity, EditorActivity::class.java)
                        startActivity(intent)
                    },
                    onPlayLanClicked = {
                        val intent = Intent(this@MainMenuActivity, PlayLanActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun MainMenuScreen(
    onComposableAppClicked: () -> Unit,
    onEditorClicked: () -> Unit,
    onPlayLanClicked: () -> Unit
) {
    VChessTheme {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Main Menu",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp),
                color = MaterialTheme.colorScheme.onBackground
            )


            Button(
                onClick = onComposableAppClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Composable app (experimental)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onEditorClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Editor")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPlayLanClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Play LAN")
            }
        }
    }
}