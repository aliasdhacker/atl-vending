package co.atlvending.api.exception;

/**
 * Lab 02 — thrown when a machine id does not resolve.
 */
public class MachineNotFoundException extends RuntimeException {

    public MachineNotFoundException(Long id) {
        super("Machine not found: " + id);
    }
}
