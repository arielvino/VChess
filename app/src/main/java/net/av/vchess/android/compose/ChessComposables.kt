package net.av.vchess.android.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import net.av.vchess.R
import net.av.vchess.android.compose.theme.TileBlocked
import net.av.vchess.android.compose.theme.TileDark
import net.av.vchess.android.compose.theme.TileLight
import net.av.vchess.android.compose.theme.TileOnlyPass
import net.av.vchess.android.compose.theme.TilePeaceful
import net.av.vchess.android.compose.theme.TileSelected
import net.av.vchess.android.compose.theme.TileTargetDark
import net.av.vchess.android.compose.theme.TileTargetLight
import net.av.vchess.android.compose.theme.VChessTheme
import net.av.vchess.android.compose.viewmodel.BoardViewModel
import net.av.vchess.android.compose.viewmodel.TileViewModel
import net.av.vchess.game.data.ActualGame
import net.av.vchess.game.data.Board
import net.av.vchess.game.data.pieces.Piece
import net.av.vchess.game.data.TestBoardRenderer
import net.av.vchess.game.data.Tile
import net.av.vchess.game.data.turn.TurnInfo
import net.av.vchess.network.data.GameSettings
import net.av.vchess.reusables.PlayerColor
import net.av.vchess.reusables.Vector2D

fun isDark(location: Vector2D): Boolean {
    return (location.x + location.y) % 2 == 0
}

val defaultTileColor = { tile: Tile ->
    if (isDark(tile.location)) TileDark
    else TileLight
}

val defaultTileBorderColor = { tile: Tile ->
    when (tile.ruledTraversability) {
        Tile.Traversability.Blocked -> TileBlocked
        Tile.Traversability.OnlyPass -> TileOnlyPass
        Tile.Traversability.Peaceful -> TilePeaceful
        Tile.Traversability.Normal -> defaultTileColor(tile)
    }
}

val pieceIconIdProvider = { tile: Tile ->
    if (tile.piece == null) {
        0
    } else {
        val piece = tile.piece!!
        when (piece::class.java.simpleName) {
            "Rook" -> {
                when (piece.color) {
                    PlayerColor.White -> R.drawable.w_rook
                    PlayerColor.Black -> R.drawable.b_rook
                }
            }

            "Queen" -> {
                when (piece.color) {
                    PlayerColor.White -> R.drawable.w_queen
                    PlayerColor.Black -> R.drawable.b_queen
                }
            }

            else -> 0
        }
    }
}

val pieceImageDescriptionProvider = { tile: Tile ->
    if (tile.piece == null) {
        null
    } else {
        val piece = tile.piece!!
        val color = when (piece.color) {
            PlayerColor.White -> "white"
            PlayerColor.Black -> "black"
        }
        val name = piece::class.java.name
        "$color $name"
    }
}

@Composable
fun ChessTile(
    tile: TileViewModel,
    bodyColorProvider: (Tile) -> Color = defaultTileColor,
    borderColorProvider: (Tile) -> Color = defaultTileBorderColor,
    onClick: (Vector2D) -> Unit = {}
) {
    val bodyColor = remember { mutableStateOf(bodyColorProvider(tile.tile)) }
    val borderColor = remember { mutableStateOf(borderColorProvider(tile.tile)) }
    val imageResId = remember { mutableIntStateOf(pieceIconIdProvider(tile.tile)) }
    val imageDescription = remember { mutableStateOf(pieceImageDescriptionProvider(tile.tile)) }

    tile.listener = object : TileViewModel.ChangesListener {
        override fun onChanged() {
            bodyColor.value = bodyColorProvider(tile.tile)
            borderColor.value = borderColorProvider(tile.tile)
            imageResId.intValue = pieceIconIdProvider(tile.tile)
            imageDescription.value = pieceImageDescriptionProvider(tile.tile)
        }

    }

    Box(modifier = Modifier
        .aspectRatio(1f) // Make the view square
        .border(
            width = 6.dp, color = borderColor.value
        )
        .background(color = bodyColor.value)
        .clickable { onClick(tile.tile.location) }) {
        if (imageResId.intValue != 0) {
            Image(
                painter = painterResource(id = imageResId.intValue),
                contentDescription = imageDescription.value,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                contentScale = ContentScale.FillBounds,
                alignment = Alignment.Center
            )
        }
    }
}

@Preview
@Composable
fun BoardPreview() {
    VChessTheme {
        val title = remember {
            mutableStateOf("Preview")
        }
        val pointOfView = rememberSaveable {
            mutableStateOf(PlayerColor.White)
        }
        Column {
            Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                val game = ActualGame(
                    object : ActualGame.IListener {
                        override fun onTurnDone(color: PlayerColor, turnInfo: TurnInfo) {

                        }

                        override fun onWin(color: PlayerColor) {
                            TODO("Not yet implemented")
                        }
                    },
                    GameSettings(
                        TestBoardRenderer(),
                        "test",
                        PlayerColor.White
                    )
                )

                GameBoard(game, pointOfView = pointOfView.value)

                Box(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.background)
                        .border(width = 5.dp, color = MaterialTheme.colorScheme.outline)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = title.value,
                        textAlign = TextAlign.Left,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            Button(
                onClick = {
                    pointOfView.value = when (pointOfView.value) {
                        PlayerColor.White -> PlayerColor.Black
                        PlayerColor.Black -> PlayerColor.White
                    }
                }, modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
            ) {
                Text(text = "Change side")
            }
        }
    }
}

