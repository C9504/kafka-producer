package org.c9504.producers;

import io.smallrye.reactive.messaging.kafka.Record;
import org.c9504.consumers.ProjectConsumer;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.c9504.entities.Project;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ProjectProducer {

    private final Logger logger = Logger.getLogger(ProjectProducer.class);

    @Inject
    @Channel("projects-out")
    Emitter<Record<Integer, String>> emitter;

    public void sendProject(Project project) {
        logger.infof("sent Project with ID: %d, Value:  %s", project.id, project.value.toString());
        emitter.send(Record.of(project.id, project.value));
    }

}
