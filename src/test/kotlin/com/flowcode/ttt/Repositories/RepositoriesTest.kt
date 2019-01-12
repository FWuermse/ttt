package com.flowcode.ttt.Repositories

import com.flowcode.ttt.POJOs.Game
import com.flowcode.ttt.POJOs.Move
import com.flowcode.ttt.POJOs.Player
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DataJpaTest
class RepositoriesTest (@Autowired val entityManager: TestEntityManager,
                           @Autowired val playerRepository: PlayerRepository,
                           @Autowired val gameRepository: GameRepository,
                           @Autowired val moveRepository: MoveRepository) {

    @Test
    fun `Return Game by GameId`() {
        val florian = Player("1","wuermseer.florian@gmail.com", "Florian", "0", "0", 0,0)
        val ada = Player("2","ada.lovelace@gmail.com", "Alo", "20", "2", 10,10)
        entityManager.persist(florian)
        entityManager.persist(ada)
        val game = Game(florian,ada,"pending", "ranked", 'X')
        entityManager.persist(game)
        entityManager.flush()

        val found = gameRepository.findById(game.id!!)

        Assertions.assertThat(found.get()).isEqualTo(game)
    }

    @Test
    fun `Return User by UserId`() {
        val florian = Player("1","wuermseer.florian@gmail.com", "Florian", "0", "0", 0,0)
        entityManager.persist(florian)
        entityManager.flush()

        val found = playerRepository.findById(florian.id)

        Assertions.assertThat(found.get()).isEqualTo(florian)
    }

    @Test
    fun `Return Move by MoveId`() {
        val florian = Player("1","wuermseer.florian@gmail.com", "Florian", "0", "0", 0,0)
        val ada = Player("2","ada.lovelace@gmail.com", "Alo", "20", "2", 10,10)
        entityManager.persist(florian)
        entityManager.persist(ada)
        val game = Game(florian,ada,"pending", "ranked", 'X')
        entityManager.persist(game)
        val move = Move(florian, game, 0, 0, 2, 1)
        entityManager.persist(move)
        entityManager.flush()

        val found = moveRepository.findById(move.id!!)

        Assertions.assertThat(found.get()).isEqualTo(move)
    }

    @Test
    fun `Return Games by UserId`() {
        val florian = Player("1","wuermseer.florian@gmail.com", "Florian", "0", "0", 0,0)
        val ada = Player("2","ada.lovelace@gmail.com", "Alo", "20", "2", 10,10)
        val max = Player("3","max.mustermann@gmail.com", "Maxi", "0", "5", 10,1)
        entityManager.persist(florian)
        entityManager.persist(ada)
        val game = Game(florian,ada,"pending", "ranked", 'X')
        val game2 = Game(max,florian,"pending", "ranked", 'X')
        entityManager.persist(game)
        entityManager.persist(game2)
        entityManager.flush()

        val found = gameRepository.findAllByFirstPlayerOrSecondPlayer(florian, florian)

        Assertions.assertThat(found.size).isEqualTo(2)
    }

    @Test
    fun `Returns Moves by Player and Game`() {
        val florian = Player("1","wuermseer.florian@gmail.com", "Florian", "0", "0", 0,0)
        val ada = Player("2","ada.lovelace@gmail.com", "Alo", "20", "2", 10,10)
        entityManager.persist(florian)
        entityManager.persist(ada)
        val game = Game(florian,ada,"pending", "ranked", 'X')
        val game2 = Game(ada,florian,"pending", "ranked", 'X')
        entityManager.persist(game)
        val move1 = Move(florian, game, 0, 0, 2, 1)
        val move2 = Move(ada, game, 1, 1, 3, 3)
        val move3 = Move(florian, game, 2, 2, 1, 1)
        val move4 = Move(ada, game2, 1, 1, 3, 3)
        val move5 = Move(florian, game2, 2, 2, 1, 1)
        entityManager.persist(move1)
        entityManager.persist(move2)
        entityManager.persist(move3)
        entityManager.persist(move4)
        entityManager.persist(move5)
        entityManager.flush()

        val found = moveRepository.findAllByPlayerAndGame(florian, game)
        val foundSecond = moveRepository.findAllByPlayerAndGame(florian, game2)

        Assertions.assertThat(found.size).isEqualTo(2)
        Assertions.assertThat(foundSecond.size).isEqualTo(1)
    }

    @Test
    fun `Returns Moves by Game`() {
        val florian = Player("1","wuermseer.florian@gmail.com", "Florian", "0", "0", 0,0)
        val ada = Player("2","ada.lovelace@gmail.com", "Alo", "20", "2", 10,10)
        entityManager.persist(florian)
        entityManager.persist(ada)
        val game = Game(florian,ada,"pending", "ranked", 'X')
        val game2 = Game(ada,florian,"pending", "ranked", 'X')
        entityManager.persist(game)
        val move1 = Move(florian, game, 0, 0, 2, 1)
        val move2 = Move(ada, game, 1, 1, 3, 3)
        val move3 = Move(florian, game, 2, 2, 1, 1)
        val move4 = Move(ada, game2, 1, 1, 3, 3)
        val move5 = Move(florian, game2, 2, 2, 1, 1)
        entityManager.persist(move1)
        entityManager.persist(move2)
        entityManager.persist(move3)
        entityManager.persist(move4)
        entityManager.persist(move5)
        entityManager.flush()

        val found = moveRepository.findAllByGame(game)
        val foundSecond = moveRepository.findAllByGame(game2)

        Assertions.assertThat(found.size).isEqualTo(3)
        Assertions.assertThat(foundSecond.size).isEqualTo(2)
    }

    @Test
    fun `Returns Players by PlayerName`() {
        val florian = Player("1","wuermseer.florian@gmail.com", "Florian", "0", "0", 0,0)
        val ada = Player("2","ada.lovelace@gmail.com", "Alo", "20", "2", 10,10)
        val max = Player("3","max.mustermann@gmail.com", "Maxi", "0", "5", 10,1)
        entityManager.persist(florian)
        entityManager.persist(ada)
        entityManager.persist(max)
        entityManager.flush()

        val found: List<Player> = playerRepository.findAllByNameContainingIgnoreCase("Florian")

        Assertions.assertThat(found.size).isEqualTo(1)
    }

    @Test
    fun `Returns Players by part of PlayerName`() {
        val florian = Player("1","wuermseer.florian@gmail.com", "Florian", "0", "0", 0,0)
        val ada = Player("2","ada.lovelace@gmail.com", "Alo", "20", "2", 10,10)
        val max = Player("3","max.mustermann@gmail.com", "Maxi", "0", "5", 10,1)
        entityManager.persist(florian)
        entityManager.persist(ada)
        entityManager.persist(max)
        entityManager.flush()

        val found: List<Player> = playerRepository.findAllByNameContainingIgnoreCase("a")

        Assertions.assertThat(found.size).isEqualTo(3)
    }

    @Test
    fun `Returns Players by lower case PlayerName`() {
        val florian = Player("1","wuermseer.florian@gmail.com", "Florian", "0", "0", 0,0)
        val ada = Player("2","ada.lovelace@gmail.com", "Alo", "20", "2", 10,10)
        val max = Player("3","max.mustermann@gmail.com", "Maxi", "0", "5", 10,1)
        entityManager.persist(florian)
        entityManager.persist(ada)
        entityManager.persist(max)
        entityManager.flush()

        val found: List<Player> = playerRepository.findAllByNameContainingIgnoreCase("florian")

        Assertions.assertThat(found.size).isEqualTo(1)
    }
}
