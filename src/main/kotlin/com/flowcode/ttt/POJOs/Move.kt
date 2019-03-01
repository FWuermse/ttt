package com.flowcode.ttt.POJOs

import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Move(
        @ManyToOne(cascade = [CascadeType.ALL]) @JoinColumn val player: Player,
        @ManyToOne(cascade = [CascadeType.ALL]) @JoinColumn val game: Game,
        val boardRow: Int,
        val boardColumn: Int,
        val fieldRow: Int,
        val fieldColumn: Int,
        val created: LocalDateTime,
        @Id @GeneratedValue val id: Long? = null
)
