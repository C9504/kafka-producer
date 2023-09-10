package org.c9504.consumers;

import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectConsumer {

    private final Logger logger = Logger.getLogger(ProjectConsumer.class);

    @Incoming("projects-in")
    public void receiveProject(Record<Integer, String> project) {
        logger.infof("Got Consumer project: %d - %s", project.key(), project.value());
    }
}
