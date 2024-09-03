package com.github.connorwyatt.quoteboard

import io.reactivex.rxjava3.core.Observable

fun <T : Any> Observable<T>.withPrevious(): Observable<WithPreviousResult<T>> {
    return this.scan(IntermediateWithPreviousResult<T>()) { previousResult, current ->
            IntermediateWithPreviousResult(current, previousResult.current)
        }
        .filter { it.current != null }
        .map { WithPreviousResult(it.current!!, it.previous) }
}

data class WithPreviousResult<T : Any>(val current: T, val previous: T? = null)

private data class IntermediateWithPreviousResult<T : Any>(
    val current: T? = null,
    val previous: T? = null,
)
