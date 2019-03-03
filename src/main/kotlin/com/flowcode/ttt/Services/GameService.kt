package com.flowcode.ttt.Services

import com.flowcode.ttt.POJOs.Game
import com.flowcode.ttt.Repositories.GameRepository
import com.flowcode.ttt.Repositories.MoveRepository
import org.springframework.stereotype.Service
import java.lang.Exception
import java.util.*

@Service
class GameService(val gameRepository: GameRepository, val playerService: PlayerService, val moveRepository: MoveRepository) {

    fun create(playerId: String) {
        val game = Game(
                firstPlayer = playerService.getPlayer(playerId),
                secondPlayer = null,
                status = "pending",
                type = "normal",
                firstPlayerPieceCode = randomPieceCode()
                )
        gameRepository.save(game)
    }

    fun addPlayer(playerId: String, gameId: Long) {
        val game = gameRepository.findById(gameId)
        if (game.isPresent)
            if (game.get().secondPlayer == null && game.get().status == "pending" && game.get().firstPlayer.id != playerId) {
                gameRepository.save(game.get().copy(secondPlayer = playerService.getPlayer(playerId), status = "inprogress"))
            } else {
                throw Exception("This game is already full! Someone was faster.")
            }
    }

    private fun randomPieceCode(): Char {
        val random = Random(2)
        when (random.nextInt(2)) {
            1 -> return 'X'
            0 -> return 'O'
        }
        throw Exception("Random X or O generator failed.")
    }

    fun getAll(): MutableIterable<Game> {
        return gameRepository.findAll()
    }

    fun dissolve(game: Game, status: String) {
        gameRepository.save(gameRepository.findById(game.id!!).get().copy(status = status))
    }
}
