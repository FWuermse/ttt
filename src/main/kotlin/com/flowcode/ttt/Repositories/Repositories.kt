package com.flowcode.ttt.Repositories

import com.flowcode.ttt.POJOs.Game
import com.flowcode.ttt.POJOs.Move
import com.flowcode.ttt.POJOs.Player
import org.springframework.data.repository.CrudRepository

interface GameRepository : CrudRepository<Game, Long> {
    // for date?? fun findAllByOrderByCreatedDesc(): Iterable<Game>
    fun findAllByFirstPlayerOrSecondPlayer(firstPlayer: Player, secondPlayer: Player): List<Game>
}

interface PlayerRepository : CrudRepository<Player, String> {
    fun findAllByNameContainingIgnoreCase(name: String): List<Player>
}

interface MoveRepository : CrudRepository<Move, Long> {
    fun findAllByPlayerAndGame(player: Player, game: Game): List<Move>
    fun findAllByGame(game: Game): List<Move>
    fun findFirstByGameOrderByCreatedDesc(game: Game): Move

}