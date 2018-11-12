package com.flowcode.ttt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TttApplication

fun main(args: Array<String>) {
    runApplication<TttApplication>(*args)
}
