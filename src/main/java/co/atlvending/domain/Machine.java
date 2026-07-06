package co.atlvending.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * Lab 05 — Persistence with Panache (active-record style).
 * A vending machine deployed at a physical location.
 */
@Entity
@Table(name = "machines")
public class Machine extends PanacheEntity {

    public String location;

    @Enumerated(EnumType.STRING)
    public MachineStatus status;

    public Machine() {
    }

    public Machine(String location, MachineStatus status) {
        this.location = location;
        this.status = status;
    }
}
