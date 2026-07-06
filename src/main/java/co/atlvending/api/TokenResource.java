package co.atlvending.api;

import io.quarkus.arc.profile.IfBuildProfile;
import io.smallrye.jwt.build.Jwt;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.Set;

/**
 * Lab 09 — DEV/TEST ONLY helper that mints a signed JWT so you can demo the
 * secured endpoints without external tooling:
 *
 *   TOKEN=$(curl -s "http://localhost:8080/dev/token?role=OPERATOR")
 *   curl -H "Authorization: Bearer $TOKEN" -X POST .../restock ...
 *
 * This bean only exists under the dev and test profiles — it is never built
 * into a prod image.
 */
@Path("/dev/token")
@IfBuildProfile(anyOf = {"dev", "test"})
public class TokenResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String token(@QueryParam("role") String role,
                        @QueryParam("user") String user) {
        String subject = (user == null || user.isBlank()) ? "demo-user" : user;
        String r = (role == null || role.isBlank()) ? "OPERATOR" : role;
        return Jwt.issuer("https://atl-vending/issuer")
                .upn(subject)
                .subject(subject)
                .groups(Set.of(r))
                .sign();
    }
}
