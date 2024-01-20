package de.pflugradts.kafkademo

import org.springframework.stereotype.Component

@Component
class ArithmeticOperationResultStore {
    private val resultList = mutableListOf<Int>()
    fun add(result: Int) = resultList.add(result)
    fun getLatest() = resultList.lastOrNull()
    fun count() = resultList.size
    fun clear() = resultList.clear()
}
