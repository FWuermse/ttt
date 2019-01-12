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
        } else
            throw Exception("This game does not exist anymore. Please refresh your browser.")
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
        var bigField = MutableList(3) {MutableList(3) {MutableList(3) {MutableList(3) {false}}}}
        for (move: Move in moveRepository.findAllByPlayerAndGame(move.player, move.game)) {
            bigField[move.fieldRow][move.fieldColumn][move.fieldRow][move.fieldColumn] = true
        }
        var smallField: MutableList<MutableList<Boolean>> = bigField[move.fieldColumn][move.fieldRow]
        if (squareWin(smallField) || reverseSquaredWin(smallField) || straightWin(smallField))
            throw Exception("This field has already been won")
    }

    fun squareWin(smallField: MutableList<MutableList<Boolean>>): Boolean {
        var index: Int = 0
        var amount: Int = 0

        while (index < 3) {
            if (smallField[index][index]) {
                amount ++
            }
            index ++
        }
        return amount > 2
    }

    fun reverseSquaredWin(smallField: MutableList<MutableList<Boolean>>): Boolean {
        var index: Int = 0
        var reverseIndex: Int = 2
        var amount: Int = 0

        while (index < 3) {
            if (smallField[index][reverseIndex]) {
                amount += 1
            }
            index ++
            reverseIndex --
        }
        return amount > 2
    }

    fun straightWin(smallField: MutableList<MutableList<Boolean>>): Boolean {
        val index: Int = 0

        while (index < 3) {
            if (smallField[index].filter { b -> b }.count() > 2)
                println()
        }
        return false
    }
}
