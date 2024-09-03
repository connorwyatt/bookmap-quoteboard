package com.github.connorwyatt.quoteboard.extensions

inline fun <T> List<T>.intersperse(item: () -> T): List<T> =
    this.flatMapIndexed { index, element ->
        if (index == 0) listOf(element) else listOf(item(), element)
    }
