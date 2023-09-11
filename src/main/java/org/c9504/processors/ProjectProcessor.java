package org.c9504.processors;

import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.smallrye.reactive.messaging.kafka.Record;
import io.vertx.core.json.JsonObject;
import org.c9504.consumers.ProjectConsumer;
import org.c9504.entities.Project;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectProcessor {

    private final Logger logger = Logger.getLogger(ProjectProcessor.class);

    @Incoming("projects-in")
    @Outgoing("projects")
    @Broadcast
    public Project processProject(Record<Integer, String> project) {
        logger.infof("Got Process project: %d - %s", project.key(), project.value());
        JsonObject processor = new JsonObject(project.value().toString());
        processor.put("some", 100000000);
        return new Project(project.key(), processor.toString());
    }

}
