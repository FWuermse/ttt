package com.flowcode.ttt.Services

import com.flowcode.ttt.POJOs.Player
import com.flowcode.ttt.Repositories.PlayerRepository
import org.springframework.stereotype.Service
import java.lang.Exception
import java.util.*

@Service
class PlayerService(val playerRepository: PlayerRepository) {

    fun searchPlayer(name: String): List<Player> {
        return playerRepository.findAllByNameContainingIgnoreCase(name)
    }

    fun createPlayer(player: Player) {
        playerRepository.save(player)
    }

    fun deletePlayer(id: String) {
        val player: Optional<Player> = playerRepository.findById(id)
        if (player.isPresent)
            playerRepository.delete(player.get())
        else
            throw Exception("User to delete was not found.")
    }

    fun getPlayer(id: String): Player =
            playerRepository.findById(id).get()

    fun addPlayedGame(player: Player) {
        playerRepository.save(playerRepository.findById(player.id).get().copy(gamesPlayed = + 1))
    }

    fun addWonGame(player: Player) {
        playerRepository.save(playerRepository.findById(player.id).get().copy(gamesWon = + 1))
    }
}