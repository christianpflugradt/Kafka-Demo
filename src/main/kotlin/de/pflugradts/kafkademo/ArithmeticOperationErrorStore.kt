package de.pflugradts.kafkademo

import org.springframework.stereotype.Component

@Component
class ArithmeticOperationErrorStore {
    private val errorList = mutableListOf<ArithmeticOperation>()
    fun add(erroneousOperation: ArithmeticOperation) = errorList.add(erroneousOperation)
    fun hasErrors() = errorList.isNotEmpty()
    fun count() = errorList.size
    fun clear() = errorList.clear()
}
