package com.flowcode.ttt.Controllers;

import com.flowcode.ttt.Services.GameService
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
class GameController(val gameService: GameService) {

    @PostMapping("/create")
    fun create(@RequestParam(value = "id") id: String) =
            gameService.create(id)

    @GetMapping("/join")
    fun join(@RequestParam(value = "playerId") playerId: String,
             @RequestParam(value = "gameId") gameId: Long)=
            gameService.addPlayer(playerId, gameId)

    @GetMapping("/")
    fun getAll() = gameService.getAll()
}
