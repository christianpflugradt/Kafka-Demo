package de.pflugradts.kafkademo

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.TopicBuilder

@ConfigurationProperties(prefix = "kafkademo")
data class KafkaTopicConfiguration(val topic: String) {

    @Bean
    fun arithmeticOperationTopic() = TopicBuilder.name(topic).partitions(1).replicas(1).build()

}
