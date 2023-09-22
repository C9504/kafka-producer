package org.c9504.deserializers;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.c9504.entities.Test;

public class TestDeserializer extends ObjectMapperDeserializer<Test> {

    public TestDeserializer() {
        super(Test.class);
    }
}
