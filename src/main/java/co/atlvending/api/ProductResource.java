package co.atlvending.api;

import co.atlvending.api.dto.ProductRequest;
import co.atlvending.domain.Product;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Lab 02 — product endpoints with Bean Validation.
 *   GET  /products     list
 *   POST /products     create (validated)
 */
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @GET
    public List<Product> all() {
        return Product.listAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(@Valid ProductRequest req) {
        Product p = new Product(req.sku(), req.name(), req.priceCents());
        p.persist();
        return Response.status(Response.Status.CREATED).entity(p).build();
    }
}
