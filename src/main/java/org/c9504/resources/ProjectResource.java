package org.c9504.resources;

import io.vertx.core.json.JsonObject;
import org.c9504.entities.Project;
import org.c9504.producers.ProjectProducer;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Random;

@Path("projects")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProjectResource {

    @Inject
    ProjectProducer producer;

    @GET
    public Response send() {
        for (int i = 0; i < 3000; i++) {
            Random random = new Random();
            Project project = new Project();
            project.id = Math.abs(random.nextInt());
            JsonObject value = new JsonObject();
            value.put("sales", random.nextInt());
            value.put("customers", random.nextInt());
            value.put("products", random.nextInt());
            value.put("sellers", random.nextInt());
            project.value = value.toString();
            producer.sendProject(project);
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return Response.accepted().build();
    }
}
