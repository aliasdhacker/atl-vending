package co.atlvending.service;

import co.atlvending.domain.Machine;
import co.atlvending.domain.MachineStatus;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

/**
 * Lab 03 — business logic extracted from the resource layer into a CDI bean.
 * Data is still in-memory; Lab 05 switches this to Panache/Postgres.
 */
@ApplicationScoped
public class InventoryService {

    private static final List<Machine> MACHINES = List.of(
            new Machine(1L, "Concourse A Gate 12", MachineStatus.ONLINE),
            new Machine(2L, "Concourse B Gate 8", MachineStatus.OFFLINE),
            new Machine(3L, "Downtown Lobby 1", MachineStatus.ONLINE));

    public List<Machine> all() {
        return MACHINES;
    }

    public List<Machine> byStatus(MachineStatus status) {
        return MACHINES.stream().filter(m -> m.status() == status).toList();
    }

    public Optional<Machine> find(Long id) {
        return MACHINES.stream().filter(m -> m.id().equals(id)).findFirst();
    }
}
