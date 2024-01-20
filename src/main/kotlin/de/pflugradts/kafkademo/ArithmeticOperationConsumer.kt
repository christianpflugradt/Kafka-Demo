package de.pflugradts.kafkademo

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.retrytopic.DltStrategy
import org.springframework.stereotype.Component

@Component
class ArithmeticOperationConsumer(
    val arithmeticOperationResultStore: ArithmeticOperationResultStore,
    val arithmeticOperationErrorStore: ArithmeticOperationErrorStore,
) {

    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR)
    @KafkaListener(topics = ["#{'\${kafkademo.topic}'}"], groupId = "kafkademo")
    fun consume(consumerRecord: ConsumerRecord<Any, ArithmeticOperation>) {
        with(consumerRecord.value()) {
            try {
                arithmeticOperationResultStore.add(calculateResult())
            } catch (ex: ArithmeticException) {
                handleErroneousArithmeticOperation(this)
            }
        }
    }

    @DltHandler
    fun handleErroneousArithmeticOperation(arithmeticOperation: ArithmeticOperation) {
        arithmeticOperationErrorStore.add(arithmeticOperation)
    }

}
