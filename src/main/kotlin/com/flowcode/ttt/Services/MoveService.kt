package com.flowcode.ttt.Services

import com.flowcode.ttt.POJOs.Game
import com.flowcode.ttt.POJOs.Move
import com.flowcode.ttt.Repositories.GameRepository
import com.flowcode.ttt.Repositories.MoveRepository
import org.springframework.stereotype.Service
import java.lang.Exception
import java.util.*

@Service
class MoveService(val gameRepository: GameRepository, val moveRepository: MoveRepository) {

    fun makeMove(playerId: String, move: Move) {
        val game: Optional<Game> = gameRepository.findById(move.game.id!!)
        if (game.isPresent) {
            validatePlayer(playerId, game.get())
            validateMoveRedundancy(move)
            validateMoveInWonField(move)
            validateMoveInPermittedField(move)
        } else
            throw Exception("This game does not exist anymore. Please refresh your browser.")
    }

    private fun validateMoveInPermittedField(move: Move) {
        moveRepository.findLastByGameOrderByCreatedDesc(move.game)
    }

    fun validatePlayer(playerId: String, game: Game) {
        if (game.firstPlayer.id == playerId || game.secondPlayer!!.id == playerId)
        else
            throw Exception("The user you are logged in with is not permitted to access this game!")
    }

    fun validateMoveRedundancy(executedMove: Move) {
        for (pastMove: Move in moveRepository.findAllByGame(executedMove.game)) {
            if (pastMove.boardColumn == executedMove.boardColumn && pastMove.boardRow == executedMove.boardRow && pastMove.fieldColumn == executedMove.fieldColumn && pastMove.fieldRow == executedMove.fieldRow)
                throw Exception("This move has already been made.")
        }
    }

    fun validateMoveInWonField(move: Move) {
        val allMoves = moveRepository.findAllByGameAndBoardRowAndBoardColumn(move.game, move.boardRow, move.boardColumn)
        val firstPlayerMoves = moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(move.player, move.game, move.boardRow, move.boardColumn)
        val secondPlayerMoves = allMoves.filterNot { firstPlayerMoves.contains(it) }

        validateFirstPlayersMoves(firstPlayerMoves, move)
        validateSecondPlayerMoves(secondPlayerMoves, move)
    }

    fun validateSecondPlayerMoves(moves: List<Move> ,move: Move) {
        val smallField: MutableList<MutableList<Boolean>> = MutableList(3) { MutableList(3) { false } }
        moves.forEach {
            smallField[it.fieldRow][it.fieldColumn] = true }
        if (squareWin(smallField) || reverseSquaredWin(smallField) || straightWin(smallField))
            throw Exception("This field has already been won by your opponent")
    }

    fun validateFirstPlayersMoves(moves: List<Move>, move: Move) {
        val smallField: MutableList<MutableList<Boolean>> = MutableList(3) { MutableList(3) { false } }
        moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(move.player, move.game, move.boardRow, move.boardColumn).forEach {
            smallField[it.fieldRow][it.fieldColumn] = true
        }
        if (squareWin(smallField) || reverseSquaredWin(smallField) || straightWin(smallField))
            throw Exception("You already won this field")
    }

    fun squareWin(smallField: MutableList<MutableList<Boolean>>): Boolean {
        return (0..2).filter{ index -> smallField[index][index] }.size > 2
    }

    fun reverseSquaredWin(smallField: MutableList<MutableList<Boolean>>): Boolean {
        return (0..2).filter { row -> smallField[row][getColumn(row)] }.size > 2
    }

    fun getColumn(row: Int): Int {
        return -row + 2
    }

    fun straightWin(smallField: MutableList<MutableList<Boolean>>): Boolean {
        return (0..2).any { row ->
            smallField[row].filter { column ->
                column
            }.size > 2
        } || (0..2).any { row ->
            (0..2).filter { column ->
                smallField[column][row]
            }.size > 2
        }
    }
}
