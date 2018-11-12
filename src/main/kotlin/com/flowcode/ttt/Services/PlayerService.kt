package com.flowcode.ttt.Services

import com.flowcode.ttt.POJOs.Player
import com.flowcode.ttt.Repositories.PlayerRepository
import org.springframework.stereotype.Service

@Service
class PlayerService(val playerRepository: PlayerRepository) {

    fun searchPlayer(name: String): List<Player> {
        return playerRepository.findAllByNameContainingIgnoreCase(name)
    }

    fun createPlayer(player: Player) {
        playerRepository.save(player)
    }
}