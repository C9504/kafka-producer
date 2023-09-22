package org.c9504.consumers;

import io.smallrye.reactive.messaging.kafka.Record;
import org.c9504.entities.Test;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestDeployingConsumer {

    private final Logger logger = Logger.getLogger(TestDeployingConsumer.class);

    @Incoming("test-deploying-in")
    public void receiveTestPending(Record<Integer, Test> test) {
        logger.infof("Got Consumer Deploying ID: %s, State: %s", test.value().getId().toString(), test.value().getState());
    }

}
