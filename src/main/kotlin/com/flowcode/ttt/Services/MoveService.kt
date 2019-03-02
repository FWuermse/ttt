package com.flowcode.ttt.Services

import com.flowcode.ttt.POJOs.Game
import com.flowcode.ttt.POJOs.Move
import com.flowcode.ttt.POJOs.Player
import com.flowcode.ttt.Repositories.GameRepository
import com.flowcode.ttt.Repositories.MoveRepository
import org.springframework.stereotype.Service
import java.lang.Exception
import java.time.LocalDateTime
import java.util.*

@Service
class MoveService(val gameRepository: GameRepository,
                  val moveRepository: MoveRepository,
                  val gameService: GameService,
                  val playerService: PlayerService) {

    fun makeMove(playerId: String, move: Move) {
        val game: Optional<Game> = gameRepository.findById(move.game.id!!)
        if (game.isPresent) {
            val player: Player = playerService.getPlayer(playerId)
            validatePlayer(playerId, game.get())
            validateTurnSequence(playerId, game.get())
            validatePossibleMove(move)
            validateMoveRedundancy(move)
            validateMoveInWonField(move, player)
            validateMoveInPermittedField(move, player)
            moveRepository.save(Move(player, game.get(), move.boardRow, move.boardColumn, move.fieldRow, move.fieldColumn, LocalDateTime.now()))
            checkIfGameWon(player, game.get())
            checkIfGameDraw(game.get())
        } else
            throw Exception("This game does not exist anymore.")
    }

    private fun checkIfGameDraw(game: Game) {
        val board: MutableList<MutableList<MutableList<MutableList<Boolean>>>> = MutableList(3) { MutableList(3) { MutableList(3) { MutableList(3) { false } } } }
        (0..2).forEach {
            (0..2).forEach {
                (0..2).forEach {
                    (0..2).forEach {
                        moveRepository.findAllByGame(game).forEach {
                            board[it.boardRow][it.boardColumn][it.fieldRow][it.fieldColumn]
                        }
                    }
                }
            }
        }
        if (board.filter { boardRow ->
                    boardRow.filter { boardColumn ->
                        boardColumn.filter { fieldRow ->
                            fieldRow.filter { fieldColumn ->
                                fieldColumn
                            }.size == 3
                        }.size == 3
                    }.size == 3
                }.size == 3) {
            dissolveGameAsDraw(game)
        }
    }

    private fun dissolveGameAsDraw(game: Game) {
        playerService.addPlayedGame(game.firstPlayer)
        playerService.addPlayedGame(game.secondPlayer!!)
        gameService.dissolve(game, "Draw")
    }

    private fun checkIfGameWon(player: Player, game: Game) {
        val bigField: MutableList<MutableList<Boolean>> = MutableList(3) { MutableList(3) { false } }
        (0..2).forEach { row ->
            (0..2).forEach { field ->
                val smallField: MutableList<MutableList<Boolean>> = MutableList(3) { MutableList(3) { false } }
                moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(player, game, row, field).forEach {
                    smallField[it.fieldRow][it.fieldColumn] = true
                }
                if (squareWin(smallField) || reverseSquaredWin(smallField) || straightWin(smallField))
                    bigField[row][field] = true
            }
        }
        if (squareWin(bigField) || reverseSquaredWin(bigField) || straightWin(bigField)) {
            dissolveGameAsWonBy(player, game)
        }
    }

    fun dissolveGameAsWonBy(player: Player, game: Game) {
        playerService.addPlayedGame(game.secondPlayer!!)
        playerService.addPlayedGame(game.firstPlayer)
        playerService.addWonGame(player)
        gameService.dissolve(game, "Won by ${player.name}")
    }

    fun validatePossibleMove(move: Move) {
        if (move.boardRow  !in (0..2) || move.boardColumn  !in (0..2) || move.fieldRow  !in (0..2) || move.fieldColumn  !in (0..2)) {
            throw Exception("We don't tolerate cheating! Please choose a valid field.")
        }
    }

    fun validateTurnSequence(playerId: String, game: Game) {
        if (moveRepository.findFirstByGameOrderByCreatedDesc(game).isPresent) {
            if (moveRepository.findFirstByGameOrderByCreatedDesc(game).get().player!!.id == playerId) {
                throw Exception("It's not your turn. Please wait for the other Player to move.")
            }
        } else {
            if (playerId == game.firstPlayer.id) {
                if (!firstPlayerHasX(game)) {
                    throw Exception("It's not your turn. Please wait for the other Player to move.")
                }
            } else {
                if (firstPlayerHasX(game)) {
                    throw Exception("It's not your turn. Please wait for the other Player to move.")
                }
            }
        }
    }

    fun firstPlayerHasX(game: Game): Boolean {
        return game.firstPlayerPieceCode == 'X'
    }

    fun validateMoveInPermittedField(move: Move, player: Player) {
        if (moveRepository.findFirstByGameOrderByCreatedDesc(move.game).isPresent) {
            val lastMove: Move = moveRepository.findFirstByGameOrderByCreatedDesc(move.game).get()
            try {
                validateMoveInWonField(lastMove, player)
                if (!((lastMove.fieldRow == move.boardRow).and(lastMove.fieldColumn == move.boardColumn))) {
                    throw Exception("Invalid field. Your move has to be in field ${lastMove.fieldRow + 1} - ${lastMove.fieldColumn + 1}")
                }
            } catch (e: Exception) {
                if (!(e.message.equals("This field has already been won by your opponent") || e.message.equals("You already won this field"))) {
                    throw e
                }
            }
        }
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

    fun validateMoveInWonField(move: Move, player: Player) {
        val allMoves = moveRepository.findAllByGameAndBoardRowAndBoardColumn(move.game, move.boardRow, move.boardColumn)
        val firstPlayerMoves = moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(player, move.game, move.boardRow, move.boardColumn)
        val secondPlayerMoves = allMoves.filterNot { firstPlayerMoves.contains(it) }

        validateFirstPlayersMoves(firstPlayerMoves, move, player)
        validateSecondPlayerMoves(secondPlayerMoves, move)
    }

    fun validateSecondPlayerMoves(moves: List<Move>, move: Move) {
        val smallField: MutableList<MutableList<Boolean>> = MutableList(3) { MutableList(3) { false } }
        moves.forEach {
            smallField[it.fieldRow][it.fieldColumn] = true
        }
        if (squareWin(smallField) || reverseSquaredWin(smallField) || straightWin(smallField))
            throw Exception("This field has already been won by your opponent")
    }

    fun validateFirstPlayersMoves(moves: List<Move>, move: Move, player: Player) {
        val smallField: MutableList<MutableList<Boolean>> = MutableList(3) { MutableList(3) { false } }
        moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(player, move.game, move.boardRow, move.boardColumn).forEach {
            smallField[it.fieldRow][it.fieldColumn] = true
        }
        if (squareWin(smallField) || reverseSquaredWin(smallField) || straightWin(smallField))
            throw Exception("You already won this field")
    }

    fun squareWin(smallField: MutableList<MutableList<Boolean>>): Boolean {
        return (0..2).filter { index -> smallField[index][index] }.size > 2
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

    fun getAll(gameId: Long): List<Move> {
        return moveRepository.findAllByGame(gameRepository.findById(gameId).get())
    }
}
