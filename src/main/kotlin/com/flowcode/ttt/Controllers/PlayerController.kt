package com.flowcode.ttt.Controllers

import com.flowcode.ttt.POJOs.Player
import com.flowcode.ttt.Services.PlayerService
import org.springframework.web.bind.annotation.*

@RestController
class PlayerController(val playerService: PlayerService) {

    @GetMapping("/player")
    fun search(@RequestParam(value = "name", defaultValue = "") name: String) =
            playerService.searchPlayer(name)

    @PostMapping("/player")
    fun create(@RequestBody player: Player) {
        playerService.createPlayer(player)
    }

}