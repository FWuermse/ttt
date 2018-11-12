package com.flowcode.ttt.POJOs

import javax.persistence.*

@Entity
data class Player(
        @Id val id: String,
        val email: String,
        val name: String,
        val boardSkin: String,
        val pieceSkin: String,
        val gamesPlayed: Long,
        val gamesWon: Long
)
