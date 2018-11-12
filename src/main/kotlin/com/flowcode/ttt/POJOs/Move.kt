package com.flowcode.ttt.POJOs

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Move(
        @ManyToOne(cascade = [CascadeType.ALL]) @JoinColumn val player: Player,
        @ManyToOne(cascade = [CascadeType.ALL]) @JoinColumn val game: Game,
        val boardNumber: Char,
        val boardRow: Int,
        val boardColumn: Int,
        @Id @GeneratedValue val id: Long? = null,
        val created: LocalDateTime = LocalDateTime.now()
)
