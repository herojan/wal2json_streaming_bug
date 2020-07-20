# Bug in wal2json_streaming plugin

The wal2json_streaming plugin throws a null pointer exception when a DDL statement is ran.

Steps to reproduce:

1. Start postgres container

    ```bash
    export PGPASSWORD=POSTGRES
    docker run -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d --rm debezium/postgres:10.0
    ```
1. Start app

    ```bash
    ./gradlew clean run
    ``` 
1. Open a new terminal tab and connect psql with 

    ```bash
    export PGPASSWORD='postgres'
    psql -h localhost -p 5432 -U postgres
    ```
1. Create a table

    ```postgresql
    create table my_table
    (
        id      serial  not null primary key,
        col_one varchar not null,
        col_two varchar not null
    );
    ```
1. It will explode with a null pointer

    ```text
    org.apache.kafka.connect.errors.ConnectException: An exception occurred in the change event producer. This connector will be stopped.
            at io.debezium.pipeline.ErrorHandler.setProducerThrowable(ErrorHandler.java:42)
            at io.debezium.connector.postgresql.PostgresStreamingChangeEventSource.execute(PostgresStreamingChangeEventSource.java:197)
            at io.debezium.pipeline.ChangeEventSourceCoordinator.lambda$start$0(ChangeEventSourceCoordinator.java:101)
            at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515)
            at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
            at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
            at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
            at java.base/java.lang.Thread.run(Thread.java:834)
    Caused by: java.lang.NullPointerException
            at io.debezium.connector.postgresql.PostgresStreamingChangeEventSource.lambda$execute$0(PostgresStreamingChangeEventSource.java:128)
            at io.debezium.connector.postgresql.connection.wal2json.StreamingWal2JsonMessageDecoder.doProcessMessage(StreamingWal2JsonMessageDecoder.java:259)
            at io.debezium.connector.postgresql.connection.wal2json.StreamingWal2JsonMessageDecoder.nonInitialChunk(StreamingWal2JsonMessageDecoder.java:185)
            at io.debezium.connector.postgresql.connection.wal2json.StreamingWal2JsonMessageDecoder.processNotEmptyMessage(StreamingWal2JsonMessageDecoder.java:158)
            at io.debezium.connector.postgresql.connection.AbstractMessageDecoder.processMessage(AbstractMessageDecoder.java:33)
            at io.debezium.connector.postgresql.connection.PostgresReplicationConnection$1.deserializeMessages(PostgresReplicationConnection.java:417)
            at io.debezium.connector.postgresql.connection.PostgresReplicationConnection$1.readPending(PostgresReplicationConnection.java:410)
            at io.debezium.connector.postgresql.PostgresStreamingChangeEventSource.execute(PostgresStreamingChangeEventSource.java:125)
    ```