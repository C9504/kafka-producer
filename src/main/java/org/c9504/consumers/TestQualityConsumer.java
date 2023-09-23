package org.c9504.consumers;

import io.smallrye.reactive.messaging.kafka.Record;
import io.vertx.core.Vertx;
import org.c9504.entities.Test;
import org.c9504.producers.TestDeploying;
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
import java.util.UUID;
import java.util.concurrent.CompletionStage;

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
    @Retry(delay = 10, maxRetries = 5)
    public CompletionStage<Void> receiveTestDeploying(Message<Test> test) {
        UUID id = test.getPayload().getId();
        Test toDeploy = testRepository.findById(id);
        if (toDeploy != null) {
            toDeploy.setState("QUALITY");
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            testService.saveTest(toDeploy);
            vertx.executeBlocking(future -> {
                testDeploying.sendTestDeploying(toDeploy);
                future.complete();
            }, false, asyncResult -> {
                if (asyncResult.succeeded()) {
                    logger.infof("Got Consumer Quality ID: %s, State: %s", toDeploy.getId().toString(), toDeploy.getState());
                    System.out.println("COMMITED TO DEPLOY");
                }
            });
            return test.ack();
        }
        return test.nack(new Throwable("NOT ACKED").getCause());
    }
}
