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
import java.time.LocalDateTime
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
class MoveServiceTest(@Autowired val moveService: MoveService) {

    @MockBean
    private lateinit var moveRepository: MoveRepository

    @Test
    fun `Validates Move redundancy positive`() {
        // Mock player and game
        val player1: Player = Mockito.mock(Player::class.java)
        val player2: Player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)

        // Mock moves 1 - 8 (4 moves by player)
        val move1 = Move(player1, game, 0, 0, 1, 0, LocalDateTime.now())
        val move2 = Move(player2, game, 0, 1, 2, 2, LocalDateTime.now())
        val move3 = Move(player1, game, 2, 2, 0, 0, LocalDateTime.now())
        val move4 = Move(player2, game, 0, 0, 2, 0, LocalDateTime.now())
        val move5 = Move(player1, game, 2, 0, 0, 1, LocalDateTime.now())
        val move6 = Move(player2, game, 0, 1, 0, 1, LocalDateTime.now())
        val move7 = Move(player1, game, 0, 1, 0, 0, LocalDateTime.now())
        val moves: List<Move> = listOf(move1, move2, move3, move4, move5, move6, move7)

        whenever(moveRepository.findAllByGame(game)).thenReturn(moves)

        try {
            moveService.validateMoveRedundancy(Move(player1, game, 0, 0, 0, 0, LocalDateTime.now()))
        } catch (e: Exception) {
            assertThat(e).doesNotThrowAnyException()
        }
    }

    @Test
    fun `Validates Move redundancy negative`() {
        // Mock player and game
        val player1: Player = Mockito.mock(Player::class.java)
        val player2: Player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)

        // Mock moves 1 - 8 (4 moves by player)
        val move1 = Move(player1, game, 0, 0, 1, 0, LocalDateTime.now())
        val move2 = Move(player2, game, 0, 1, 2, 2, LocalDateTime.now())
        val move3 = Move(player1, game, 2, 2, 0, 0, LocalDateTime.now())
        val move4 = Move(player2, game, 0, 0, 2, 0, LocalDateTime.now())
        val move5 = Move(player1, game, 2, 0, 0, 1, LocalDateTime.now())
        val move6 = Move(player2, game, 0, 1, 0, 1, LocalDateTime.now())
        val move7 = Move(player1, game, 0, 1, 0, 0, LocalDateTime.now())
        val moves: List<Move> = listOf(move1, move2, move3, move4, move5, move6, move7)

        whenever(moveRepository.findAllByGame(game)).thenReturn(moves)


        assertThatExceptionOfType(Exception::class.java).isThrownBy { moveService.validateMoveRedundancy(Move(player1, game, 0, 1, 0, 0, LocalDateTime.now())) }
    }

    @Test
    fun `Validate Move in won field negative`() {
        // Mock player and game
        val player1: Player = Mockito.mock(Player::class.java)
        val player2: Player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)

        // Mock moves 1 - 8 (4 moves by player)
        val move1 = Move(player1, game, 0, 0, 1, 0, LocalDateTime.now())
        val move2 = Move(player2, game, 0, 1, 2, 2, LocalDateTime.now())
        val move3 = Move(player1, game, 2, 2, 0, 0, LocalDateTime.now())
        val move4 = Move(player2, game, 0, 0, 2, 0, LocalDateTime.now())
        val move5 = Move(player1, game, 2, 0, 0, 1, LocalDateTime.now())
        val move6 = Move(player2, game, 0, 1, 0, 1, LocalDateTime.now())
        val move7 = Move(player1, game, 0, 1, 0, 0, LocalDateTime.now())
        val move8 = Move(player2, game, 0, 0, 1, 1, LocalDateTime.now())
        val move9 = Move(player1, game, 1, 1, 0, 0, LocalDateTime.now())
        val move10 = Move(player2, game, 0, 0, 0, 2, LocalDateTime.now())

        whenever(moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(player2, game, 0, 0)).thenReturn(listOf(move4, move8, move10))
        whenever(moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(player1, game, 0, 0)).thenReturn(listOf(move1))
        whenever(moveRepository.findAllByGameAndBoardRowAndBoardColumn(game, 0, 0)).thenReturn(listOf(move1, move4, move8, move10))

        assertThatExceptionOfType(Exception::class.java).isThrownBy { moveService.validateMoveInWonField(Move(player2, game, 0, 0, 0, 0, LocalDateTime.now()), player2) }
        assertThatExceptionOfType(Exception::class.java).isThrownBy { moveService.validateMoveInWonField(Move(player1, game, 0, 0, 0, 0, LocalDateTime.now()), player1) }
        try {
            moveService.validateMoveInWonField(Move(player2, game, 0, 0, 0, 0, LocalDateTime.now()), player2)
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("You already won this field")
        }

        try {
            moveService.validateMoveInWonField(Move(player1, game, 0, 0, 0, 0, LocalDateTime.now()), player1)
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("This field has already been won by your opponent")
        }
    }

    @Test
    fun `Validate Move in won field positive`() {
        // Mock player and game
        val player1: Player = Mockito.mock(Player::class.java)
        val player2: Player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)

        // Mock moves 1 - 8 (4 moves by player)
        val move1 = Move(player1, game, 0, 0, 1, 0, LocalDateTime.now())
        val move2 = Move(player2, game, 0, 1, 2, 2, LocalDateTime.now())
        val move3 = Move(player1, game, 2, 2, 0, 0, LocalDateTime.now())
        val move4 = Move(player2, game, 0, 0, 2, 0, LocalDateTime.now())
        val move5 = Move(player1, game, 2, 0, 0, 1, LocalDateTime.now())
        val move6 = Move(player2, game, 0, 1, 0, 1, LocalDateTime.now())
        val move7 = Move(player1, game, 0, 1, 0, 0, LocalDateTime.now())
        val move8 = Move(player2, game, 0, 0, 1, 1, LocalDateTime.now())
        val move9 = Move(player1, game, 1, 1, 0, 0, LocalDateTime.now())
        val move10 = Move(player2, game, 0, 0, 0, 2, LocalDateTime.now())

        // Mock other method calls
        whenever(moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(player2, game, 0, 1)).thenReturn(listOf(move2, move6))
        whenever(moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(player1, game, 0, 1)).thenReturn(listOf(move7))
        whenever(moveRepository.findAllByGameAndBoardRowAndBoardColumn(game, 0, 1)).thenReturn(listOf(move2, move6, move7))

        try {
            moveService.validateMoveInWonField(Move(player1, game, 0, 1, 0, 0, LocalDateTime.now()), player1)
        } catch (e: Exception) {
            assertThat(e).doesNotThrowAnyException()
        }
    }

    @Test
    fun `Validate Move in permitted Field positive`() {
        // Mock player and game
        val player1: Player = Mockito.mock(Player::class.java)
        val player2: Player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)

        // Mock moves 1 - 8 (4 moves by player)
        val move1 = Move(player1, game, 0, 0, 1, 0, LocalDateTime.of(2019, 3, 1, 14, 50, 58))
        val move2 = Move(player2, game, 0, 1, 2, 2, LocalDateTime.of(2019, 3, 1, 15, 11, 34))
        val move3 = Move(player1, game, 2, 2, 0, 0, LocalDateTime.of(2019, 3, 1, 15, 15, 32))
        val move4 = Move(player2, game, 0, 0, 2, 0, LocalDateTime.of(2019, 3, 1, 15, 23, 11))
        val move5 = Move(player1, game, 2, 0, 0, 1, LocalDateTime.of(2019, 3, 1, 15, 24, 44))
        val move6 = Move(player2, game, 0, 1, 0, 1, LocalDateTime.of(2019, 3, 1, 15, 26, 31))
        val move7 = Move(player1, game, 0, 1, 0, 0, LocalDateTime.of(2019, 3, 1, 15, 31, 1))
        val move8 = Move(player2, game, 0, 0, 1, 1, LocalDateTime.of(2019, 3, 1, 15, 31, 9))
        val move9 = Move(player1, game, 1, 1, 0, 0, LocalDateTime.of(2019, 3, 1, 15, 45, 39))
        val move10 = Move(player2, game, 0, 0, 0, 2, LocalDateTime.of(2019, 3, 1, 15, 54, 58))

        // Mock other method calls
        whenever(moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(player2, game, 0, 1)).thenReturn(listOf(move2, move6))
        whenever(moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(player1, game, 0, 1)).thenReturn(listOf(move7))
        whenever(moveRepository.findAllByGameAndBoardRowAndBoardColumn(game, 0, 1)).thenReturn(listOf(move2, move6, move7))
        whenever(moveRepository.findFirstByGameOrderByCreatedDesc(game)).thenReturn(Optional.of(move10))

        try {
            moveService.validateMoveInPermittedField(Move(player1, game, 0, 2, 0, 0, LocalDateTime.of(2019, 3, 1, 16, 1, 1, 1)), player1)
        } catch (e: Exception) {
            assertThat(e).doesNotThrowAnyException()
        }
    }

    @Test
    fun `Validate Move in permitted Field negative`() {
        // Mock player and game
        val player1: Player = Mockito.mock(Player::class.java)
        val player2: Player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)

        // Mock moves 1 - 8 (4 moves by player)
        val move1 = Move(player1, game, 0, 0, 1, 0, LocalDateTime.of(2019, 3, 1, 14, 50, 58))
        val move2 = Move(player2, game, 0, 1, 2, 2, LocalDateTime.of(2019, 3, 1, 15, 11, 34))
        val move3 = Move(player1, game, 2, 2, 0, 0, LocalDateTime.of(2019, 3, 1, 15, 15, 32))
        val move4 = Move(player2, game, 0, 0, 2, 0, LocalDateTime.of(2019, 3, 1, 15, 23, 11))
        val move5 = Move(player1, game, 2, 0, 0, 1, LocalDateTime.of(2019, 3, 1, 15, 24, 44))
        val move6 = Move(player2, game, 0, 1, 0, 1, LocalDateTime.of(2019, 3, 1, 15, 26, 31))
        val move7 = Move(player1, game, 0, 1, 0, 0, LocalDateTime.of(2019, 3, 1, 15, 31, 1))
        val move8 = Move(player2, game, 0, 0, 1, 1, LocalDateTime.of(2019, 3, 1, 15, 31, 9))
        val move9 = Move(player1, game, 1, 1, 0, 0, LocalDateTime.of(2019, 3, 1, 15, 45, 39))
        val move10 = Move(player2, game, 0, 0, 0, 2, LocalDateTime.of(2019, 3, 1, 15, 54, 58))

        // Mock other method calls
        whenever(moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(player2, game, 0, 1)).thenReturn(listOf(move2, move6))
        whenever(moveRepository.findAllByPlayerAndGameAndBoardRowAndBoardColumn(player1, game, 0, 1)).thenReturn(listOf(move7))
        whenever(moveRepository.findAllByGameAndBoardRowAndBoardColumn(game, 0, 1)).thenReturn(listOf(move2, move6, move7))
        whenever(moveRepository.findFirstByGameOrderByCreatedDesc(game)).thenReturn(Optional.of(move10))

        assertThatExceptionOfType(Exception::class.java).isThrownBy { moveService.validateMoveInPermittedField(Move(player1, game, 0, 1, 0, 0, LocalDateTime.of(2019, 3, 1, 16, 1, 1, 1)), player1) }
        try {
            moveService.validateMoveInPermittedField(Move(player1, game, 0, 1, 0, 0, LocalDateTime.of(2019, 3, 1, 16, 1, 1, 1)), player1)
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("Invalid field. Your move has to be in field 1 - 3")
        }
    }

    @Test
    fun `Validate Turn Sequence positive`() {
        // Mock player and game
        val player1: Player = Mockito.mock(Player::class.java)
        val player2: Player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)

        // Mock moves 1 - 8 (4 moves by player)
        val move1 = Move(player1, game, 0, 0, 1, 0, LocalDateTime.of(2019, 3, 1, 14, 50, 58))
        val move2 = Move(player2, game, 0, 1, 2, 2, LocalDateTime.of(2019, 3, 1, 15, 11, 34))
        val move3 = Move(player1, game, 2, 2, 0, 0, LocalDateTime.of(2019, 3, 1, 15, 15, 32))
        val move4 = Move(player2, game, 0, 0, 2, 0, LocalDateTime.of(2019, 3, 1, 15, 23, 11))

        // Mock other method calls
        whenever(moveRepository.findFirstByGameOrderByCreatedDesc(game)).thenReturn(Optional.of(move4))
        whenever(move4.player!!.id).thenReturn("2")

        try {
            moveService.validateTurnSequence("1", game)
        } catch (e: Exception) {
            assertThat(e).doesNotThrowAnyException()
        }
    }

    @Test
    fun `Validate Turn Sequence negative`() {
        // Mock player and game
        val player1: Player = Mockito.mock(Player::class.java)
        val player2: Player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)

        // Mock moves 1 - 8 (4 moves by player)
        val move1 = Move(player1, game, 0, 0, 1, 0, LocalDateTime.of(2019, 3, 1, 14, 50, 58))
        val move2 = Move(player2, game, 0, 1, 2, 2, LocalDateTime.of(2019, 3, 1, 15, 11, 34))
        val move3 = Move(player1, game, 2, 2, 0, 0, LocalDateTime.of(2019, 3, 1, 15, 15, 32))

        // Mock other method calls
        whenever(moveRepository.findFirstByGameOrderByCreatedDesc(game)).thenReturn(Optional.of(move3))
        whenever(move3.player!!.id).thenReturn("1")

        try {
            moveService.validateTurnSequence("1", game)
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("It's not your turn. Please wait for the other Player to move.")
        }
    }

    @Test
    fun `Validate first Move Turn Sequence positive`() {
        // Mock player and game
        val game = Mockito.mock(Game::class.java)
        val player = Mockito.mock(Player::class.java)

        // Mock other method calls
        whenever(moveRepository.findFirstByGameOrderByCreatedDesc(game)).thenReturn(Optional.empty())
        whenever(game.firstPlayerPieceCode).thenReturn('X')
        whenever(game.firstPlayer).thenReturn(player)
        whenever(player.id).thenReturn("1")

        assertThatExceptionOfType(Exception::class.java).isThrownBy { moveService.validateTurnSequence("2", game) }
        try {
            moveService.validateTurnSequence("2", game)
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("It's not your turn. Please wait for the other Player to move.")
        }
    }

    @Test
    fun `Validate first Move Turn Sequence negative`() {
        // Mock player and game
        val game = Mockito.mock(Game::class.java)
        val player = Mockito.mock(Player::class.java)

        // Mock other method calls
        whenever(moveRepository.findFirstByGameOrderByCreatedDesc(game)).thenReturn(Optional.empty())
        whenever(game.firstPlayerPieceCode).thenReturn('X')
        whenever(game.firstPlayer).thenReturn(player)
        whenever(player.id).thenReturn("1")

        try {
            moveService.validateTurnSequence("1", game)
        } catch (e: Exception) {
            assertThat(e).doesNotThrowAnyException()
        }
    }

    @Test
    fun `Validate Valid Field positive`() {
        val player1: Player = Mockito.mock(Player::class.java)
        val player2: Player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)

        try {
            moveService.validatePossibleMove(Move(player1, game, 0, 1, 1, 2, LocalDateTime.of(2019, 3, 1, 14, 50, 58)))
        } catch (e: Exception) {
            assertThat(e).doesNotThrowAnyException()
        }
        try {
            moveService.validatePossibleMove(Move(player1, game, 0, 0, 0, 1, LocalDateTime.of(2019, 3, 1, 14, 50, 58)))
        } catch (e: Exception) {
            assertThat(e).doesNotThrowAnyException()
        }
    }

    @Test
    fun `Validate Valid Field negative`() {
        val player1: Player = Mockito.mock(Player::class.java)
        val player2: Player = Mockito.mock(Player::class.java)
        val game = Mockito.mock(Game::class.java)
        assertThatExceptionOfType(Exception::class.java).isThrownBy { moveService.validatePossibleMove(Move(player2, game, 1, 1, 3, 2, LocalDateTime.of(2019, 3, 1, 15, 11, 34))) }
        assertThatExceptionOfType(Exception::class.java).isThrownBy { moveService.validatePossibleMove(Move(player2, game, 5, 1, 2, 2, LocalDateTime.of(2019, 3, 1, 15, 11, 34))) }
        assertThatExceptionOfType(Exception::class.java).isThrownBy { moveService.validatePossibleMove(Move(player2, game, 0, 1, 2, -9, LocalDateTime.of(2019, 3, 1, 15, 11, 34))) }

    }
}