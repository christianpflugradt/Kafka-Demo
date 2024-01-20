package de.pflugradts.kafkademo

import de.pflugradts.kafkademo.ArithmeticOperationProducer.Mode.ADDITION
import de.pflugradts.kafkademo.ArithmeticOperationProducer.Mode.DIVISION
import de.pflugradts.kafkademo.ArithmeticOperationProducer.Mode.MULTIPLICATION
import de.pflugradts.kafkademo.ArithmeticOperationProducer.Mode.SUBTRACTION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.junit.jupiter.TestcontainersExtension
import org.testcontainers.shaded.org.awaitility.Awaitility
import org.testcontainers.shaded.org.hamcrest.CoreMatchers.equalTo
import org.testcontainers.shaded.org.hamcrest.Matcher
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import kotlin.random.Random

@Testcontainers
@ExtendWith(SpringExtension::class)
@SpringBootTest
@ContextConfiguration(initializers = [ApplicationInitializer::class])
class KafkaIntegrationTest {

    @Autowired
    private lateinit var arithmeticOperationProducer: ArithmeticOperationProducer

    @Autowired
    private lateinit var arithmeticOperationResultStore: ArithmeticOperationResultStore

    @Autowired
    private lateinit var arithmeticOperationErrorStore: ArithmeticOperationErrorStore

    @AfterEach
    fun clear() {
        arithmeticOperationResultStore.clear()
        arithmeticOperationErrorStore.clear()
    }

    @Test
    fun `should consume valid addition`() {
        // given / when
        arithmeticOperationProducer.produce(mode = ADDITION, operand1 = 9, operand2 = 3)

        // then
        await(equalTo(12)) { arithmeticOperationResultStore.getLatest()}
    }

    @Test
    fun `should consume valid subtraction`() {
        // given / when
        arithmeticOperationProducer.produce(mode = SUBTRACTION, operand1 = 9, operand2 = 3)

        // then
        await(equalTo(6)) { arithmeticOperationResultStore.getLatest()}
    }

    @Test
    fun `should consume valid multiplication`() {
        // given / when
        arithmeticOperationProducer.produce(mode = MULTIPLICATION, operand1 = 9, operand2 = 3)

        // then
        await(equalTo(27)) { arithmeticOperationResultStore.getLatest()}
    }

    @Test
    fun `should consume valid division`() {
        // given / when
        arithmeticOperationProducer.produce(mode = DIVISION, operand1 = 9, operand2 = 3)

        // then
        await(equalTo(3)) { arithmeticOperationResultStore.getLatest()}
    }

    @Test
    fun `should consume invalid division in dead letter queue`() {
        // given / when
        arithmeticOperationProducer.produce(mode = DIVISION, operand1 = 9, operand2 = 0)

        // then
        await(equalTo(true)) { arithmeticOperationErrorStore.hasErrors()}
    }

    @Test
    fun `should process many messages`() {
        // given
        val messageCount = 10_000

        // when
        runBlocking(Dispatchers.IO) {
            repeat(messageCount) {
                launch {
                    arithmeticOperationProducer.produce(
                        ArithmeticOperationProducer.Mode.entries[rnd(4)], rnd(1000), rnd(1000)
                    )
                }
            }
        }

        // then
        await(equalTo(messageCount)) { arithmeticOperationResultStore.count() + arithmeticOperationErrorStore.count()}
    }

    fun <T> await(expected: Matcher<T>, condition: () -> T) {
        Awaitility.await()
            .atMost(Duration.ofMillis(5000))
            .with()
            .pollInterval(Duration.ofMillis(100))
            .until(condition, expected)
    }

    fun rnd(until: Int) = Random.nextInt(until)

    companion object {
        @RegisterExtension
        internal val testContainersExtension = TestcontainersExtension()

        @JvmStatic
        @BeforeAll
        fun start() { kafkaContainer.start() }

        @JvmStatic
        @AfterAll
        fun stop() { kafkaContainer.stop() }
    }
}

class ApplicationInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(context: ConfigurableApplicationContext) {
        TestPropertyValues.of("spring.kafka.bootstrap-servers=" + kafkaContainer.bootstrapServers).applyTo(context)
    }
}

private val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.1"))
    .withCreateContainerCmdModifier { it.hostConfig?.withMemory(1024 * 1024 * 1024) }
    .withNetwork(Network.newNetwork())
