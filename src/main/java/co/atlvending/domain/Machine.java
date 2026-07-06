package co.atlvending.domain;

/**
 * Lab 02 — a vending machine, modelled as a simple record for now.
 * (In Lab 05 this becomes a Panache entity.)
 */
public record Machine(Long id, String location, MachineStatus status) {
}
