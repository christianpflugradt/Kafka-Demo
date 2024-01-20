package de.pflugradts.kafkademo

import de.pflugradts.kafkademo.ArithmeticOperationProducer.Mode.ADDITION
import de.pflugradts.kafkademo.ArithmeticOperationProducer.Mode.DIVISION
import de.pflugradts.kafkademo.ArithmeticOperationProducer.Mode.MULTIPLICATION
import de.pflugradts.kafkademo.ArithmeticOperationProducer.Mode.SUBTRACTION
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component


@Component
class ArithmeticOperationProducer(
    val kafkaTemplate: KafkaTemplate<String, ArithmeticOperation>,
    val kafkaTopicConfiguration: KafkaTopicConfiguration,
) {

    enum class Mode { ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION }

    fun produce(mode: Mode, operand1: Int, operand2: Int) {
        kafkaTemplate.send(
            kafkaTopicConfiguration.topic,
            when(mode) {
                ADDITION -> Addition(operand1, operand2)
                SUBTRACTION -> Subtraction(operand1, operand2)
                MULTIPLICATION -> Multiplication(operand1, operand2)
                DIVISION -> Division(operand1, operand2)
            },
        ).whenComplete { _, ex -> ex.printStackTrace() }
    }

}
