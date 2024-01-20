# Kafka-Demo

This example demonstrates how to interact with Apache Kafka using Spring Kafka by:
- running a Kafka instance through Testcontainers library
- creating a topic programmatically
- sending serialized instances of multiple classes implementing a common interface to Kafka
- receiving and deserializing instances of the same classes from Kafka
- configuring a dead letter topic to handle unprocessable messages 

## Usage

Clone this repo and run the tests in [KafkaIntegrationTest.kt](src/test/kotlin/de/pflugradts/kafkademo/KafkaIntegrationTest.kt).

You can easily add tests for other use cases such as log compaction. Before running the tests you might want to update the [dependencies](build.gradle.kts) to the latest versions.
