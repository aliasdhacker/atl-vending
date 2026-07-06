package co.atlvending.service;

import co.atlvending.domain.Machine;
import co.atlvending.domain.MachineStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Lab 05 — the service now reads/writes machines through Hibernate ORM +
 * Panache instead of an in-memory list. Writes are @Transactional.
 */
@ApplicationScoped
public class InventoryService {

    public List<Machine> all() {
        return Machine.listAll();
    }

    public List<Machine> byStatus(MachineStatus status) {
        return Machine.list("status", status);
    }

    public Optional<Machine> find(Long id) {
        return Optional.ofNullable(Machine.findById(id));
    }

    public long count() {
        return Machine.count();
    }

    @Transactional
    public Machine create(Machine m) {
        m.persist();
        return m;
    }
}
