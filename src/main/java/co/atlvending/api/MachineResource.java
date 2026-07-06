package co.atlvending.api;

import co.atlvending.api.exception.MachineNotFoundException;
import co.atlvending.domain.Machine;
import co.atlvending.domain.MachineStatus;
import co.atlvending.service.InventoryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * Lab 03 — the resource now delegates to {@link InventoryService} (injected)
 * instead of holding data inline.
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
}
