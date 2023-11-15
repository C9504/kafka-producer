package org.c9504.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.File;


@Path("/")
public class IndexResource {

    @GET
    @Path("{path: .*}")
    public Response index() {
        // Devuelve el archivo HTML de tu aplicación React para manejar la ruta en el lado del cliente.
        return Response.ok(new File("src/main/resources/META-INF/resources/index.html")).build();
    }

}
