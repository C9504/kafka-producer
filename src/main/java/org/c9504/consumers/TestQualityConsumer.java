package org.c9504.consumers;

import io.smallrye.reactive.messaging.kafka.Record;
import org.c9504.entities.Test;
import org.c9504.producers.TestDeploying;
import org.c9504.producers.TestQuality;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TestQualityConsumer {

    private final Logger logger = Logger.getLogger(TestQualityConsumer.class);

    @Inject
    TestDeploying testDeploying;

    @Incoming("test-quality-in")
    public void receiveTestDeploying(Record<Integer, Test> test) {
        logger.infof("Got Consumer Pending ID: %s, State: %s", test.value().getId().toString(), test.value().getState());
        testDeploying.sendTestDeploying(test.value());
    }
}
