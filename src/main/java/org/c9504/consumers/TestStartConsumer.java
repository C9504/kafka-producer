package org.c9504.consumers;

import io.smallrye.reactive.messaging.kafka.Record;
import org.c9504.entities.Test;
import org.c9504.producers.TestPending;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TestStartConsumer {

    private final Logger logger = Logger.getLogger(TestStartConsumer.class);

    @Inject
    TestPending testPending;

    @Incoming("test-start-in")
    public void receiveTestStarted(Record<Integer, Test> test) {
        logger.infof("Got Consumer Start ID: %s, State: %s", test.value().getId().toString(), test.value().getState());
        testPending.sendTestPending(test.value());
    }

}
