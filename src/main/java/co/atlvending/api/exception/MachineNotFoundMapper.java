package co.atlvending.api.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

/**
 * Lab 02 — turns {@link MachineNotFoundException} into a clean JSON 404.
 */
@Provider
public class MachineNotFoundMapper implements ExceptionMapper<MachineNotFoundException> {

    @Override
    public Response toResponse(MachineNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
    }
}
