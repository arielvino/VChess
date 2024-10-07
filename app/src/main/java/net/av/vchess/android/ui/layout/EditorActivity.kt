package net.av.vchess.android.ui.layout

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import net.av.vchess.R
import net.av.vchess.android.viewmodels.EditorBoardViewModel
import net.av.vchess.android.viewmodels.UnresponsiveTileViewModel
import net.av.vchess.game.data.Board
import net.av.vchess.game.data.pieces.Piece
import net.av.vchess.game.data.Tile
import net.av.vchess.game.data.pieces.Pieces
import net.av.vchess.io.BoardIO
import net.av.vchess.reusables.PlayerColor
import net.av.vchess.reusables.Vector2D
import kotlin.math.ceil

class EditorActivity : ComponentActivity(), EditorBoardViewModel.IListener {

    private lateinit var boardHolder: FrameLayout
    private lateinit var piecesPanel: LinearLayout
    private lateinit var piecesLabel: Button
    private lateinit var piecesContainer: GridLayout
    private lateinit var colorSelector: ImageButton
    private lateinit var tilePropertiesPanel: LinearLayout
    private lateinit var tilePropertiesLabel: Button
    private lateinit var piecePropertiesPanel: LinearLayout
    private lateinit var piecePropertiesLabel: Button
    private lateinit var tileProperties: LinearLayout
    private lateinit var pieceProperties: LinearLayout
    private lateinit var boardView: BoardView
    private lateinit var traversabilityRadioGroup: RadioGroup
    private lateinit var boardNameInput:EditText
    private lateinit var saveBoardButton: Button


    private var color: PlayerColor = PlayerColor.White
    private var board = Board(8, 8)//todo: enable dynamic dimensions

    private var selectedLocation: Vector2D? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editor_activity)

        boardHolder = findViewById(R.id.board_holder)
        boardView = BoardView(
            this, board, EditorBoardViewModel(this, board)//todo: create new type of viewmodel
        )
        boardView.pointOfView = PlayerColor.White
        boardHolder.addView(boardView)

        piecesPanel = findViewById(R.id.pieces_panel)
        piecesLabel = findViewById(R.id.pieces_label)
        piecesLabel.setOnClickListener {
            if (piecesContainer.visibility == View.GONE) {
                piecesContainer.visibility = View.VISIBLE
                colorSelector.visibility = View.VISIBLE
            } else {
                piecesContainer.visibility = View.GONE
                colorSelector.visibility = View.GONE
            }
        }

        piecesContainer = findViewById(R.id.pieces_container)
        window.decorView.rootView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                refreshPieces()

                window.decorView.rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        colorSelector = findViewById(R.id.color_selector)
        colorSelector.setOnClickListener {
            handleColorSelectorClick()
        }

        tilePropertiesPanel = findViewById(R.id.tile_properties_panel)
        tilePropertiesLabel = findViewById(R.id.tile_properties_label)
        tilePropertiesLabel.setOnClickListener {
            tileProperties.visibility =
                if (tileProperties.visibility == View.GONE) View.VISIBLE else View.GONE
        }
        tileProperties = findViewById(R.id.tile_properties_container)
        traversabilityRadioGroup = findViewById(R.id.radio_group_traversability)
        traversabilityRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedTraversability = when (checkedId) {
                R.id.radio_blocked -> Tile.Traversability.Blocked
                R.id.radio_only_pass -> Tile.Traversability.OnlyPass
                R.id.radio_peaceful -> Tile.Traversability.Peaceful
                R.id.radio_normal -> Tile.Traversability.Normal
                else -> null
            }
            // Do something with the selected Traversability value
            if (selectedLocation != null) {
                board.getTile(selectedLocation!!).consistentTraversability =
                    selectedTraversability!!
            }
        }

        piecePropertiesPanel = findViewById(R.id.piece_properties_panel)
        piecePropertiesLabel = findViewById(R.id.piece_properties_label)
        piecePropertiesLabel.setOnClickListener {
            pieceProperties.visibility =
                if (pieceProperties.visibility == View.GONE) View.VISIBLE else View.GONE
        }
        pieceProperties = findViewById(R.id.piece_properties)

        boardNameInput = findViewById(R.id.board_name)
        saveBoardButton = findViewById(R.id.save_board_button)
        saveBoardButton.setOnClickListener {
            if(BoardIO.saveBoard(board, boardNameInput.text.toString()))
            {
                finish()
            }
            else{
                Toast.makeText(this, "An error occurred. You may try another name.", Toast.LENGTH_LONG).show()
            }
        }

        refreshProperties()
    }

    private fun refreshPieces() {
        piecesContainer.removeAllViews()
        val pieces = Pieces.getPiecesTypes()
        val numOfButtonsInRow = 6
        piecesContainer.columnCount = numOfButtonsInRow
        piecesContainer.rowCount = ceil(pieces.size / numOfButtonsInRow.toFloat()).toInt()
        for (i in 0.until(pieces.size)) {
            val pieceButton = ImageButton(this)
            piecesContainer.addView(pieceButton)
            val colorLetter = if (color == PlayerColor.White) "w" else "b"
            pieceButton.setImageDrawable(
                UnresponsiveTileViewModel.getDrawableFromAsset(
                    this, colorLetter + "_" + pieces[i].simpleName.lowercase() + ".png"
                )
            )
            val layoutParams = pieceButton.layoutParams as GridLayout.LayoutParams
            layoutParams.rowSpec = GridLayout.spec(i / numOfButtonsInRow)
            layoutParams.columnSpec = GridLayout.spec(i % numOfButtonsInRow)
            layoutParams.width = window.decorView.width / numOfButtonsInRow
            layoutParams.height = window.decorView.width / numOfButtonsInRow
            pieceButton.scaleType = ImageView.ScaleType.CENTER_INSIDE
            pieceButton.setOnClickListener {
                if (selectedLocation != null) {
                    val steps = 0
                    val location = selectedLocation

                    val piece = Class.forName(pieces[i].name).constructors.first()
                        .newInstance(color, board, location, steps) as Piece
                    board.getTile(selectedLocation!!).piece = piece
                    refreshProperties()
                }
            }
        }
    }

    private fun handleColorSelectorClick() {
        if (color == PlayerColor.White) {
            color = PlayerColor.Black
            colorSelector.setImageResource(R.drawable.w_pawn)
        } else {
            color = PlayerColor.White
            colorSelector.setImageResource(R.drawable.b_pawn)
        }
        refreshPieces()
    }

    private fun refreshProperties() {
        if (selectedLocation == null) {
            piecesPanel.visibility = View.GONE
            piecePropertiesPanel.visibility = View.GONE
            tilePropertiesPanel.visibility = View.GONE
        } else {
            piecesPanel.visibility = View.VISIBLE
            tilePropertiesPanel.visibility = View.VISIBLE
            traversabilityRadioGroup.check(
                when (board.getTile(selectedLocation!!).consistentTraversability) {
                    Tile.Traversability.Normal -> R.id.radio_normal
                    Tile.Traversability.Peaceful -> R.id.radio_peaceful
                    Tile.Traversability.Blocked -> R.id.radio_blocked
                    Tile.Traversability.OnlyPass -> R.id.radio_only_pass
                }
            )

            if (board.getTile(selectedLocation!!).piece != null) {
                piecePropertiesPanel.visibility = View.VISIBLE
            }
            //todo: piece properties
        }
    }

    override fun onSelectionChanged(selectedLocation:Vector2D?) {
        this.selectedLocation = selectedLocation
        refreshProperties()
    }
}
