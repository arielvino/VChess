package net.av.vchess.android.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import net.av.vchess.R
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.av.vchess.android.compose.theme.VChessTheme
import net.av.vchess.network.data.GameInformerData
import net.av.vchess.network.data.NetworkGameSettings

@Composable
fun HostGameDialogScreen(onGameStarted: (NetworkGameSettings) -> Unit) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val lobbyName = rememberSaveable {
            mutableStateOf("My lobby 1")
        }
        val color = rememberSaveable {
            mutableStateOf(GameInformerData.MyColorSetting.Random)
        }

        Text(
            text = "Game settings",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge
        )
        Column(
            modifier = Modifier
                .padding(0.dp, 16.dp)
                .fillMaxWidth()
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(5.dp)
                )
                .padding(7.dp)
        ) {
            Text(
                text = "Lobby name",
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                maxLines = 1,
                value = lobbyName.value,
                onValueChange = { value -> lobbyName.value = value },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )
            Spacer(modifier = Modifier.height(10.dp))

            val determineButtonColorBySelectedColorSetting = @Composable { ownedColor: GameInformerData.MyColorSetting ->
                if (ownedColor == color.value) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RectangleShape,
                    onClick = { color.value = GameInformerData.MyColorSetting.White },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = determineButtonColorBySelectedColorSetting(
                            GameInformerData.MyColorSetting.White
                        )
                    )
                ) {
                    Image(
                        modifier = Modifier
                            .height(40.dp)
                            .aspectRatio(1f),
                        painter = painterResource(id = R.drawable.w_pawn),
                        contentDescription = "White"
                    )
                }
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RectangleShape,
                    onClick = { color.value = GameInformerData.MyColorSetting.Random },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = determineButtonColorBySelectedColorSetting(
                            GameInformerData.MyColorSetting.Random
                        )
                    )
                ) {
                    Image(
                        modifier = Modifier
                            .height(40.dp)
                            .aspectRatio(1f),
                        painter = painterResource(id = R.drawable.random_symbol),
                        contentDescription = "Random"
                    )
                }
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RectangleShape,
                    onClick = { color.value = GameInformerData.MyColorSetting.Black },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = determineButtonColorBySelectedColorSetting(
                            GameInformerData.MyColorSetting.Black
                        )
                    )
                ) {
                    Image(
                        modifier = Modifier
                            .height(40.dp)
                            .aspectRatio(1f),
                        painter = painterResource(id = R.drawable.b_pawn),
                        contentDescription = "Black"
                    )
                }
            }
        }

        Button(
            onClick = {
                onGameStarted(
                    NetworkGameSettings(
                        lobbyName.value,
                        color.value
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text(text = "Start game", color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Preview
@Composable
fun HostGameDialogPreview() {
    VChessTheme {
        HostGameDialogScreen {}
    }
}