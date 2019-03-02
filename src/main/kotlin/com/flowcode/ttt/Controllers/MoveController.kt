package com.flowcode.ttt.Controllers

import com.flowcode.ttt.POJOs.Move
import com.flowcode.ttt.Services.MoveService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/move")
class MoveController(val moveService: MoveService) {

    @PostMapping("/")
    fun join(@RequestBody move: Move,
             @RequestParam(value = "playerId") playerId: String) =
            moveService.makeMove(playerId, move)

    @GetMapping("/")
    fun getAll(@RequestParam(value = "gameId") gameId: Long) =
            moveService.getAll(gameId)
}