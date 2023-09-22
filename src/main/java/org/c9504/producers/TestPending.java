package org.c9504.producers;

import io.smallrye.reactive.messaging.kafka.Record;
import org.c9504.entities.Test;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Random;

@ApplicationScoped
public class TestPending {

    private final Logger logger = Logger.getLogger(TestPending.class);

    @Inject
    @Channel("test-pending-out")
    Emitter<Record<Integer, Test>> emitter;

    public void sendTestPending(Test test) {
        logger.infof("Test pending with Id: %s, State:  %s", test.getId().toString(), test.getState());
        emitter.send(Record.of(Math.abs(new Random().nextInt()), test));
    }

}
