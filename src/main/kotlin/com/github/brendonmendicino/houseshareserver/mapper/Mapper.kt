package com.github.brendonmendicino.houseshareserver.mapper

interface Mapper<IN, OUT> {
    fun map(it: IN): OUT
}