package co.atlvending.api;

import co.atlvending.api.exception.MachineNotFoundException;
import co.atlvending.domain.Machine;
import co.atlvending.domain.MachineStatus;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * Lab 02 — REST endpoints with Jakarta REST. Data is held inline for now;
 * Lab 03 extracts this into an InventoryService.
 */
@Path("/machines")
@Produces(MediaType.APPLICATION_JSON)
public class MachineResource {

    private static final List<Machine> MACHINES = List.of(
            new Machine(1L, "Concourse A Gate 12", MachineStatus.ONLINE),
            new Machine(2L, "Concourse B Gate 8", MachineStatus.OFFLINE),
            new Machine(3L, "Downtown Lobby 1", MachineStatus.ONLINE));

    @GET
    public List<Machine> all() {
        return MACHINES;
    }

    @GET
    @Path("/{id}")
    public Machine one(@PathParam("id") Long id) {
        return MACHINES.stream()
                .filter(m -> m.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new MachineNotFoundException(id));
    }
}
