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
        val player1 :Player = Mockito.mock(Player::class.java)
        val player2: Player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)

        // Mock moves 1 - 8 (4 moves by player)
        val move1 = Move(player1, game, 0, 0, 1, 0)
        val move2 = Move(player2, game, 0, 1, 2, 2)
        val move3 = Move(player1, game, 2, 2, 0, 0)
        val move4 = Move(player2, game, 0, 0, 2, 0)
        val move5 = Move(player1, game, 2, 0, 0, 1)
        val move6 = Move(player2, game, 0, 1, 0, 1)
        val move7 = Move(player1, game, 0, 1, 0, 0)
        val moves: List<Move> = listOf(move1, move2, move3, move4, move5, move6, move7)

        whenever(moveRepository.findAllByGame(game)).thenReturn(moves)

        try {
            moveService.validateMoveRedundancy(Move(player1, game, 0, 0, 0, 0))
        } catch (e: Exception) {
            assertThat(e).doesNotThrowAnyException()
        }
    }

    @Test
    fun `Validates Move redundancy negative`() {
        // Mock player and game
        val player1 :Player = Mockito.mock(Player::class.java)
        val player2: Player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)

        // Mock moves 1 - 8 (4 moves by player)
        val move1 = Move(player1, game, 0, 0, 1, 0)
        val move2 = Move(player2, game, 0, 1, 2, 2)
        val move3 = Move(player1, game, 2, 2, 0, 0)
        val move4 = Move(player2, game, 0, 0, 2, 0)
        val move5 = Move(player1, game, 2, 0, 0, 1)
        val move6 = Move(player2, game, 0, 1, 0, 1)
        val move7 = Move(player1, game, 0, 1, 0, 0)
        val moves: List<Move> = listOf(move1, move2, move3, move4, move5, move6, move7)

        whenever(moveRepository.findAllByGame(game)).thenReturn(moves)


        assertThatExceptionOfType(Exception::class.java).isThrownBy { moveService.validateMoveRedundancy(Move(player1, game, 0, 1, 0, 0)) }
    }

    @Test
    fun `Validate Move in won field negative`() {
        // Mock player and game
        val player1 :Player = Mockito.mock(Player::class.java)
        val player2: Player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)

        // Mock moves 1 - 8 (4 moves by player)
        val move1 = Move(player1, game, 0, 0, 1, 0)
        val move2 = Move(player2, game, 0, 1, 2, 2)
        val move3 = Move(player1, game, 2, 2, 0, 0)
        val move4 = Move(player2, game, 0, 0, 2, 0)
        val move5 = Move(player1, game, 2, 0, 0, 1)
        val move6 = Move(player2, game, 0, 1, 0, 1)
        val move7 = Move(player1, game, 0, 1, 0, 0)
        val move8 = Move(player2, game, 0, 0, 1, 1)
        val move9 = Move(player1, game, 1, 1, 0, 0)
        val move10 = Move(player2, game, 0, 0, 0, 2)

        whenever(moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(player2, game, 0 , 0)).thenReturn(listOf(move4, move8, move10))
        whenever(moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(player1, game, 0 , 0)).thenReturn(listOf(move1))
        whenever(moveRepository.findAllByGameAndBoardRowAndBoardColumn(game, 0, 0)).thenReturn(listOf(move1, move4, move8, move10))

        assertThatExceptionOfType(Exception("You already won this field")::class.java).isThrownBy { moveService.validateMoveInWonField(Move (player2, game, 0, 0, 0, 0))}
        assertThatExceptionOfType(Exception("This field has already been won by your opponent")::class.java).isThrownBy { moveService.validateMoveInWonField(Move (player1, game, 0, 0, 0, 0))}
    }

    @Test
    fun `Validate Move in won field positive`() {
        // Mock player and game
        val player1 :Player = Mockito.mock(Player::class.java)
        val player2: Player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)

        // Mock moves 1 - 8 (4 moves by player)
        val move1 = Move(player1, game, 0, 0, 1, 0)
        val move2 = Move(player2, game, 0, 1, 2, 2)
        val move3 = Move(player1, game, 2, 2, 0, 0)
        val move4 = Move(player2, game, 0, 0, 2, 0)
        val move5 = Move(player1, game, 2, 0, 0, 1)
        val move6 = Move(player2, game, 0, 1, 0, 1)
        val move7 = Move(player1, game, 0, 1, 0, 0)
        val move8 = Move(player2, game, 0, 0, 1, 1)
        val move9 = Move(player1, game, 1, 1, 0, 0)
        val move10 = Move(player2, game, 0, 0, 0, 2)
        val moves: List<Move> = listOf(move1, move2, move3, move4, move5, move6, move7, move8, move9, move10)

        whenever(moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(player2, game, 0, 1)).thenReturn(listOf(move2, move6))
        whenever(moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(player1, game, 0, 1)).thenReturn(listOf(move7))
        whenever(moveRepository.findAllByGameAndBoardRowAndBoardColumn(game, 0, 1)).thenReturn(listOf(move2, move6, move7))

        try {
            moveService.validateMoveInWonField(Move(player1, game, 0, 1, 0, 0))
        } catch (e: Exception) {
            assertThat(e).doesNotThrowAnyException()
        }
    }
}