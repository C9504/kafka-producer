package org.c9504.consumers;

import io.smallrye.reactive.messaging.kafka.Record;
import io.vertx.core.Vertx;
import org.c9504.entities.Test;
import org.c9504.producers.TestPending;
import org.c9504.producers.TestQuality;
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
public class TestPendingConsumer {

    private final Logger logger = Logger.getLogger(TestPendingConsumer.class);

    @Inject
    TestQuality testQuality;

    @Inject
    TestService testService;

    @Inject
    TestRepository testRepository;

    @Inject
    Vertx vertx;

    @Incoming("test-pending-in")
    @Transactional
    @Retry(delay = 10, maxRetries = 5)
    public CompletionStage<Void> receiveTestPending(Message<Test> test) {
        UUID id = test.getPayload().getId();
        Test toQuality = testRepository.findById(id);
        if (toQuality != null) {
            toQuality.setState("QUALITY");
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            testService.saveTest(toQuality);
            vertx.executeBlocking(future -> {
                testQuality.sendTestQuality(toQuality);
                future.complete();
            }, false, asyncResult -> {
                if (asyncResult.succeeded()) {
                    logger.infof("Got Consumer Pending ID: %s, State: %s", toQuality.getId().toString(), toQuality.getState());
                    System.out.println("COMMITED TO QUALITY");
                }
            });
            return test.ack();
        }
        return test.nack(new Throwable("NO ACKED").getCause());
    }

}
