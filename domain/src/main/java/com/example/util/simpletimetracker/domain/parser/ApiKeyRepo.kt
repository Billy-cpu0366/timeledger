package com.example.util.simpletimetracker.domain.parser

interface ApiKeyRepo {

    fun get(): String?

    fun set(key: String)

    fun clear()
}
