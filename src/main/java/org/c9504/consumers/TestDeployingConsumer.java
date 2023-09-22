package org.c9504.consumers;

import io.smallrye.reactive.messaging.kafka.Record;
import io.vertx.core.Vertx;
import org.c9504.entities.Test;
import org.c9504.producers.TestDeploying;
import org.c9504.repositories.TestRepository;
import org.c9504.services.TestService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class TestDeployingConsumer {

    private final Logger logger = Logger.getLogger(TestDeployingConsumer.class);

    @Inject
    TestDeploying testDeploying;

    @Inject
    TestService testService;

    @Inject
    TestRepository testRepository;

    @Inject
    Vertx vertx;

    @Incoming("test-deploying-in")
    @Transactional
    public void receiveTestPending(Record<Integer, Test> test) {
        UUID id = test.value().getId();
        Test toDeploying = testRepository.findById(id);
        if (toDeploying != null) {
            toDeploying.setState("DEPLOYED");
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            testService.saveTest(toDeploying);
            logger.infof("Got Consumer Deploying ID: %s, State: %s", toDeploying.getId().toString(), toDeploying.getState());
        }
    }

}
