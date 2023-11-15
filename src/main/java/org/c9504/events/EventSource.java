package org.c9504.events;

import io.smallrye.mutiny.Multi;
import org.c9504.entities.Project;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestStreamElementType;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Path("/events")
public class EventSource {

    private final Logger logger = Logger.getLogger(EventSource.class);

    private final Map<String, SseEventSink> subscriptions = new ConcurrentHashMap<>();

    private final AtomicLong counter = new AtomicLong(0);

    @Inject
    @Channel("projects")
    Multi<Project> projects;

    @GET
    @Path("/projects/{id}")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<Project> streamProjectsBySession(@PathParam("id") int id, @Context SseEventSink eventSink, @Context Sse sse) {
        long started = System.currentTimeMillis();
        try {
            subscriptions.put(String.valueOf(id), eventSink);
            final Long invocationNumber = counter.getAndIncrement();
            logger.infof("EventSource#streamProjectsBySession() invocation #%d returning successfully | #%d timed out after %d ms", invocationNumber, invocationNumber, System.currentTimeMillis() - started);
            return projects.filter(project -> project.getId().equals(id))
                    .onTermination()
                    .invoke(() -> {
                        subscriptions.remove(String.valueOf(id));
                        logger.info("Session closed");
                    })
                    .onCancellation()
                    .invoke(() -> {
                        subscriptions.remove(String.valueOf(id));
                        logger.info("Session disconnect");
                    });
        } catch (Exception e) {
            subscriptions.remove("client-unique");
            final Long invocationNumber = counter.getAndIncrement();
            logger.errorf("EventSource#streamProjectsBySession() invocation #%d returning failure: %s | #%d timed out after %d ms", e.getMessage(), invocationNumber, invocationNumber, System.currentTimeMillis() - started);
            return Multi.createFrom().failure(e);
        }
    }

    @GET
    @Path("/projects")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<Project> streamProjects() {
        return projects;
    }
}
