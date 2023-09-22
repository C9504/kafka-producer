package org.c9504.resources;

import org.c9504.entities.Test;
import org.c9504.producers.TestStart;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.UUID;

@Path("/test")
public class TestResource {

    @Inject
    private TestStart testStart;

    @GET
    @Path("/start")
    @Produces(MediaType.APPLICATION_JSON)
    public Test setTestStart() {
        Test test = new Test(UUID.randomUUID(), "STARTED", "Description", Instant.now().toEpochMilli());
        testStart.sendTestStart(test);
        return test;
    }

}
