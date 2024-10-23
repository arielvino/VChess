package net.av.vchess.android.compose

import android.content.Intent
import net.av.vchess.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay
import net.av.vchess.android.App
import net.av.vchess.android.compose.theme.VChessTheme
import net.av.vchess.android.ui.layout.EditorActivity
import net.av.vchess.network.GameSearcher
import net.av.vchess.network.data.GameInformerData
import net.av.vchess.network.data.GameOfferData
import net.av.vchess.network.data.NetworkGameSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()

        setContent {
            VChessTheme {
                MyApp()
            }
        }
    }
}

@Suppress("LocalVariableName")
@Composable
fun MyApp() {
    val navController = rememberNavController()

    val LAUNCH = "launch"
    val MAIN_MENU = "mainMenu"
    val EDITOR = "editor"
    val PLAY_LAN = "play_lan"
    val SEARCH_GAME_LAN = "search_game_lan"
    val JOIN_LAN_GAME = "join_lan_game"
    val HOST_GAME_DIALOG = "host_game_dialog"
    val HOST_LAN_GAME = "host_game_lan"

    VChessTheme {
        NavHost(
            navController = navController, startDestination = LAUNCH
        ) {
            composable(
                route = LAUNCH,
            ) {
                LaunchScreen {
                    navController.navigate(MAIN_MENU) {
                        popUpTo(LAUNCH) {
                            inclusive = true
                        }
                    }
                }
            }
            composable(
                route = MAIN_MENU
            ) {
                MainMenuScreen(onEditorButtonClicked = {
                    navController.navigate(EDITOR)
                }, onPlayLanButtonClicked = {
                    navController.navigate(PLAY_LAN)
                })
            }
            composable(
                route = PLAY_LAN
            ) {
                PlayLanScreen(onJoinGameClicked = {
                    navController.navigate(SEARCH_GAME_LAN)
                }, onHostGameClicked = {
                    navController.navigate(HOST_GAME_DIALOG)
                })
            }
            composable(
                route = SEARCH_GAME_LAN
            ) {
                SearchGameLanScreen(onGameSelected = { ipAddress -> navController.navigate("$JOIN_LAN_GAME/$ipAddress") })
            }
            composable(route = HOST_GAME_DIALOG) {
                HostGameDialogScreen(onGameStarted = { data: NetworkGameSettings ->
                    if (App.keyPair != null) {
                        navController.navigate("$HOST_LAN_GAME/${data.lobbyName}/${data.myColorSetting.name}")
                    }
                })
            }
            composable(
                route = "$HOST_LAN_GAME/{lobbyName}/{myColor}",
                arguments = listOf(
                    navArgument("lobbyName") { type = NavType.StringType },
                    navArgument("myColor") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val lobbyName = backStackEntry.arguments?.getString("lobbyName")
                val myColor = backStackEntry.arguments?.getString("myColor")
                val recipientColor = when(GameInformerData.MyColorSetting.valueOf(myColor!!)){
                    GameInformerData.MyColorSetting.White -> GameInformerData.MyColorSetting.Black
                    GameInformerData.MyColorSetting.Black -> GameInformerData.MyColorSetting.White
                    GameInformerData.MyColorSetting.Random -> GameInformerData.MyColorSetting.Random
                }

                HostLanGameScreen(
                    data = GameInformerData(
                        recipientColor,
                        lobbyName!!
                    ),
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = "$JOIN_LAN_GAME/{ipAddress}",
                arguments = listOf(
                    navArgument("ipAddress") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val ipAddress = backStackEntry.arguments?.getString("ipAddress")

                JoinLanGameScreen(
                    ipAddress = ipAddress!!,
                    onBackPressed = { navController.popBackStack() })
            }
            composable(route = EDITOR) {
                val context = LocalContext.current
                val intent = Intent(context, EditorActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
}

@Composable
fun LaunchScreen(onContinue: () -> Unit) {
    LaunchedEffect(key1 = Unit) {
        // Delay for 2 seconds
        delay(1500)
        onContinue()
    }
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Welcome to VChess!",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MainMenuScreen(onEditorButtonClicked: () -> Unit, onPlayLanButtonClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        MenuButton(text = "Editor", onClick = onEditorButtonClicked)
        Spacer(modifier = Modifier.height(5.dp))
        MenuButton(text = "Play LAN", onClick = onPlayLanButtonClicked)
    }
}

@Composable
fun PlayLanScreen(onJoinGameClicked: () -> Unit, onHostGameClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        MenuButton(text = "Host game", onClick = onHostGameClicked)
        Spacer(modifier = Modifier.height(5.dp))
        MenuButton(text = "Join game", onClick = onJoinGameClicked)
    }
}

@Composable
fun SearchGameLanScreen(
    onGameSelected: (ipAddress: String) -> Unit,
) {

    val items = remember {
        mutableStateListOf<GameOfferData>()
    }
    val searching = remember {
        mutableStateOf(false)
    }

    val searcher = GameSearcher(object : GameSearcher.ResultCollector {
        override fun onSearchStarted() {
            searching.value = true
        }

        override fun onResultFound(data: GameOfferData) {
            items.add(data)
        }

        override fun onFinish() {
            searching.value = false
        }
    })

    val startScan = {
        items.clear()
        searching.value = true
        searcher.scanForGames()
    }

    LaunchedEffect(Unit) {
        startScan()
    }


    Column(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { offer ->
                LanGameOffer(
                    modifier = Modifier.clickable { onGameSelected(offer.ipAddress) }, data = offer
                )
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp),
            enabled = !searching.value,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            onClick = startScan
        ) {
            Text(text = "Refresh", color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
fun LanGameOffer(modifier: Modifier, data: GameOfferData) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(5.dp)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(3.dp), horizontalAlignment = AbsoluteAlignment.Left) {
            Text(text = data.lobbyName, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(
                modifier = Modifier.padding(3.dp), text = data.ipAddress, color = Color(0xff939393)
            )
        }
        val resId = when (data.recipientColorSetting) {
            GameInformerData.MyColorSetting.Black -> R.drawable.b_pawn
            GameInformerData.MyColorSetting.White -> R.drawable.w_pawn
            GameInformerData.MyColorSetting.Random -> R.drawable.random_symbol
        }
        Image(
            modifier = Modifier
                .size(40.dp)
                .padding(3.dp),
            painter = painterResource(id = resId),
            contentDescription = data.recipientColorSetting.name
        )
    }
}

@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = onClick,
        shape = RectangleShape
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}