package debezium.wal.streaming.bug.repro

import io.debezium.config.Configuration
import io.debezium.connector.postgresql.PostgresConnectorConfig
import io.debezium.embedded.EmbeddedEngine
import org.apache.log4j.BasicConfigurator
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private lateinit var executor: ExecutorService

fun main() {
    BasicConfigurator.configure();
    val config = Configuration.create()
        .with(EmbeddedEngine.CONNECTOR_CLASS, "io.debezium.connector.postgresql.PostgresConnector")
        .with(EmbeddedEngine.OFFSET_STORAGE, "org.apache.kafka.connect.storage.MemoryOffsetBackingStore")
        .with(EmbeddedEngine.ENGINE_NAME, "postgres-connector")
        .with(PostgresConnectorConfig.SERVER_NAME, "myserver")
        .with(PostgresConnectorConfig.SCHEMA_WHITELIST, "public")
        .with(PostgresConnectorConfig.PLUGIN_NAME, PostgresConnectorConfig.LogicalDecoder.WAL2JSON_STREAMING.value)
        .with(PostgresConnectorConfig.HOSTNAME, "localhost")
        .with(PostgresConnectorConfig.PORT, 5432)
        .with(PostgresConnectorConfig.DATABASE_NAME, "postgres")
        .with(PostgresConnectorConfig.USER, "postgres")
        .with(PostgresConnectorConfig.PASSWORD, "postgres")
        .with(PostgresConnectorConfig.SLOT_NAME, "ferit")
        .build()

    val engine = EmbeddedEngine.BuilderImpl()
        .using(config)
        .notifying { sourceRecord ->
            println(sourceRecord.toString())
        }
        .build()

    startEngine(engine)
}

fun startEngine(engine: EmbeddedEngine): EmbeddedEngine {
    executor = Executors.newSingleThreadScheduledExecutor()
    executor.execute(engine)
    return engine
}

fun stopEngine(engine: EmbeddedEngine) {
    try {
        println("shutting down")
        engine.stop()
        engine.await(10, TimeUnit.SECONDS)
        executor.shutdown()
        while (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            println("Waiting another 10 seconds for the embedded engine to complete")
        }
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
    }
}