package org.c9504.events;

import io.smallrye.mutiny.Multi;
import org.c9504.entities.Project;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("/events")
public class EventSource {

    //private final Logger logger = Logger.getLogger(EventSource.class);

    private Map<Integer, List<Project>> subscriptions = new ConcurrentHashMap<>();

    public Map<Integer, List<Project>> getSubscriptions() {
        return subscriptions;
    }

    @Inject
    @Channel("projects")
    Multi<Project> projects;

    @GET
    @Path("/projects/{id}")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<Project> streamProjects(@PathParam("id") int id/*, @Context SseEventSink eventSink, @Context Sse sse*/) {
        return projects.filter(project -> project.getId().equals(id));
    }

    @GET
    @Path("/projects")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<Project> streamProjects() {
        return projects;
    }
}
