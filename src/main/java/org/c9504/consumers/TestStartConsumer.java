package org.c9504.consumers;

import io.smallrye.reactive.messaging.kafka.Record;
import io.vertx.core.Vertx;
import org.c9504.entities.Test;
import org.c9504.producers.TestPending;
import org.c9504.repositories.TestRepository;
import org.c9504.services.TestService;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

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
    @Retry(delay = 10, maxRetries = 5)
    public CompletionStage<Void> receiveTestStarted(Message<Test> test) {
        UUID id = test.getPayload().getId();
        Test toPending = testRepository.findById(id);
        if (toPending != null) {
            toPending.setState("PENDING");
            testService.saveTest(toPending);
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            vertx.executeBlocking(future -> {
                testPending.sendTestPending(toPending);
                future.complete();
            }, false, asyncResult -> {
                if (asyncResult.succeeded()) {
                    System.out.println("COMMITED");
                    logger.infof("Got Consumer Start ID: %s, State: %s", toPending.getId().toString(), toPending.getState());
                }
            });
            return test.ack();
        }
        return test.nack(new Throwable("NOT ACKED").getCause());
    }

}
