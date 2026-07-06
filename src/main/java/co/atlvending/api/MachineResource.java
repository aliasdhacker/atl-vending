package co.atlvending.api;

import co.atlvending.api.dto.RestockRequest;
import co.atlvending.api.exception.MachineNotFoundException;
import co.atlvending.domain.Machine;
import co.atlvending.domain.MachineStatus;
import co.atlvending.service.InventoryService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Labs 02–09 — machine endpoints.
 *   GET    /machines            list (optional ?status= filter)
 *   GET    /machines/{id}       one, or 404
 *   POST   /machines            create
 *   POST   /machines/{id}/restock   OPERATOR only
 *   DELETE /machines/{id}       ADMIN only
 */
@Path("/machines")
@Produces(MediaType.APPLICATION_JSON)
public class MachineResource {

    @Inject
    InventoryService inventory;

    @GET
    public List<Machine> all(@QueryParam("status") MachineStatus status) {
        return status == null ? inventory.all() : inventory.byStatus(status);
    }

    @GET
    @Path("/{id}")
    public Machine one(@PathParam("id") Long id) {
        return inventory.find(id)
                .orElseThrow(() -> new MachineNotFoundException(id));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Machine machine) {
        Machine saved = inventory.create(machine);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @POST
    @Path("/{id}/restock")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("OPERATOR")
    public Response restock(@PathParam("id") Long id, @Valid RestockRequest req) {
        inventory.find(id).orElseThrow(() -> new MachineNotFoundException(id));
        inventory.restock(id, req);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response decommission(@PathParam("id") Long id) {
        if (!inventory.decommission(id)) {
            throw new MachineNotFoundException(id);
        }
        return Response.noContent().build();
    }
}
