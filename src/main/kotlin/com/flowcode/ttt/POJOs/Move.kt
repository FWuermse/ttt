package com.flowcode.ttt.POJOs

import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Move(
        @ManyToOne @JoinColumn val player: Player?,
        @ManyToOne @JoinColumn val game: Game,
        val boardRow: Int,
        val boardColumn: Int,
        val fieldRow: Int,
        val fieldColumn: Int,
        val created: LocalDateTime?,
        @Id @GeneratedValue val id: Long? = null
)