@Composable
fun GameBoard(
    game: ActualGame?,
    pointOfView: PlayerColor = PlayerColor.White,
    isReactive: (player: PlayerColor) -> Boolean = { true }
) {
    if (game != null) {
        val boardViewModel = BoardViewModel(game.board)
        var selectedLocation: String? = null
        val targetsMap = mutableMapOf<Vector2D, MutableList<TurnInfo>>()

        fun unselect() {
            if (selectedLocation != null) {
                val oldSelection = Vector2D.fromString(selectedLocation!!)
                selectedLocation = null
                boardViewModel.getTile(oldSelection).refresh()

                val targetLocations = targetsMap.keys.toList()
                targetsMap.clear()
                targetLocations.forEach { location ->
                    boardViewModel.getTile(location).refresh()
                }
            }
        }

        fun selectLocation(location: Vector2D) {
            selectedLocation = location.toString()
            boardViewModel.getTile(location).refresh()

            val possibleTurns = game.ruler.getLegalTurnsFrom(
                game.board.getTile(location).piece!!.listUnfilteredPossibleTurns(game.board)
            )
            possibleTurns.forEach { turn ->
                val key = turn.targetLocation
                if (targetsMap[key] == null) {
                    targetsMap[key] = mutableListOf()
                }
                targetsMap[turn.targetLocation]!!.add(turn)
            }

            val targetLocations = targetsMap.keys.toList()
            targetLocations.forEach { targetLocation ->
                boardViewModel.getTile(targetLocation).refresh()
            }
        }

        val onTileClicked = { location: Vector2D ->
            if (selectedLocation.contentEquals(location.toString())) {
                unselect()
            } else
                if (targetsMap[location] != null) {
                    if (targetsMap[location]!!.size == 1) {
                        game.performTurn(targetsMap[location]!![0])
                        unselect()
                    } else {
                        //todo: handle multiple targets
                    }
                } else {
                    val piece: Piece? = boardViewModel.board.getTile(location).piece
                    if (piece != null) {
                        if (piece.color == game.currentTurn && isReactive(piece.color)) {
                            unselect()
                            selectLocation(location)
                        }
                    }
                }
        }

        val tileBodyColor = { tile: Tile ->
            if (selectedLocation.contentEquals(tile.location.toString())) {
                TileSelected
            } else if (targetsMap[tile.location] != null) {
                if (isDark(tile.location)) {
                    TileTargetDark
                } else {
                    TileTargetLight
                }
            } else {
                defaultTileColor(tile)
            }
        }

        val tileBorderColorProvider = { tile: Tile ->
            if (tile.ruledTraversability != Tile.Traversability.Normal) {
                defaultTileBorderColor(tile)
            } else {
                if (selectedLocation.contentEquals(tile.location.toString())) {
                    TileSelected
                } else if (targetsMap[tile.location] != null) {
                    if (isDark(tile.location)) {
                        TileTargetDark
                    } else {
                        TileTargetLight
                    }
                } else {
                    defaultTileColor(tile)
                }
            }
        }

        ChessBoard(
            boardViewModel,
            pointOfView = pointOfView,
            onTileClicked = onTileClicked,
            tileBodyColorProvider = tileBodyColor,
            tileBorderColorProvider = tileBorderColorProvider
        )
    }
}

@Composable
fun ChessBoard(
    boardViewModel: BoardViewModel = BoardViewModel(Board(8, 8)),
    onTileClicked: (Vector2D) -> Unit = {},
    tileBodyColorProvider: (Tile) -> Color = defaultTileColor,
    tileBorderColorProvider: (Tile) -> Color = defaultTileBorderColor,
    pointOfView: PlayerColor = PlayerColor.White
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(boardViewModel.board.width),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            val renderTile = { x: Int, y: Int ->
                item(key = Vector2D(x, y).toString()) {
                    ChessTile(
                        tile = boardViewModel.getTile(x, y),
                        onClick = onTileClicked,
                        borderColorProvider = tileBorderColorProvider,
                        bodyColorProvider = tileBodyColorProvider
                    )
                }
            }

            when (pointOfView) {
                PlayerColor.White -> for (y in (boardViewModel.board.height - 1).downTo(0)) {
                    for (x in 0.until(boardViewModel.board.width)) {
                        renderTile(x, y)
                    }
                }

                PlayerColor.Black -> for (y in 0.until(boardViewModel.board.height)) {
                    for (x in (boardViewModel.board.width - 1).downTo(0)) {
                        renderTile(x, y)
                    }
                }
            }
        }
    }
}