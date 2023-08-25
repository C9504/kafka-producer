package org.negrdo.producers;

import io.smallrye.reactive.messaging.kafka.Record;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.negrdo.entities.Project;

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
