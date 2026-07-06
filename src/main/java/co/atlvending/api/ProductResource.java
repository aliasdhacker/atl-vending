package co.atlvending.api;

import co.atlvending.domain.Product;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Lab 02 — POST /products with input validation. Inline storage for now.
 */
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    private final List<Product> products = new ArrayList<>(List.of(
            new Product("COKE-12", "Coca-Cola 12oz", 250),
            new Product("WATER-16", "Bottled Water", 150),
            new Product("CHIPS-LAYS", "Lays Original", 175)));

    @GET
    public List<Product> all() {
        return products;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@Valid Product product) {
        products.add(product);
        return Response.status(Response.Status.CREATED).entity(product).build();
    }
}
