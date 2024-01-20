package de.pflugradts.kafkademo

interface ArithmeticOperation {
    val operator: Char
    var operand1: Int
    var operand2: Int
    fun calculateResult(): Int
}

data class Addition(override var operand1: Int = -1, override var operand2: Int = -1) : ArithmeticOperation {
    override val operator = '+'
    override fun calculateResult() = operand1 + operand2
}

data class Subtraction(override var operand1: Int = -1, override var operand2: Int = -1) : ArithmeticOperation {
    override val operator = '-'
    override fun calculateResult() = operand1 - operand2
}

data class Multiplication(override var operand1: Int = -1, override var operand2: Int = -1) : ArithmeticOperation {
    override val operator = '*'
    override fun calculateResult() = operand1 * operand2
}

data class Division(override var operand1: Int = -1, override var operand2: Int = -1) : ArithmeticOperation {
    override val operator = '/'
    override fun calculateResult() = operand1 / operand2
}
