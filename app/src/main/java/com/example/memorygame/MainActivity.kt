package com.example.memorygame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import com.example.memorygame.models.BoardSize
import com.example.memorygame.models.MemoryCard
import com.example.memorygame.models.MemoryGame
import com.example.memorygame.utils.DEFAULT_ICONS
import com.example.memorygame.utils.EXTRA_BOARD_SIZE
import com.google.android.material.animation.ArgbEvaluatorCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(){

    private lateinit var memoryGame: MemoryGame
    private lateinit var recyclerViewBoard: RecyclerView
    private lateinit var numMovesTextView: TextView
    private lateinit var numPairsTextView: TextView
    private lateinit var adapter: MemoryBoardAdapter
    private lateinit var clRoot: ConstraintLayout

    private lateinit var startForResult:ActivityResultLauncher<Intent>

    private var boardSize: BoardSize = BoardSize.EASY


    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerViewBoard = findViewById(R.id.acitivity_main_RecyclerView)
        numMovesTextView = findViewById(R.id.activity_main_TextView_numMoves)
        numPairsTextView = findViewById(R.id.activity_main_TextView_numPairs)
        clRoot = findViewById(R.id.main)

        // setup Board
        setUpBoard()

        // initialize startForResult: ActivityResultLauncher<Intent>
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            // some data handling
            // testing commit
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.mi_referesh ->
                {
                    if(memoryGame.getNumMove() > 0 && !memoryGame.haveWon()) {
                        showAlertDialog("Quit you current game?", null, View.OnClickListener {
                            setUpBoard()
                        })
                    }
                    else
                    {
                        setUpBoard()
                    }
                    return true
                }

            R.id.mi_new_size ->
                {
                    showNewSizeDialog()
                    return true
                }

            R.id.mi_custom ->
                {
                   showCreationDialog()
                }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showCreationDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null, false)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)

        showAlertDialog("Create your own memory board", boardSizeView, View.OnClickListener {
            val desiredBoardSize = when (radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            // Navigate to a new activity
            val intent = Intent(this, CreateActivity::class.java)
            intent.putExtra(EXTRA_BOARD_SIZE, desiredBoardSize)
            startForResult.launch(intent)
        })

    }

    private fun showNewSizeDialog() {

        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null, false)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)

        when(boardSize)
        {
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
        }

        showAlertDialog("Choose New Size", boardSizeView, View.OnClickListener {
            boardSize = when (radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            setUpBoard()
        })
    }

    private fun updateGameWithFlip(position: Int) {
        // error handling
        if(memoryGame.haveWon())
        {
            Snackbar.make(clRoot, "You already won the game", Snackbar.LENGTH_LONG).show()
            return
        }
        if (memoryGame.isCardFaceUp(position))
        {
            Snackbar.make(clRoot, "Invalid move", Snackbar.LENGTH_SHORT).show()
            return
        }
        if(memoryGame.flipCard(position))
        {
            numPairsTextView.text = "Pairs: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            val color = ArgbEvaluatorCompat().evaluate(
                memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                ContextCompat.getColor(this, R.color.color_progress_none),
                ContextCompat.getColor(this, R.color.color_progress_full))

            numPairsTextView.setTextColor(color)
            if(memoryGame.haveWon())
            {
                Snackbar.make(clRoot, "You Won!! Total Moves: ${memoryGame.getNumMove()}", Snackbar.LENGTH_LONG).show()
            }
        }


        numMovesTextView.text = "Moves: ${memoryGame.getNumMove()}"
        adapter.notifyDataSetChanged()
    }

    private fun setUpBoard()
    {
        //creating game object
        memoryGame = MemoryGame(boardSize)
        numPairsTextView.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))
        adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards, object: MemoryBoardAdapter.CardClickListener{
            override fun onCardClick(position: Int) {
                updateGameWithFlip(position)
            }

        })

        numPairsTextView.text = "Pairs: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
        numMovesTextView.text = "Moves: 0"
        recyclerViewBoard.adapter = adapter
        recyclerViewBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
        recyclerViewBoard.setHasFixedSize(true)
    }

    private fun showAlertDialog(title:String, view: View?, postiveClickListener: View.OnClickListener)
    {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") { _, _ ->
                postiveClickListener.onClick(null)
            }.show()
    }

}