package org.c9504.consumers;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import io.smallrye.reactive.messaging.kafka.Record;
import io.vertx.core.Vertx;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.c9504.entities.Test;
import org.c9504.producers.TestDeploying;
import org.c9504.repositories.TestRepository;
import org.c9504.services.TestService;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

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
    @Retry(delay = 10, maxRetries = 5)
    public CompletionStage<Void> receiveTestPending(Message<Test> test) {
        UUID id = test.getPayload().getId();
        Test toDeploying = testRepository.findById(id);
        if (toDeploying != null) {
            toDeploying.setState("DEPLOYED");
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            testService.saveTest(toDeploying);
            logger.infof("Got Consumer Deploying ID: %s, State: %s", toDeploying.getId().toString(), toDeploying.getState());
            return test.ack();
        }
        return test.nack(new Throwable("NOT ACKED").getCause());
    }

}
