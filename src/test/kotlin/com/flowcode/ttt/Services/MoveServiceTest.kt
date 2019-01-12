package com.flowcode.ttt.Services

import com.flowcode.ttt.POJOs.Game
import com.flowcode.ttt.POJOs.Move
import com.flowcode.ttt.POJOs.Player
import com.flowcode.ttt.Repositories.MoveRepository
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.Exception

@ExtendWith(SpringExtension::class)
@SpringBootTest
class MoveServiceTest(@Autowired val moveService: MoveService) {

    @MockBean
    private lateinit var moveRepository: MoveRepository

    @Test
    fun `Validates Move redundancy positive`() {
        // Mock player and game
        val player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)

        // Mock moves 1 - 8 (4 moves by player)
        val move1 = Move(player, game, 0, 0, 1, 0)
        val move3 = Move(player, game, 2, 2, 0, 0)
        val move2 = Move(player, game, 2, 0, 0, 1)
        val move4 = Move(player, game, 0, 1, 0, 0)
        val moves: List<Move> = listOf(move1, move2, move3, move4)

        whenever(moveRepository.findAllByGame(game)).thenReturn(moves)

        try {
            moveService.validateMoveRedundancy(Move(player, game, 0, 0, 0, 0))
        } catch (e: Exception) {
            assertThat(e).doesNotThrowAnyException()
        }
    }

    @Test
    fun `Validates Move redundancy negative`() {
        // Mock player and game
        val player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)

        // Mock moves 1 - 8 (4 moves by player)
        val move1 = Move(player, game, 0, 0, 1, 0)
        val move3 = Move(player, game, 2, 2, 0, 0)
        val move2 = Move(player, game, 2, 0, 0, 1)
        val move4 = Move(player, game, 0, 1, 0, 0)
        val moves: List<Move> = listOf(move1, move2, move3, move4)

        whenever(moveRepository.findAllByGame(game)).thenReturn(moves)

        assertThatExceptionOfType(Exception::class.java).isThrownBy { moveService.validateMoveRedundancy(Move(player, game, 0, 1, 0, 0)) }
    }
}