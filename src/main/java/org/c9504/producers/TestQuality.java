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
public class TestQuality {

    private final Logger logger = Logger.getLogger(TestQuality.class);

    @Inject
    @Channel("test-quality-out")
    Emitter<Record<Integer, Test>> emitter;

    public void sendTestQuality(Test test) {
        logger.infof("Test quality with Id: %s, State:  %s", test.getId().toString(), test.getState());
        test.setState("QUALITY");
        emitter.send(Record.of(Math.abs(new Random().nextInt()), test));
    }

}
