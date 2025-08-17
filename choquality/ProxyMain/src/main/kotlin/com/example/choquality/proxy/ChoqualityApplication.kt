package com.example.choquality.proxy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = [
    "com.example.choquality.common"
    ,"com.example.choquality.todo"
    ,"com.example.choquality.proxy"
])
class ChoqualityApplication

fun main(args: Array<String>) {
    runApplication<ChoqualityApplication>(*args)
}
