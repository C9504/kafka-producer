package org.c9504.consumers;

import io.smallrye.reactive.messaging.kafka.Record;
import org.c9504.entities.Test;
import org.c9504.producers.TestPending;
import org.c9504.producers.TestQuality;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TestPendingConsumer {

    private final Logger logger = Logger.getLogger(TestPendingConsumer.class);

    @Inject
    TestQuality testQuality;

    @Incoming("test-pending-in")
    public void receiveTestPending(Record<Integer, Test> test) {
        logger.infof("Got Consumer Pending ID: %s, State: %s", test.value().getId().toString(), test.value().getState());
        testQuality.sendTestQuality(test.value());
    }

}
