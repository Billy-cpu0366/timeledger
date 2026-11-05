package com.example.util.simpletimetracker.domain.parser

import com.example.util.simpletimetracker.domain.parser.model.ParseRequest
import com.example.util.simpletimetracker.domain.parser.model.ParseResult

/**
 * 把一段自然语言变成结构化记录。
 *
 * 这是契约，不是实现。DeepSeek 只是其中一个实现，
 * 将来换模型只需新增实现类，调用方一行不改。
 */
interface ActivityParser {

    suspend fun parse(request: ParseRequest): ParseResult
}
