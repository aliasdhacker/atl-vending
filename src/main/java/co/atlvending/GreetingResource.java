package co.atlvending;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Lab 01 — Project Setup + Dev Mode.
 * The @Path was changed from "/hello" to "/welcome" to demonstrate hot reload.
 */
@Path("/welcome")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String welcome() {
        return "Welcome to Atlanta Vending Co.";
    }
}
