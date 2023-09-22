package org.c9504.consumers;

import io.smallrye.reactive.messaging.kafka.Record;
import io.vertx.core.Vertx;
import org.c9504.entities.Test;
import org.c9504.producers.TestPending;
import org.c9504.repositories.TestRepository;
import org.c9504.services.TestService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class TestStartConsumer {

    private final Logger logger = Logger.getLogger(TestStartConsumer.class);

    @Inject
    TestPending testPending;

    @Inject
    TestService testService;

    @Inject
    TestRepository testRepository;

    @Inject
    Vertx vertx;

    @Incoming("test-start-in")
    @Transactional
    public void receiveTestStarted(Record<Integer, Test> test) {
        UUID id = test.value().getId();
        Test toPending = testRepository.findById(id);
        toPending.setState("PENDING");
        testService.saveTest(toPending);
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        vertx.executeBlocking(future -> {
            testPending.sendTestPending(toPending);
            logger.infof("Got Consumer Start ID: %s, State: %s", toPending.getId().toString(), toPending.getState());
            future.complete();
        }, false, asyncResult -> {
            if (asyncResult.succeeded()) {
                System.out.println("COMMITED");
            }
        });
    }

}
