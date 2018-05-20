package com.my.sudoku

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.my.sudoku.model.Sudoku
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    val sudoku= Sudoku()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        openNewGameDialog()

        sudoku.setOnFinishListener({
           val dialog=AlertDialog.Builder(this).setTitle(R.string.win_title)
                    .setMessage(R.string.win_text)
                    .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                        openNewGameDialog()
                        dialog.dismiss()
                    })
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            dialog.show()
        })
        sudokuView.setSudoku(sudoku)

        btn_new.setOnClickListener({openNewGameDialog()})
        btn_reset.setOnClickListener({reset()})
        btn_undo.setOnClickListener({undo()})
    }

    /**
     * start a new game
     */
    private fun startGame(level: Int)
    {
        sudoku.initGame(level)
        sudokuView.clear()
        sudokuView.invalidate()
    }

    /**
     * open level dialog
     */
    private fun openNewGameDialog() {
        android.app.AlertDialog.Builder(this)
                .setTitle(R.string.new_game_title)
                .setItems(R.array.difficulty,
                        DialogInterface.OnClickListener { dialoginterface, i -> startGame(i) }).show()
    }

    private fun undo()
    {
        sudokuView.clear()
        if (sudoku.undo())
            sudokuView.invalidate()
    }
    private fun reset()
    {
        sudoku.reset()
        sudokuView.clear()
        sudokuView.invalidate()
    }
}
