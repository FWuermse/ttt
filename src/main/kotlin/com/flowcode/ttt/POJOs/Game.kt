package com.flowcode.ttt.POJOs

import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotEmpty

@Entity
data class Game(
        @NotEmpty @ManyToOne(cascade = [CascadeType.ALL]) @JoinColumn val firstPlayer: Player,
        @ManyToOne(cascade = [CascadeType.ALL]) @JoinColumn val secondPlayer: Player,
        val status: String,
        val type: String,
        val firstPlayerPieceCode: Char,
        @Id @GeneratedValue val id: Long? = null,
        val created: LocalDateTime = LocalDateTime.now()
)
