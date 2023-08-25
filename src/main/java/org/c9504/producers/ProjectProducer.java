package org.c9504.producers;

import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.c9504.entities.Project;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ProjectProducer {

    @Inject
    @Channel("projects-out")
    Emitter<Record<Integer, String>> emitter;

    public void sendProject(Project project) {
        System.out.println("sent Project with ID: " + project.id + ", Value: " + project.value.toString());
        emitter.send(Record.of(project.id, project.value));
    }

}
