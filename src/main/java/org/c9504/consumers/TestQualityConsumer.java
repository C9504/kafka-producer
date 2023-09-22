package org.c9504.consumers;

import io.smallrye.reactive.messaging.kafka.Record;
import io.vertx.core.Vertx;
import org.c9504.entities.Test;
import org.c9504.producers.TestDeploying;
import org.c9504.producers.TestQuality;
import org.c9504.repositories.TestRepository;
import org.c9504.services.TestService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class TestQualityConsumer {

    private final Logger logger = Logger.getLogger(TestQualityConsumer.class);
    
    @Inject
    Vertx vertx;

    @Inject
    TestDeploying testDeploying;

    @Inject
    TestRepository testRepository;

    @Inject
    TestService testService;

    @Incoming("test-quality-in")
    @Transactional
    public void receiveTestDeploying(Record<Integer, Test> test) {
        UUID id = test.value().getId();
        Test toDeploy = testRepository.findById(id);
        toDeploy.setState("QUALITY");
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        testService.saveTest(toDeploy);
        vertx.executeBlocking(future -> {
            testDeploying.sendTestDeploying(toDeploy);
            logger.infof("Got Consumer Quality ID: %s, State: %s", toDeploy.getId().toString(), toDeploy.getState());
            future.complete();
        }, false, asyncResult -> {
            if (asyncResult.succeeded()) {
                System.out.println("COMMITED TO DEPLOY");
            }
        });
    }
}
